package com.langdon.http;

import java.util.Map;

public class ServerHttpRequest extends AbstractHttpMessage implements HttpRequest{

    private HttpMethod method;

    private String requestUri;

    private Map<String,Object> attributes ;

    public HttpMethod getHttpMethod() {
        return null;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
