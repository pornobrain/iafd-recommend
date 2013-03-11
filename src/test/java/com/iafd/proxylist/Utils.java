package com.iafd.proxylist;

import com.google.common.base.Charsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Throwables.propagate;

class Utils {
	private static final String TEST_PAGE_FILENAME = "page.html";

	static Document getTestDocument() {
		InputStream in = Utils.class.getResourceAsStream(TEST_PAGE_FILENAME);
		try {
			return Jsoup.parse(in, Charsets.UTF_8.name(), ProxyList.ENDPOINT);
		} catch (IOException e) {
			throw propagate(e);
		}
	}
}
