package com.iafd.proxylist;

import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;

public class ProxyPageTest {
	private ProxyPage page;

	@Before
	public void before() throws IOException {
		page = new ProxyPage(Utils.getTestDocument());
	}

	@Test
	public void getEncryptionKey() {
		String key = page.getEncryptionKey();
		assertThat(key).startsWith("eval(function(p,r,o");
	}

	@Test
	public void getProxyLines() {
		Elements lines = page.getProxyRows();
		assertThat(lines).hasSize(100);
	}
}
