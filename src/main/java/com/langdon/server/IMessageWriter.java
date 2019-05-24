package com.langdon.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IMessageWriter {

    /**
     *
     * @param socketChannel the channel to write
     * @param messageBuffer the message has read before
     * @return
     *   {@link MessageConst#WRITE_COMPLETE} (0) write successfully and it is time closing the channel
     *   {@link MessageConst#WAITE_FOR_NEXT_ROUND} (1) write successfully and please do not close the channel
     *   {@link MessageConst#WRITE_ERROR} (-1) write error due to bad request .
     * @throws IOException
     */
    public int write(SocketChannel socketChannel , MessageBuffer messageBuffer)throws IOException;
}
