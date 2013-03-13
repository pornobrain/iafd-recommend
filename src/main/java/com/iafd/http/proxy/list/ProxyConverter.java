package com.iafd.http.proxy.list;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

final class ProxyConverter {
	private static Set<ProxyProtocol> ACCEPTED_PROTOCOLS = ImmutableSet.of(ProxyProtocol.HTTP, ProxyProtocol.HTTPS);

	private static Predicate<ProxyServer> HTTP_ONLY = new Predicate<ProxyServer>() {
		@Override
		public boolean apply(ProxyServer input) {
			return ACCEPTED_PROTOCOLS.contains(input.getProtocol());
		}
	};

	private static Function<ProxyServer, com.ning.http.client.ProxyServer> TO_PROXY =
		new Function<ProxyServer, com.ning.http.client.ProxyServer>() {
			@Override
			public com.ning.http.client.ProxyServer apply(ProxyServer input) {
				return new com.ning.http.client.ProxyServer(
					toProtocol(input.getProtocol()),
					input.getAddress().getHostAddress(),
					input.getPort());
			}
		};

	private static com.ning.http.client.ProxyServer.Protocol toProtocol(ProxyProtocol in) {
		switch(in) {
			case HTTP:
				return com.ning.http.client.ProxyServer.Protocol.HTTP;
			case HTTPS:
				return com.ning.http.client.ProxyServer.Protocol.HTTPS;
			default:
				return null;
		}
	}

	static List<com.ning.http.client.ProxyServer> convert(List<ProxyServer> proxies) {
		return ImmutableList.copyOf(transform(filter(
			proxies,
			HTTP_ONLY),
			TO_PROXY));
	}
}
