package com.langdon.http;

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
        return getEntity()==null ? sb.toString() : sb.append(new String(getEntity())).toString();
    }
}
