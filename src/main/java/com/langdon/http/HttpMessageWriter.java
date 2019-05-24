package com.langdon.http;

import com.langdon.http.basic.ServerHttpRequest;
import com.langdon.server.IMessageWriter;
import com.langdon.server.MessageBuffer;

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
    public boolean write(SocketChannel socketChannel , MessageBuffer messageBuffer) throws IOException {
        if (!messageBuffer.hasMessage())
            return false;

        byte[] src = messageBuffer.getBytesHasRead();
        InputStream in = new ByteArrayInputStream(src);
        ServerHttpRequest httpRequest = httpParser.parse(in);
//        System.out.println(httpRequest.toString());
        return true;
    }
}
