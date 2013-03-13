package com.iafd;

import com.github.jmkgreen.morphia.Datastore;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.iafd.http.proxy.list.ProxyList;
import com.iafd.model.DatastoreBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ProxyServer;

import java.util.List;

public class BaseModule extends AbstractModule {
	protected void configure() {
		bind(Bootstrap.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	AsyncHttpClient provideAsyncHttpClient() {
		return new AsyncHttpClient(
			new AsyncHttpClientConfig.Builder()
				.setFollowRedirects(true)
				.build()
		);
	}

	@Provides
	@Singleton
	List<ProxyServer> provideProxies(ProxyList proxyList) {
		return proxyList.get();
	}

	@Provides
	@Singleton
	Datastore provideDatastore() {
		return new DatastoreBuilder().build();
	}
}
