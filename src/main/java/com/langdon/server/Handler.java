package com.langdon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler implements Runnable{
    private final Logger log = Logger.getLogger(getClass().getName());
    private final SocketChannel socketChannel ;
    private final SelectionKey selectionKey ;

    private MessageBuffer messageBuffer;
    private IMessageReader messageReader ;
    private IMessageWriter messageWriter;


    public Handler(Selector s , SocketChannel c ,IMessageReader r ,IMessageWriter w)throws IOException{
        this.socketChannel = c ;
        this.messageReader = r;
        this.messageWriter = w;
        socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(s, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ); // selector 根据 interest 来 selector the ready keys
        messageBuffer = new MessageBuffer();
        //这里必须先唤醒read_Sel，然后加锁，防止读写线程的中select方法再次锁定
        s.wakeup();
    }

    public void run(){
        try {
//            if (this.messageReader.read(socketChannel,messageBuffer)){
//                this.messageWriter.write(socketChannel,messageBuffer);
//                this.close();
//            }
            boolean readComplete = this.messageReader.read(socketChannel,messageBuffer);
            if (readComplete){
                boolean readyToClose = this.messageWriter.write(socketChannel,messageBuffer);
                if (readyToClose)
                    close();
            }
        }catch (IOException | OutOfMemoryError ex){
            ex.printStackTrace();
            close();
        }
    }

    public int write(ByteBuffer byteBuffer) throws IOException{
        int bytesWritten      = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;

        while(bytesWritten > 0 && byteBuffer.hasRemaining()){
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }

        return totalBytesWritten;
    }

    private void close(){
        log.log(Level.FINEST,"SocketChannel has closed " + socketChannel);
        selectionKey.attach(null);
        selectionKey.cancel();
        try{
            socketChannel.close();
        }catch (IOException ex){
            log.log(Level.WARNING,ex.getMessage());
        }
        this.messageBuffer.clear();
        selectionKey.selector().wakeup();
    }



}
