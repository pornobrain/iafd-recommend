package com.iafd.proxylist;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.google.common.base.Throwables.propagate;

class Decryptor {
	private static final String PACKER_FILENAME = "p_a_c_k_e_r_unpacker.js";

	private final Context cx = Context.enter();
	private final ScriptableObject scope = cx.initStandardObjects();
	{
		Reader script = new InputStreamReader(Decryptor.class.getResourceAsStream(PACKER_FILENAME));
		evaluate(script, PACKER_FILENAME);
	}

	Decryptor(String encryptionKey) {
		scope.put("encryptionKey", scope, encryptionKey);
		evaluate("eval(P_A_C_K_E_R.unpack(encryptionKey))", "<unpack encryption key>");
	}

	int decrypt(String encryptedPort) {
		return Integer.valueOf(Context.toString(evaluate("''+"+encryptedPort, "<decrypt port>")));
	}

	void close() {
		Context.exit();
	}

	private Object evaluate(Reader reader, String sourceName) {
		try {
			return cx.evaluateReader(scope, reader, sourceName, 1, null);
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	private Object evaluate(String script, String sourceName) {
		return cx.evaluateString(scope, script, sourceName, 1, null);
	}
}
