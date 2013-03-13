package com.iafd.model;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.validation.ValidationExtension;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

import static com.google.common.base.Throwables.propagate;

public class DatastoreBuilder {
	private Morphia morphia;
	private Mongo mongo;
	private String db = "iafd";

	public DatastoreBuilder setMorphia(Morphia morphia) {
		this.morphia = morphia;
		return this;
	}

	public DatastoreBuilder setMongo(Mongo mongo) {
		this.mongo = mongo;
		return this;
	}

	public DatastoreBuilder setDb(String db) {
		this.db = db;
		return this;
	}

	public Datastore build() {
		Morphia morphia = this.morphia;
		if(morphia == null)
			morphia = new Morphia().mapPackage("com.iafd.model");
		Mongo mongo = this.mongo;
		if(mongo == null)
			mongo = createDefaultMongo();
		new ValidationExtension(morphia);
		return morphia.createDatastore(mongo, db);
	}

	private static Mongo createDefaultMongo() {
		try {
			return new Mongo();
		} catch (UnknownHostException e) {
			throw propagate(e);
		}
	}
}
