package com.langdon.http.basic;

import com.langdon.http.AbstractHttpMessage;

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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getMethod())
                .append(' ')
                .append(getRequestUri())
                .append(' ')
                .append(getHttpVersion())
                .append((char)Character.LINE_SEPARATOR);
        for (Map.Entry<String ,String > entry : getHeaders().entrySet()){
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append('\r')
                    .append('\n');
        }
        sb.append((char)Character.LINE_SEPARATOR);
        if (getEntity()!=null){
            sb.append(new String(getEntity()));
        }
        return sb.toString();
    }
}
