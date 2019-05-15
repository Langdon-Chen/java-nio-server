package com.langdon.http;

import com.langdon.server.Handler;
import com.langdon.server.IMessageReader;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HttpMessageReader implements IMessageReader {

    public static final int MAX_IN = 1024 ; // the max bytes each time to read

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
        if(totalBytesRead > 0 ){ // bytesRead == 0 || bytesRead == -1
            readByteBuffer.flip(); // set position to 0 , make ready for being read
            if (! handler.messageBuffer.writeIntoBuffer(readByteBuffer)){ // 缓存时自动扩容
                throw new OutOfMemoryError("Request is larger than 10 MB ! Server has rejected this request !");
            }
            if (hasReadCompletely(handler.messageBuffer.getBuffer(),handler.messageBuffer.getLength())){
//                System.out.println(new String(handler.messageBuffer.getBytesHasRead()));
                handler.setStreamHasEnded(true);
            }
        }
    }
    /**
     * for some request , they may would not auto close the inputStream . so we need to  check if  read completely
     * strategy : check if the request headers contain "content-length" . if so , then count the complete request's length and
     * compare it to what we have read .
     */
    public boolean hasReadCompletely(byte[] src , int length) throws IOException {
        // find if the HttpHeader has content-length and then check the body's length
        int endOfFirstLine = findCRLF(src,0,length);
        if (endOfFirstLine == -1)
            return false;

        int contentLength = -1 ; // content-length is the body bytes's length , not the length of body char[]

        int startOfLine = endOfFirstLine + 1;
        int endOfLine = findCRLF(src,startOfLine,length);
        String line = getSubString(src,startOfLine,endOfLine);
        while (endOfLine != -1 && endOfLine != startOfLine + 1){ // "" means the end of headers
            // find content length
            if (contentLength == -1 && line.contains(HttpHeaders.CONTENT_LENGTH) || line.contains(HttpHeaders.CONTENT_LENGTH2)){
                String [] values = line.split(":");
                if (values.length==2){
                    contentLength = Integer.valueOf(values[1].trim());
                }
            }
            startOfLine = endOfLine + 1;
            endOfLine = findCRLF(src,startOfLine,length);
            line = getSubString(src,startOfLine,endOfLine+1);// get next line
        }
        // 1. has not read the complete header yet
        if (endOfLine == -1)
            return false;
        int bodyStart = endOfLine  + 1;
        int bodyEnd  = bodyStart + contentLength;
        // 2. false : has not read the complete request
        // 3. true  : has read the complete request
        return bodyEnd <= length;

    }

    /**
     *
     * @param src
     * @param start
     * @param end
     * @return {#HttpHeaders.CRLF} is \r\n  , return value is the index of \n
     *  /r == 9
     *  /n == 10
     */
    private static int findCRLF(byte[]src , int start ,int end ){
        for(int index = start; index < end; index++){
            if(src[index] == '\n' && src[index - 1] == '\r'){
                return index;
            }
        }
        return -1;
    }

    private String getSubString(byte[]bytes , int start ,int end){
        byte [] res = new byte[end - start];
        if (end - start >= 0) System.arraycopy(bytes, start, res, 0, end - start);
        return new String(res);
    }

}
