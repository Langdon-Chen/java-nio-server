package com.langdon.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IMessageWriter {

    public boolean write(SocketChannel socketChannel , MessageBuffer messageBuffer)throws IOException;
}
