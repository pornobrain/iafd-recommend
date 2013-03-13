package com.iafd.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity
public class ParsedPerformer implements Performer {
	@Id private String id;
	@Reference
	private RawPerformer rawPerformer;

	private ParsedPerformer() {}

	private ParsedPerformer(RawPerformer rawPerformer) {
		this.id = rawPerformer.getId();
		this.rawPerformer = rawPerformer;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUrl() {
		return rawPerformer.getUrl();
	}
}
