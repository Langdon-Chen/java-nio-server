package com.langdon.server;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.logging.Logger;

public final class Handler  implements Runnable{
    protected Logger log = Logger.getLogger(getClass().getName());
    private final SocketChannel socketChannel ;
    private final SelectionKey selectionKey ;

    public static final int MAX_IN = 4096 ;

    static final int READING = 0 ,SENDING = 1;
    private int state = READING ;
    private boolean streamHasEnded = false;

    private MessageBuffer messageBuffer;


    public Handler(Selector selector , SocketChannel c)throws IOException{
        socketChannel = c ;
        socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ); // selector 根据 interest 来 selector the ready keys
        messageBuffer = new MessageBuffer();

        //这里必须先唤醒read_Sel，然后加锁，防止读写线程的中select方法再次锁定。
        selector.wakeup();
    }

    public void run(){
        if (selectionKey.isConnectable()){
            log.info(selectionKey + " isConnectable"); // 其实已经 connected ，所以这里是false
        }else if (selectionKey.isReadable() && state == READING){
            log.info(selectionKey + " isReadable");
            try {
                ByteBuffer readByteBuffer  = ByteBuffer.allocate(MAX_IN);
                int bytesRead = this.read(readByteBuffer);
                if(readByteBuffer.remaining() == 0){ // bytesRead == 0 || bytesRead == -1
                    readByteBuffer.clear();
                }else {
                    readByteBuffer.flip();
                    if (! messageBuffer.writeIntoBuffer(readByteBuffer)){ // 缓存时自动扩容
                        String errorMsg = "The Request is larger than 10 MB !";
                        ByteBuffer bufferToWrite = ByteBuffer.wrap(errorMsg.getBytes());
                        while (bufferToWrite.remaining() > 0){
                            socketChannel.write(bufferToWrite);
                        }
                        selectionKey.cancel();
                        socketChannel.close();
                    }
                    // todo 根据 content-length 判断当前message是否已经读完了；读完了就开始回复

                }


                if (streamHasEnded){
                    log.info("SocketChannel has closed " + socketChannel);
                    selectionKey.attach(null);
                    selectionKey.cancel();
                    socketChannel.close();
                    // to write

                }

            }catch (IOException ex){
                ex.printStackTrace();
            }
        }else if (selectionKey.isWritable()){
            log.info(selectionKey + " isWritable");
        }
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            bytesRead = this.socketChannel.read(byteBuffer); // return 0 if byteBuffer is full.
            totalBytesRead += bytesRead;
        }

        if(bytesRead == -1){
            this.streamHasEnded = true;
        }

        return totalBytesRead;
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


}
