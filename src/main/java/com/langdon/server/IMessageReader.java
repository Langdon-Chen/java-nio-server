package com.langdon.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IMessageReader {

    /**
     *
     * @param socketChannel the {@link java.nio.channels.SocketChannel} to read from
     * @param messageBuffer a buffer {@link MessageBuffer} stored the message from socketChannel
     * @return
     *  {@link MessageConst#READ_PART} (1) : read partial message , waite for next round .
     *  {@link MessageConst#READ_CHANNEL_END} (-1) : have reach the end of channel. respond bad request.
     *  {@link MessageConst#READ_COMPLETE} (1) : read complete message
     * @throws IOException
     */
    public int read(SocketChannel socketChannel , MessageBuffer messageBuffer) throws IOException;

}
