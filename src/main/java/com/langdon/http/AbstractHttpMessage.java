package com.langdon.http;

import com.langdon.http.basic.HttpMessage;
import com.langdon.http.basic.HttpVersion;

import java.util.Map;

public abstract class AbstractHttpMessage implements HttpMessage {

    private HttpVersion version;

    private byte[] entity = null;

    private Map<String, String> headers;

    public HttpVersion getHttpVersion() {
        // TODO Auto-generated method stub
        return version;
    }

    public Map<String, String> getHeaders() {
        // TODO Auto-generated method stub
        return headers;
    }

    public byte[] getEntity() {
        // TODO Auto-generated method stub
        return entity;
    }

    public void setVersion(HttpVersion version) {
        this.version = version;
    }

    public void setEntity(byte[] entity) {
        this.entity = entity;
    }

    public void setEntity(String entity){
        this.entity = entity.getBytes();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String name,String value){
        this.headers.put(name,value);
    }

}
