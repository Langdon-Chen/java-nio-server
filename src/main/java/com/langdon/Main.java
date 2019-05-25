package com.langdon;

import com.langdon.http.HttpMessageReader;
import com.langdon.http.HttpMessageWriter;
import com.langdon.http.HttpParser;
import com.langdon.server.Reactor;

public class Main  {
    public static void main(String[] args) throws Exception{
        int port = 8001;
        if (args.length == 1){
            port = Integer.valueOf(args[0]);
        }
        final HttpParser parser = new HttpParser();
        Reactor reactor = new Reactor(port,new HttpMessageReader(),new HttpMessageWriter(parser));
        new Thread(reactor).start();

//        ExecutorService workerPool =  Executors.newFixedThreadPool(16);
//        workerPool.submit(reactor);

    }
}
