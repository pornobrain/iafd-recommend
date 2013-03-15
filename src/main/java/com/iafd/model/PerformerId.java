package com.iafd.model;

import com.google.common.base.Objects;

public class PerformerId {
	private PerformerId() {}
	PerformerId(String id, Gender gender) {
		this.id = id;
		this.gender = gender;
	}

	private String id;
	private Gender gender;

	public String getId() {
		return id;
	}

	public Gender getGender() {
		return gender;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		PerformerId that = (PerformerId) o;

		if(gender != that.gender) return false;
		if(!id.equals(that.id)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, gender);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("gender", gender)
			.toString();
	}
}
