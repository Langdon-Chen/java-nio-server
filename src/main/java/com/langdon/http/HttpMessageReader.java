package com.langdon.http;

import com.langdon.http.basic.HttpHeaders;
import com.langdon.server.Handler;
import com.langdon.server.IMessageReader;
import com.langdon.server.MessageBuffer;
import com.langdon.server.MessageConst;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
/**
 * The response from socketChannel.read(ByteBuffer)
 * https://stackoverflow.com/questions/30428660/nio-socketchannel-saying-there-is-no-data-when-there-is-or-selector-is-not-info
 * -1, indicating EOS, meaning you should close the channel
 * zero, meaning there was no data to read, meaning you should return to the select() loop, and
 * a positive value, meaning you have read that many bytes, which you should then extract and remove from the ByteBuffer (get()/compact()) before continuing.
 */
public class HttpMessageReader implements IMessageReader {

    public static final int MAX_IN = 1024;
    private static int n = 0;
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
    @Override
    public int read(SocketChannel socketChannel , MessageBuffer messageBuffer) throws IOException , OutOfMemoryError {
        n++;
        int res  = MessageConst.READ_PART;
        ByteBuffer readByteBuffer  = ByteBuffer.allocate(MAX_IN);
        int bytesRead = socketChannel.read(readByteBuffer);
        int totalBytesRead = bytesRead;
        while(bytesRead > 0){
            bytesRead = socketChannel.read(readByteBuffer); // return 0 if byteBuffer is full.
            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            res = MessageConst.READ_CHANNEL_END;
        }
        if(totalBytesRead > 0 ){ // bytesRead == 0 || bytesRead == -1
            readByteBuffer.flip(); // set position to 0 , make ready for being read
            if (! messageBuffer.writeIntoBuffer(readByteBuffer)){ // 缓存时自动扩容
                throw new OutOfMemoryError("Request is larger than 10 MB ! Server has rejected this request !");
            }
            if (hasReadCompletely(messageBuffer.getBuffer(),messageBuffer.getLength())){
//                System.out.println(new String(messageBuffer.getBytesHasRead()));
                res = MessageConst.READ_COMPLETE;
            }
        }
        return res ;
    }
    /**
     * for some request , they may would not auto close the inputStream . so we need to  check if  read completely
     * strategy : check if the request headers contain "content-length" . if so , then count the complete request's length and
     * compare it to what we have read .
     */
    private boolean hasReadCompletely(byte[] src , int length){
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
