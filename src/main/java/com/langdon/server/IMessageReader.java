package com.langdon.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface IMessageReader {

    public boolean read(SocketChannel socketChannel , MessageBuffer messageBuffer) throws IOException;
}
