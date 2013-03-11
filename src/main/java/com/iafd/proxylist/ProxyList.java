package com.iafd.proxylist;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Throwables.propagate;

public class ProxyList {
	static final String ENDPOINT = "http://spys.ru/en/";

	private final ProxyPage page;

	public ProxyList() {
		Document doc;
		try {
			doc = Jsoup.connect(ENDPOINT)
				.data(
					"tldc", "0",
					"eport", "",
					"anmm", "0",
					"vlast", "0",
					"submit", "Proxy search")
				.post();
		} catch (IOException e) {
			throw propagate(e);
		}
		page = new ProxyPage(doc);
	}

	ProxyList(Document doc) {
		page = new ProxyPage(doc);
	}

	public List<ProxyServer> get() {
		Decryptor decryptor = new Decryptor(page.getEncryptionKey());
		List<ProxyServer> ps = ImmutableList.copyOf(Lists.transform(page.getProxyRows(), toProxyServer(decryptor)));
		decryptor.close();
		return ps;
	}

	private Function<Element, ProxyServer> toProxyServer(Decryptor decryptor) {
		return new ToProxyServer(decryptor);
	}

	public static void main(String[] args) {
		List<ProxyServer> ps = new ProxyList().get();
		for(ProxyServer proxy: ps)
			System.out.println(proxy);
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
