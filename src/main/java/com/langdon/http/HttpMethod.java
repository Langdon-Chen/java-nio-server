package com.langdon.http;

public enum HttpMethod {
	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
	
	@Override
	public String toString()
	{
		return this.name();
	}

	public static HttpMethod extractMethod(String headerLine) throws IllegalArgumentException
	{
		String method = headerLine.split(" ")[0];
		if (method != null) {
			return HttpMethod.valueOf(method);
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}
