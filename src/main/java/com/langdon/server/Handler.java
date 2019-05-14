package com.langdon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler implements Runnable{
    protected Logger log = Logger.getLogger(getClass().getName());
    private final SocketChannel socketChannel ;
    private final SelectionKey selectionKey ;

    private static final int READING = 0 ,SENDING = 1;
    private int state = READING ;
    private boolean streamHasEnded = false;

    private MessageBuffer messageBuffer;
    private IMessageReader messageReader ;
    private IMessageWriter messageWriter;


    public Handler(Selector selector , SocketChannel c ,IMessageReader r ,IMessageWriter w)throws IOException{
        this.socketChannel = c ;
        this.messageReader = r;
        this.messageWriter = w;
        socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ); // selector 根据 interest 来 selector the ready keys
        messageBuffer = new MessageBuffer();
        //这里必须先唤醒read_Sel，然后加锁，防止读写线程的中select方法再次锁定

        selector.wakeup();
    }

    public void run(){
        try {
            switch (state)
            {
                case READING:
                    this.messageReader.read(this);
                    if (streamHasEnded){
                        state = SENDING;
                    }
                    break;
                case SENDING:
                    this.messageWriter.write(this);
                    this.close();
                    break;
            }
        }catch (IOException | OutOfMemoryError ex){
            ex.printStackTrace();
            close();
        }
    }

    public int read(ByteBuffer byteBuffer) throws IOException{
        return this.socketChannel.read(byteBuffer);
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

    protected void close(){
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

    public boolean writeIntoBuffer(ByteBuffer byteBuffer){
        return this.messageBuffer.writeIntoBuffer(byteBuffer);
    }

    /**
     * get valid bytes that has read ;
     * @return
     */
    public byte[] getBytesHasRead(){
        return this.messageBuffer.getBytesHasRead();
    }

    public void setStreamHasEnded(boolean ended){
        this.streamHasEnded = ended;
    }

}
