package com.langdon.server;

public class Message {

    public static final int DEFAULT_CAPACITY = 4096 ;

    public byte [] partBytes ;

    public int length ; // @see the limit in byteBuffer

//    public int offset ; // 因为message 是连续是，所以不需要记录 offset
    public int capacity ;

    private MessageBuffer messageBuffer ;

    public Message(MessageBuffer messageBuffer){
        this.messageBuffer = messageBuffer;
    }


}
