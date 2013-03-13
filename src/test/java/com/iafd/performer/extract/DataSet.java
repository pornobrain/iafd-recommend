package com.iafd.performer.extract;

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

class DataSet {
	static final List<RawPerformer> performers;
	static {
		Reader r = resource("dataset.json");
		List<RawPerformer> result = new Gson().fromJson(r, new TypeToken<List<RawPerformer>>() {}.getType());
		performers = ImmutableList.copyOf(result);
		try {
			r.close();
		} catch (IOException e) {
			throw propagate(e);
		}
	}

	static final RawPerformer TEST_PERFORMER = performers.get(0);
	static final String TEST_CONTENT = TEST_PERFORMER.getContent();
	static final Document TEST_DOCUMENT;
	static {
		TEST_DOCUMENT = Jsoup.parse(TEST_CONTENT, TEST_PERFORMER.getUrl());
	}

	private static Reader resource(String name) {
		try {
			return new InputStreamReader(DataSet.class.getResourceAsStream(name), Charsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw propagate(e);
		}
	}
}
