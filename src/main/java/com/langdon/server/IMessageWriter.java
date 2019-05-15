package com.langdon.server;

import java.io.IOException;

public interface IMessageWriter {

    public void write(Handler handler)throws IOException;
}
