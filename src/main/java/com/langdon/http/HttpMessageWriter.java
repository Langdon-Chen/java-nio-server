package com.langdon.http;

import com.langdon.server.Handler;
import com.langdon.server.IMessageWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpMessageWriter implements IMessageWriter {
    private HttpParser httpParser;

    public HttpMessageWriter(HttpParser parser){
        this.httpParser = parser;
    }

    @Override
    public void write(Handler handler) throws IOException {
        // todo 根据 content-length 判断当前message是否已经读完了；读完了就开始回复 ; 现在已经读完了
        // check
        byte[] src = handler.getBytesHasRead();
        String str = new String(src);
        System.out.println(str);
        InputStream in = new ByteArrayInputStream(src);
        // 根据 content-length 来解析，仅解析在content-length以内的内容；
        // 如果读取到的body 长度少于 content-length ， response400
        ServerHttpRequest httpRequest = httpParser.parseRequest(in);
        if (httpRequest != null){
            // send response
        }else {
            // error ;
        }
    }
}
