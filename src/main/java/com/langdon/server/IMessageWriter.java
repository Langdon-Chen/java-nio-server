package com.langdon.server;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IMessageWriter {



    public void write(Handler handler)throws IOException;
}
