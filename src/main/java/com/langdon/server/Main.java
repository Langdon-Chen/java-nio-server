package com.langdon.server;

import com.langdon.http.HttpMessageReader;
import com.langdon.http.HttpMessageWriter;
import com.langdon.http.HttpParser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {
    public static void main(String[] args) throws Exception{
        Reactor reactor = new Reactor(8080,new HttpMessageReader(),new HttpMessageWriter(new HttpParser()));

        new Thread(reactor).start();
//        ExecutorService workerPool =  Executors.newFixedThreadPool(16);
//        workerPool.submit(reactor);

    }
}
