package com.iafd.performer.extract;

public class PerformerValidator {
	private final String content;

	PerformerValidator(String content) {
		this.content = content;
	}

	boolean isValid() {
		return content.contains("iafd.com - internet adult film database</title>")
			&& !content.contains("invalid or outdated page")
			&& !content.contains("404 -  iafd.com - internet adult film database");
	}
}
