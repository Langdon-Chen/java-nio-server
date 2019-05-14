package com.langdon.server;

import java.io.IOException;

public interface IMessageReader {

    public void read(Handler handler) throws IOException;
}
