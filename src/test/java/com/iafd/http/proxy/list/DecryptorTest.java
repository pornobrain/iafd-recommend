package com.iafd.http.proxy.list;

import com.google.common.io.CharStreams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.fest.assertions.api.Assertions.assertThat;

public class DecryptorTest {
	private static final String ENCRYPTION_KEY_FILE = "encryption-key.js";
	private Decryptor decryptor;

	@Before
	public void before() throws IOException {
		String encryptionKey = CharStreams.toString(new InputStreamReader(DecryptorTest.class.getResourceAsStream(ENCRYPTION_KEY_FILE)));
		decryptor = new Decryptor(encryptionKey);
	}

	@After
	public void after() {
		decryptor.close();
	}

	@Test
	public void decrypt() {
		int port = decryptor.decrypt("(Three2NineNine^Nine9Nine)+(Five7FourOne^SevenFourSeven)+(Seven6EightSeven^Zero9Eight)+(Zero0TwoThree^Two4Two)");
		assertThat(port).isEqualTo(3128);
	}
}
