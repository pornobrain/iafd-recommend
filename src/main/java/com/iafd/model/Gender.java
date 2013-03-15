package com.iafd.model;

public enum Gender {
	Male("m"), Female("f");
	private final String code;
	private Gender(String code) {
		this.code = code;
	}
	public String code() {
		return code;
	}
}
