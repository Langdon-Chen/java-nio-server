package com.langdon.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * the handler behaves as socket
 */
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
        selectionKey.interestOps(SelectionKey.OP_READ);
        messageBuffer = new MessageBuffer();
        s.wakeup();
    }

    public void run(){
        try {
            final int readStatus = this.messageReader.read(socketChannel,messageBuffer);
            switch (readStatus){
                case MessageConst.READ_CHANNEL_END:
                    this.messageWriter.write(socketChannel,null); // for bad request
                    close();
                    break;
                case MessageConst.READ_PART:
                    break;
                case MessageConst.READ_COMPLETE:
                    final int writeStatus = this.messageWriter.write(socketChannel,messageBuffer);
                    switch (writeStatus){
                        case MessageConst.WRITE_COMPLETE:
                        case MessageConst.WRITE_ERROR:
                            close();
                            break;
                        case MessageConst.WAITE_FOR_NEXT_ROUND:
                            reset();
                            break;
                    }
                    close();
                    break;
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
        log.log(Level.INFO,"SocketChannel has closed " + socketChannel);
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

    private void reset(){
        this.messageBuffer.reset();
    }


}
