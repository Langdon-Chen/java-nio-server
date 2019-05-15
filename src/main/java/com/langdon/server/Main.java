package com.langdon.server;

import com.langdon.http.HttpMessageReader;
import com.langdon.http.HttpMessageWriter;
import com.langdon.http.HttpParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {
    public static void main(String[] args) throws Exception{
        final HttpParser parser = new HttpParser();
        Reactor reactor = new Reactor(8080,new HttpMessageReader(),new HttpMessageWriter(parser));
        new Thread(reactor).start();

//        ExecutorService workerPool =  Executors.newFixedThreadPool(16);
//        workerPool.submit(reactor);


    }

    public static void  test() throws Exception{
//        final HttpParser parser = new HttpParser();
//        HttpMessageReader reader = new HttpMessageReader(parser);
//        reader.hasReadCompletely(src.getBytes());
    }

    public static String src = "GET / HTTP/1.1\r\n" +
            "Content-Type: text/plain\r\n" +
            "User-Agent: PostmanRuntime/7.11.0\r\n" +
            "Accept: */*\r\n" +
            "Cache-Control: no-cache\n" +
            "Postman-Token: 780df59c-339e-48ea-964f-b419b4b0a0d3\r\n" +
            "Host: localhost:8080\n" +
            "cookie: JSESSIONID=937888D24EC60E7BBBB2676FF7A105BF\r\n" +
            "accept-encoding: gzip, deflate\r\n" +
            "content-length: 5999\r\n" +
            "Connection: keep-alive\r\n" +
            "\r\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a very large body ;\n" +
            "a";
}
