package com.iafd.http.proxy;

import com.ning.http.client.Response;

public class ProxyClientResponse<Context> {
	public final Context context;
	public final Response response;

	public ProxyClientResponse(Context context, Response response) {
		this.context = context;
		this.response = response;
	}
}
