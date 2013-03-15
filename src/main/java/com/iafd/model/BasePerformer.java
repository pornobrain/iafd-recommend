package com.iafd.model;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

import static com.iafd.Constants.IAFD_ENDPOINT;

@Entity(noClassnameStored = true)
abstract class BasePerformer {
	private static String URL_FORMAT = IAFD_ENDPOINT + "person.rme/perfid=%s/gender=%s/%s.htm";

	@Id	@Embedded private PerformerId id;
	String slug;

	BasePerformer() {}

	BasePerformer(String performerId, Gender gender, String slug) {
		this.id = new PerformerId(performerId, gender);
		this.slug = slug;
	}

	BasePerformer(BasePerformer performer) {
		this.id = performer.id;
		this.slug = performer.slug;
	}

	public PerformerId getId() {
		return id;
	}

	public String getUrl() {
		return String.format(URL_FORMAT, getPerformerId(), getGender().code(), slug);
	}

	public String getPerformerId() {
		return id.getId();
	}

	public Gender getGender() {
		return id.getGender();
	}
}
