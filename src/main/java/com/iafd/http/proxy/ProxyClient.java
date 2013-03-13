package com.iafd.http.proxy;

import com.google.inject.Inject;
import com.ning.http.client.*;
import react.AbstractSignal;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.google.common.base.Throwables.propagate;

public class ProxyClient<Context> extends AbstractSignal<ProxyClientResponse<Context>> {
	private final AsyncHttpClient client;
	private final Queue<ProxyServer> proxies;

	@Inject
	ProxyClient(AsyncHttpClient client, List<ProxyServer> proxies) {
		this.client = client;
		this.proxies = new ConcurrentLinkedQueue<>(proxies);
	}

	private void doSubmit(Context context, Request request) throws IOException {
		ProxyServer proxy = proxies.poll();
		client.prepareRequest(request)
			.setProxyServer(proxy)
			.setConnectionPoolKeyStrategy(new KeyStrategy(proxy))
			.execute(new ProxyHandler(context, request));
		proxies.add(proxy);
	}

	public void submit(Context context, Request request) {
		try {
			doSubmit(context, request);
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	private class ProxyHandler extends AsyncCompletionHandler<Void> {
		private final Context context;
		private final Request request;

		public ProxyHandler(Context context, Request request) {
			this.context = context;
			this.request = request;
		}

		@Override
		public void onThrowable(Throwable t) {
			super.onThrowable(t);
			if(!(t instanceof InterruptedException))
				submit(context, request);
		}

		@Override
		public Void onCompleted(Response response) throws Exception {
			notifyEmit(new ProxyClientResponse<>(context, response));
			return null;
		}

		@Override
		public STATE onBodyPartReceived(HttpResponseBodyPart content) throws Exception {
			return super.onBodyPartReceived(content);
		}
	}
}

class KeyStrategy implements ConnectionPoolKeyStrategy {
	private static final String KEY_FORMAT = "proxy-%s://%s:%d";
	private final ProxyServer proxy;

	KeyStrategy(ProxyServer proxy) {
		this.proxy = proxy;
	}

	@Override
	public String getKey(URI uri) {
		return String.format(
			KEY_FORMAT,
			proxy.getProtocolAsString(),
			proxy.getHost(),
			proxy.getPort());
	}
}

