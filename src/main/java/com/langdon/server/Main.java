package com.langdon.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {
    public static void main(String[] args) throws Exception{
        Reactor reactor = new Reactor(8080);

        ExecutorService workerPool =  Executors.newFixedThreadPool(16);
        workerPool.submit(reactor);

    }
}
