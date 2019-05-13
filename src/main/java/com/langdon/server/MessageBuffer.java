package com.langdon.server;

import java.nio.ByteBuffer;

public class MessageBuffer {
    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static final int CAPACITY_SMALL  =   1  * KB;
    private static final int CAPACITY_MEDIUM = 4  * KB;
    private static final int CAPACITY_LARGE  = 1 * MB;
    private static final int CAPACITY_MAX = 10 * MB; // the max size per request

    private int length = 0;
    private int capacity = CAPACITY_LARGE;
    private byte[] buffer ;


    public  MessageBuffer(){
        buffer = new byte[capacity];
    }

    /**
     * auto enlarge the buffer
     * @param byteBuffer ready to read
     * @return false : to byteBuffer is larger than 10MB , server refuse the request
     */
    public boolean writeIntoBuffer(ByteBuffer byteBuffer){
        byte[] readyToRead = new byte[byteBuffer.remaining()];
        byteBuffer.get(readyToRead);
        if (length + readyToRead.length >= capacity){
            if (!enlargeBuffer())
                return false;
        }
        return true;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    private boolean enlargeBuffer(){
        if (capacity == CAPACITY_SMALL){
            return enlargeBuffer(CAPACITY_MEDIUM);
        }else if (capacity == CAPACITY_MEDIUM){
            return enlargeBuffer(CAPACITY_LARGE);
        }else if (capacity == CAPACITY_LARGE){
            return enlargeBuffer(CAPACITY_MAX);
        }else return false;
    }

    private boolean enlargeBuffer(int targetCapacity){
        byte[] newBuffer = new byte[targetCapacity];
        System.arraycopy(this.buffer,0,newBuffer,0,this.length);
        this.clear();
        this.buffer = newBuffer;
        return true;
    }

    public void clear(){
        this.buffer = null;
    }

}
