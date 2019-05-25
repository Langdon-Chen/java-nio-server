package com.langdon.http;

import com.langdon.http.basic.*;
import com.langdon.server.IMessageWriter;
import com.langdon.server.MessageBuffer;
import com.langdon.server.MessageConst;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

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
        ServerHttpResponse httpResponse = new ServerHttpResponse();
        httpResponse.setHeaders(new HashMap<String, String>());
        httpResponse.setEntity(httpRequest.toString());
        StringBuilder writer = new StringBuilder();

        writer.append(HttpVersion.VERSION_1_1);
        writer.append((char) Character.SPACE_SEPARATOR);
        writer.append(HttpStatus.OK.value());
        writer.append((char) Character.SPACE_SEPARATOR);
        writer.append(HttpStatus.OK.getReasonPhrase());
        writer.append(HttpHeaders.CRLF);

        writer.append(HttpHeaders.CONNECTION);
        writer.append((char) Character.SPACE_SEPARATOR);
        writer.append("close");
        writer.append(HttpHeaders.CRLF);

        writer.append(HttpHeaders.CONTENT_LENGTH);
        writer.append((char) Character.SPACE_SEPARATOR);
        if (httpResponse.getEntity()!=null){
            writer.append(httpResponse.getEntity().length);
        }else {
            writer.append(0);
        }
        writer.append(HttpHeaders.CRLF);
        writer.append(HttpHeaders.CRLF);
        writer.append(httpRequest.toString());

        ByteBuffer buffer = ByteBuffer.wrap(writer.toString().getBytes(StandardCharsets.UTF_8));
        write(socketChannel,buffer);
        return MessageConst.WRITE_COMPLETE;
    }

    private int write(SocketChannel socketChannel,ByteBuffer byteBuffer) throws IOException{
        int bytesWritten      = socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while(bytesWritten > 0 && byteBuffer.hasRemaining()){
            bytesWritten = socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }
}
