package com.langdon.http;

import com.langdon.server.Handler;
import com.langdon.server.IMessageReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HttpMessageReader implements IMessageReader {

    public static final int MAX_IN = 4096 ; // the max bytes each time to read

    @Override
    public void read(Handler handler) throws IOException , OutOfMemoryError {
        ByteBuffer readByteBuffer  = ByteBuffer.allocate(MAX_IN);
        int bytesRead = handler.read(readByteBuffer);
        int totalBytesRead = bytesRead;
        while(bytesRead > 0){
            bytesRead = handler.read(readByteBuffer); // return 0 if byteBuffer is full.
            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            handler.setStreamHasEnded(true);
        }
        if(readByteBuffer.remaining() == 0){ // bytesRead == 0 || bytesRead == -1
            readByteBuffer.clear();
        }else {
            readByteBuffer.flip(); // set position to 0 , make ready for being read
            if (! handler.writeIntoBuffer(readByteBuffer)){ // 缓存时自动扩容
                throw new OutOfMemoryError("Request is larger than 10 MB ! Server has rejected this request !");
            }
        }
    }
}
