package com.iafd.proxylist;

import com.google.common.base.Objects;
import org.joda.time.DateTime;

import java.net.InetAddress;

public class ProxyServer {
	private final InetAddress address;
	private final int port;
	private final ProxyProtocol protocol;
	private final Anonimity anonymity;
	private final String country;
	private final String city;
	private final String hostname;
	private final DateTime checkDate;

	ProxyServer(InetAddress address, int port, ProxyProtocol protocol, Anonimity anonymity, String country, String city,
		String hostname, DateTime checkDate) {
		this.address = address;
		this.port = port;
		this.protocol = protocol;
		this.anonymity = anonymity;
		this.country = country;
		this.city = city;
		this.hostname = hostname;
		this.checkDate = checkDate;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public ProxyProtocol getProtocol() {
		return protocol;
	}

	public Anonimity getAnonymity() {
		return anonymity;
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

	public String getHostname() {
		return hostname;
	}

	public DateTime getCheckDate() {
		return checkDate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("address", address)
			.add("port", port)
			.add("protocol", protocol)
			.add("anonymity", anonymity)
			.add("country", country)
			.add("city", city)
			.add("hostname", hostname)
			.add("checkDate", checkDate)
			.toString();
	}
}
