package com.iafd.model;

public enum Gender {
	Male("m"), Female("f");
	private final String code;
	private Gender(String code) {
		this.code = code;
	}
	String code() {
		return code;
	}
}
