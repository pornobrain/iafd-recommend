package com.iafd.proxylist;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ProxyPage {
	private final Document doc;

	ProxyPage(Document doc) {
		this.doc = doc;
	}

	private static final Predicate<Element> STARTS_WITH_EVAL = new Predicate<Element>() {
		@Override
		public boolean apply(Element input) {
			return input.data().startsWith("eval(");
		}
	};

	String getEncryptionKey() {
		return Iterables.find(doc.select("script[type=text/javascript]"), STARTS_WITH_EVAL).data();
	}

	Elements getProxyRows() {
		return doc.select(".spy1x:has(font.spy14),.spy1xx:has(font.spy14)");
	}
}

class ProxyRowParser {
	private static final Pattern PORT_PATTERN = Pattern.compile("\\+(.*)\\)$");
	private static final Pattern CITY_PATTERN = Pattern.compile("^\\((.*)\\)$");

	private final Element row;
	private final Elements cols;

	ProxyRowParser(Element row) {
		this.row = row;
		cols = row.select("td");
	}

	String getAddress() {
		return row.select(".spy14:has(script)").text();
	}

	String getEncryptedPort() {
		Matcher m = PORT_PATTERN.matcher(row.select("script").get(0).data());
		m.find();
		return m.group(1);
	}

	String getProtocol() {
		return cols.get(1).text();
	}

	public String getAnonimity() {
		return cols.get(2).text();
	}

	public String getCountry() {
		return cols.get(3).select(">font").get(0).ownText();
	}

	public String getCity() {
		Matcher m = CITY_PATTERN.matcher(cols.get(3).select(".spy1").text());
		if(m.find())
			return m.group(1);
		return "";
	}

	public String getHostname() {
		return cols.get(4).text();
	}

	public String getCheckDate() {
		return cols.get(5).text();
	}
}
