package com.langdon.http.basic;

import com.langdon.http.AbstractHttpMessage;

import java.util.Map;

public class ServerHttpResponse extends AbstractHttpMessage implements HttpResponse{

    private HttpStatus httpStatus;


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ServerHttpResponse(){
        this.httpStatus = HttpStatus.OK;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getHttpVersion())
                .append((char) Character.SPACE_SEPARATOR)
                .append(httpStatus.value())
                .append((char)Character.SPACE_SEPARATOR)
                .append(httpStatus.getReasonPhrase())
                .append((char)Character.LINE_SEPARATOR);
        for (Map.Entry<String ,String > entry : getHeaders().entrySet()){
            sb.append(entry.getKey())
                    .append((char)Character.SPACE_SEPARATOR)
                    .append(entry.getValue())
                    .append((char)Character.LINE_SEPARATOR)
                    .append((char)Character.LINE_SEPARATOR);
        }
        sb.append((char)Character.LINE_SEPARATOR);
        if (getEntity()!=null){
            sb.append(new String(getEntity()));
        }
        return sb.toString();
    }
}
