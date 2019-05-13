package com.langdon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @see "http://tutorials.jenkov.com/java-nio/index.html"
 *
 */
public class Reactor implements Runnable {
    private Logger log = Logger.getLogger(this.getClass().getName());

    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    Reactor(int port) throws IOException{
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(
                new InetSocketAddress(port)
        );
        serverSocket.configureBlocking(false);
        // socketChannel + selector = selectionKey ; socketChannel ~= socket ;
        // socketChannel includes the inputStream , 交给 thread 处理
        SelectionKey sk = serverSocket.register(selector,SelectionKey.OP_ACCEPT); // 接受链接
        sk.attach(new Acceptor());
        log.info("server listening on port : "+ port);
        log.info("server SelectionKey : " + sk);
    }
    /* Alternatively, use explicit SPI provider:
    SelectorProvider p = SelectorProvider.provider();
    selector = p.openSelector();
    serverSocket = p.openServerSocketChannel(); */
    public void run() {
        try{
            while (!Thread.interrupted()){
                if (selector.select() > 0 ){// get the ready socketKeys
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator it = selectedKeys.iterator();
                    while (it.hasNext()){
                        dispatch((SelectionKey) it);
//                        it.remove();  // FIXME 需要吗？
                    }
                    selectedKeys.clear();
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private void dispatch(SelectionKey k) {
//        System.out.println(2);
        Runnable r = (Runnable)(k.attachment()); // 在 Acceptor.handler 处进行 attach
        if (r != null)
            r.run();
    }

    class Acceptor implements Runnable{
        public void run(){
            try {
                SocketChannel socketChannel = serverSocket.accept();
                // In non-blocking mode the accept() method returns immediately, and may thus return null, if no incoming connection had arrived.
                if (socketChannel != null){
                    if (socketChannel.isConnected()){
                        log.info(socketChannel + " has connected");
                    }
                    new Handler(selector,socketChannel); // 注册Channel到Selector
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

}
