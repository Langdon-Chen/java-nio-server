package com.langdon.http;

import com.langdon.http.basic.ServerHttpRequest;
import com.langdon.server.IMessageWriter;
import com.langdon.server.MessageBuffer;
import com.langdon.server.MessageConst;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SocketChannel;

public class HttpMessageWriter implements IMessageWriter {

    private HttpParser httpParser;

    public HttpMessageWriter(HttpParser parser){
        this.httpParser = parser;
    }

    @Override
    public int write(SocketChannel socketChannel , MessageBuffer messageBuffer) throws IOException {
        if (messageBuffer == null || !messageBuffer.hasMessage()){
            // see https://tools.ietf.org/html/rfc2616#section-4.4
            // handling bad request
            return MessageConst.WRITE_ERROR;
        }


        byte[] src = messageBuffer.getBytesHasRead();
        InputStream in = new ByteArrayInputStream(src);
        ServerHttpRequest httpRequest = httpParser.parse(in);
//        System.out.println(httpRequest.toString());
        return MessageConst.WRITE_COMPLETE;
    }
}
