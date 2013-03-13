package com.iafd.performer;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iafd.model.RawPerformer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.google.common.base.Throwables.propagate;

public final class PerformerDataSet {
	private PerformerDataSet() {}

	public static final List<RawPerformer> DATASET;
	static {
		Reader r = resource("dataset.json");
		List<RawPerformer> result = new Gson().fromJson(r, new TypeToken<List<RawPerformer>>() {}.getType());
		DATASET = ImmutableList.copyOf(result);
		try {
			r.close();
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	public static final RawPerformer PERFORMER = DATASET.get(0);
	public static final String CONTENT = PERFORMER.getContent();
	public static final Document DOCUMENT;
	static {
		DOCUMENT = Jsoup.parse(CONTENT, PERFORMER.getUrl());
	}

	private static Reader resource(String name) {
		try {
			return new InputStreamReader(PerformerDataSet.class.getResourceAsStream(name), Charsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw propagate(e);
		}
	}
}
