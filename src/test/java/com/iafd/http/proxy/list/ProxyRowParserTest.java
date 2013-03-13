package com.iafd.http.proxy.list;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.iafd.http.proxy.list.Utils.getTestDocument;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class ProxyRowParserTest {
	private ProxyRowParser parser;

	@Before
	public void before() {
		Element testRow = new ProxyPage(getTestDocument()).getProxyRows().get(0);
		parser = new ProxyRowParser(testRow);
	}

	@Test
	public void getAddress() {
		try {
			InetAddress.getByName(parser.getAddress());
		} catch (UnknownHostException e) {
			fail("Can't parse ip", e);
		}
	}

	@Test
	public void getEncryptedPort() {
		assertThat(parser.getEncryptedPort()).contains("^");
	}

	@Test
	public void getProtocol() {
		assertThat(parser.getProtocol()).isIn("HTTP", "HTTPS", "SOCKS5");
	}

	@Test
	public void getAnonymity() {
		assertThat(parser.getAnonimity()).isIn("NOA", "ANM", "HIA");
	}

	@Test
	public void getCountry() {
		assertThat(parser.getCountry()).isNotEmpty();
	}

	@Test
	public void getCity() {
		assertThat(parser.getCity()).isNotNull();
	}

	@Test
	public void getHostname() {
		assertThat(parser.getHostname()).contains(".");
	}

	@Test
	public void getCheckDate() {
		assertThat(parser.getCheckDate()).matches("\\d{2}-[a-z]{3}-\\d{4} \\d{2}:\\d{2}");
	}
}
