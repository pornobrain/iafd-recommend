package com.iafd.performer.extract;

import com.github.jmkgreen.morphia.Datastore;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.iafd.BaseModule;
import com.iafd.http.proxy.ProxyClient;
import com.iafd.http.proxy.ProxyClientResponse;
import com.iafd.model.Performer;
import com.iafd.model.RawPerformer;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import react.Slot;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Throwables.propagate;

public class PerformerExtractService extends AbstractIdleService {
	private static final int BATCH_SIZE = 100;
	private static final int RETRIES = 100;

	private final AsyncHttpClient client;
	private final ProxyClient<PerformerExtractCtx> proxyClient;
	private final Datastore datastore;
	private final Slot<ProxyClientResponse<PerformerExtractCtx>> slot = new Slot<ProxyClientResponse<PerformerExtractCtx>>() {
		@Override
		public void onEmit(ProxyClientResponse<PerformerExtractCtx> event) {
			try {
				handleResponse(event.context, event.response);
			} catch (IOException e) {
				throw propagate(e);
			}
		}
	};
	private final AtomicInteger queueSize = new AtomicInteger();
	private final Iterator<RawPerformer> iterator;
	private final CountDownLatch latch = new CountDownLatch(1);
	private final Logger logger = LoggerFactory.getLogger(PerformerExtractService.class);

	@Inject
	PerformerExtractService(AsyncHttpClient client, ProxyClient<PerformerExtractCtx> proxyClient, Datastore datastore) {
		this.client = client;
		this.proxyClient = proxyClient;
		this.datastore = datastore;
		iterator = datastore.find(RawPerformer.class).field("content").doesNotExist()
			.iterator();
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new BaseModule());
		final PerformerExtractService service = injector.getInstance(PerformerExtractService.class);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				service.stop();
			}
		});
		service.startAndWait();
		service.awaitShutdown();
	}

	private void handleResponse(PerformerExtractCtx ctx, Response response) throws IOException {
		if(isValid(response)) {
			ctx.performer.setContent(content(response));
			save(ctx.performer);
		}	else
			handleError(ctx, response);
	}

	private void submit(RawPerformer p, int retry) {
		Request request = new RequestBuilder("GET")
			.setUrl(p.getUrl())
			.build();
		proxyClient.submit(new PerformerExtractCtx(p, retry), request);
	}

	private void save(Performer performer) {
		datastore.save(performer);
		if(queueSize.decrementAndGet() < BATCH_SIZE / 2)
			batchSubmit();
	}

	private void handleError(PerformerExtractCtx ctx, Response response) {
		logger.info("invalid url:{} id:{} code:{}", ctx.performer.getUrl(), ctx.performer.getId(), response.getStatusCode());
		if(ctx.retry < RETRIES)
			submit(ctx.performer, ctx.retry+1);
		else {
			logger.info("give up url:{} id:{} code:{}", ctx.performer.getUrl(), ctx.performer.getId(), response.getStatusCode());
			if(queueSize.decrementAndGet() < BATCH_SIZE / 2)
				batchSubmit();
		}
	}

	private boolean isValid(Response response) {
		return new PerformerValidator(content(response)).isValid();
	}

	private static String content(Response response) {
		try {
			return response.getResponseBody("UTF-8");
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	@Override
	protected void startUp() throws Exception {
		proxyClient.connect(slot);
		batchSubmit();
	}

	private void batchSubmit() {
		logger.info("Submitting next batch...");
		try {
			for(int i=0;i<BATCH_SIZE && iterator.hasNext();i++) {
				queueSize.incrementAndGet();
				submit(iterator.next(), 1);
			}
		} catch(RuntimeException e) {
			e.printStackTrace();
			stop();
		}
		if(!iterator.hasNext() && queueSize.get() == 0)
			stop();
	}

	@Override
	protected void shutDown() throws Exception {
		client.close();
		latch.countDown();
	}

	public void awaitShutdown() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw propagate(e);
		}
	}
}

class PerformerExtractCtx {
	public final RawPerformer performer;
	public final int retry;

	PerformerExtractCtx(RawPerformer performer, int retry) {
		this.performer = performer;
		this.retry = retry;
	}
}
