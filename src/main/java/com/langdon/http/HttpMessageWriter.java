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

    /**
     *
     * @param socketChannel the channel to write
     * @param messageBuffer the message has read before
     * @return
     *   {@link MessageConst#WRITE_COMPLETE} (0) write successfully and it is time closing the channel
     *   {@link MessageConst#WAITE_FOR_NEXT_ROUND} (1) write successfully and please do not close the channel
     *   {@link MessageConst#WRITE_ERROR} (-1) write error due to bad request .
     * @throws IOException
     */
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
