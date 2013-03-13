package com.iafd.model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.base.Objects;

import static com.iafd.Constants.IAFD_ENDPOINT;

@Entity
public class RawPerformer implements Performer {
	private static String URL_FORMAT = IAFD_ENDPOINT + "person.rme/perfid=%s/gender=%s/%s.htm";
	private static String ID_FORMAT = "%s:%s:%s";

	@Id private	String id;
	private String performerId;
	private Gender gender;
	private String slug;
	private String content;

	private RawPerformer() {

	}

	RawPerformer(String performerId, Gender gender, String slug) {
		this.performerId = performerId;
		this.gender = gender;
		this.slug = slug;
		this.id = String.format(ID_FORMAT, performerId, gender, slug);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUrl() {
		return String.format(URL_FORMAT, performerId, gender.code(), slug);
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
			.add("id", id)
			.add("perFormerId", performerId)
			.add("gender", gender)
			.add("slug", slug)
			.toString();
	}
}
