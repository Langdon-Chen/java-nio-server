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
    private final IMessageReader messageReader;
    private final IMessageWriter messageWriter;

    public Reactor(int port , IMessageReader messageReader ,IMessageWriter messageWriter) throws IOException{
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(
                new InetSocketAddress(port)
        );
        serverSocket.configureBlocking(false);
        SelectionKey sk = serverSocket.register(selector,SelectionKey.OP_ACCEPT); // 接受链接
        this.messageReader = messageReader;
        this.messageWriter = messageWriter;
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
                log.info("selector " + selector + " is selecting !");
                if (selector.select() > 0 ){// get the ready socketKeys
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectedKeys.iterator();
                    while (it.hasNext()){
                        SelectionKey key = it.next();
                        dispatch(key);
//                        it.remove();  // FIXME needed ?
                    }
                    selectedKeys.clear();
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    private void dispatch(SelectionKey k) {
        log.info("selector " + selector + " is dispatching key " + k + " !");
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
                    log.info(socketChannel + " has connected");
                    new Handler(selector,socketChannel,messageReader,messageWriter); // 注册Channel到Selector
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

}
