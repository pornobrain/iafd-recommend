package com.iafd.model;

import com.google.common.base.Objects;

public class RawPerformer extends Performer {
	private String content;

	private RawPerformer() {}

	public RawPerformer(String performerId, Gender gender, String slug) {
		super(performerId, gender, slug);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", getId())
			.add("slug", slug)
			.add("content", content.isEmpty()?"<empty>":"<not empty>")
			.toString();
	}
}

