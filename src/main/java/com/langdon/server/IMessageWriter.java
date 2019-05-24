package com.langdon.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IMessageWriter {

    /**
     *
     * @param socketChannel
     * @param messageBuffer
     * @return true means it's time to shutdown the socketChannel
     * @throws IOException
     */
    public int write(SocketChannel socketChannel , MessageBuffer messageBuffer)throws IOException;
}
