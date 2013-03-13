package com.iafd.http.proxy.list;

public enum Anonimity {
	NotAnonymous, Anonymous, HighlyAnonymous;
	static Anonimity forCode(String code) {
		if(code.equals("NOA"))
			return NotAnonymous;
		if(code.equals("ANM"))
			return Anonymous;
		if(code.equals("HIA"))
			return HighlyAnonymous;
		assert false: "Should not reach here";
		return null;
	}
}
