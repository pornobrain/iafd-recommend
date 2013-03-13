package com.iafd.http.proxy.list;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Throwables.propagate;

public class ProxyList extends AsyncCompletionHandler<Document> {
	static final String ENDPOINT = "http://spys.ru/en/";
	private static final String CHARSET_NAME = Charset.forName("CP1251").name();

	private final ProxyPage page;

	@Inject
	public ProxyList(AsyncHttpClient client) {
		Document doc;
		try {
			doc = client.preparePost(ENDPOINT)
				.addParameter("tldc", "0")
				.addParameter("eport", "")
				.addParameter("anmm", "0")
				.addParameter("vlast", "0")
				.addParameter("submit", "Proxy search")
				.execute(this).get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw propagate(e);
		}
		page = new ProxyPage(doc);
	}

	ProxyList(Document doc) {
		page = new ProxyPage(doc);
	}

	public List<com.ning.http.client.ProxyServer> get() {
		Decryptor decryptor = new Decryptor(page.getEncryptionKey());
		List<ProxyServer> ps = ImmutableList.copyOf(Lists.transform(page.getProxyRows(), toProxyServer(decryptor)));
		decryptor.close();
		return ProxyConverter.convert(ps);
	}

	private Function<Element, ProxyServer> toProxyServer(Decryptor decryptor) {
		return new ToProxyServer(decryptor);
	}

	@Override
	public Document onCompleted(Response response) throws Exception {
		InputStream stream = response.getResponseBodyAsStream();
		Document result = Jsoup.parse(stream, CHARSET_NAME, ENDPOINT);
		stream.close();
		return result;
	}
}

class ToProxyServer implements Function<Element, ProxyServer> {
	private static final DateTimeZone SERVER_TIMEZONE = DateTimeZone.forOffsetHours(4);
	private static final DateTimeFormatter DATE_FORMAT =
		DateTimeFormat.forPattern("dd-MMM-yy HH:mm").withLocale(Locale.ENGLISH).withZone(SERVER_TIMEZONE);

	private final Decryptor decryptor;

	ToProxyServer(Decryptor decryptor) {
		this.decryptor = decryptor;
	}

	@Override
	public ProxyServer apply(Element input) {
		ProxyRowParser p = new ProxyRowParser(input);
		return new ProxyServer(
			addressByName(p.getAddress()),
			decryptor.decrypt(p.getEncryptedPort()),
			ProxyProtocol.valueOf(p.getProtocol()),
			Anonimity.forCode(p.getAnonimity()),
			p.getCountry(),
			p.getCity(),
			p.getHostname(),
			parseDate(p.getCheckDate())
		);
	}

	private static DateTime parseDate(String date) {
		return DATE_FORMAT.parseDateTime(date);
	}

	private static InetAddress addressByName(String name) {
		try {
			return InetAddress.getByName(name);
		} catch (UnknownHostException e) {
			throw propagate(e);
		}
	}
}
