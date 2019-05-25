package com.langdon.server;

import java.nio.channels.SocketChannel;

public final class MessageConst {

    /**-------------------------------------------
     see {@link IMessageReader#read(SocketChannel, MessageBuffer)}}
     ============================================*/
    public static final int READ_COMPLETE = 0;

    public static final int READ_CHANNEL_END = -1;

    public static final int READ_PART = 1;

    /**-------------------------------------------
     see {@link IMessageWriter#write(SocketChannel, MessageBuffer)}}
     ============================================*/

    public static final int WRITE_COMPLETE = 0;

    public static final int WAITE_FOR_NEXT_ROUND = 1; // means keep alive in http

    public static final int WRITE_ERROR = -1;

    private MessageConst(){}
}
