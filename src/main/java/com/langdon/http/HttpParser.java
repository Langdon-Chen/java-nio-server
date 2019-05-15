package com.langdon.http;

import com.langdon.server.IParser;
import com.langdon.server.Protocol;

import java.io.*;
import java.net.ProtocolFamily;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public  class HttpParser implements IParser<InputStream,HttpRequest> {

    protected Logger log = Logger.getLogger(getClass().getName());

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public ProtocolFamily getProtocolFamily(){
        return StandardProtocolFamily.INET6;
    }

    @Override
    public Charset getCharset(){
        return StandardCharsets.UTF_8;
    }

    /**
     *
     * Parses an incoming request. Reads the {@link InputStream} and creates an
     * corresponding {@link ServerHttpRequest} object, which will be returned.
     *
     * @param inputStream The stream to read from.
     * @return
     * @throws IOException
     */
    @Override
    public ServerHttpRequest parse(InputStream inputStream) throws IOException{
        String firstLine = readLine(inputStream);
        System.out.println(firstLine);
        ServerHttpRequest request = new ServerHttpRequest();
        request.setVersion(HttpVersion.extractVersion(firstLine));
        request.setMethod(HttpMethod.extractMethod(firstLine));
        request.setRequestUri(extractRequestUri(firstLine));
        request.setAttributes(extractAttributes(firstLine));
        Map<String, String> headers = new HashMap<String, String>();
        String nextLine = "";
        while (!(nextLine = readLine(inputStream)).equals("")) {
            System.out.println(nextLine);
            String []values = nextLine.split(":", 2);
            headers.put(values[0], values[1].trim());
        }
        request.setHeaders(headers);
        if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            int size = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
            byte[] data = new byte[size];
            int n;
            for (int i = 0; i < size && (n = inputStream.read()) != -1; i++) {
                data[i] = (byte) n;
            }
            request.setEntity(data);
        }
        else {
            request.setEntity(new byte[0]);
        }
        return request;
    }

    /**
     * 在Postman 中 socketChannel.read(buffer) 第一次会返回 -1 ; 需要第二次链接 socketChannel
     * n 有数据的时候返回读取到的字节数。
     * 0 没有数据并且没有达到流的末端时返回0。
     * -1 当达到流末端的时候返回-1。

     * -1, indicating EOS, meaning you should close the channel
     * zero, meaning there was no data to read, meaning you should return to the select() loop, and
     * a positive value, meaning you have read that many bytes, which you should then extract and remove from the ByteBuffer (get()/compact()) before continuing.
     */
    protected ServerHttpRequest parseRequest(SelectionKey selectionKey)throws IOException{
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // https://stackoverflow.com/questions/30428660/nio-socketchannel-saying-there-is-no-data-when-there-is-or-selector-is-not-info
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.clear();
        int bytesRead = -1 ;
        StringBuilder sb = new StringBuilder(1024);
        bytesRead = socketChannel.read(buffer);
        if (bytesRead == 0) {
            return null;
        }else if (bytesRead == -1){
            socketChannel.close();
            selectionKey.cancel();
            return null;
        } else{
            byte [] array = new byte[buffer.remaining()];
            buffer.flip();
            System.arraycopy(buffer.array(),0,array,0,array.length);
            sb.append(new String(array, StandardCharsets.UTF_8));
            buffer.clear();
        }
        log.info("http  request content as follow : "); //        means nothing
//        System.out.println(sb.toString().trim());
//        System.out.println(sb.length());
//        System.out.println(sb.toString().trim().length());
//        System.out.println(sb.toString());


        String[] lines = sb.toString().split(HttpHeaders.CRLF);
        for (String s : lines)
            System.out.println(s);
        int n = 0 ;
        ServerHttpRequest request = new ServerHttpRequest();
        request.setVersion(HttpVersion.extractVersion(lines[n]));
        request.setMethod(HttpMethod.extractMethod(lines[n]));
        request.setRequestUri(extractRequestUri(lines[n]));
        request.setAttributes(extractAttributes(lines[n]));
        Map<String, String> headers = new HashMap<String, String>();
        while ( !"".equals(lines[++n]) ){
            String []values = lines[n].split(":", 2);
            headers.put(values[0], values[1].trim());
        }
        request.setHeaders(headers);
        //  content-length or Content-Length
        // Accept-Encoding or accept-encoding
        if (headers.containsKey(HttpHeaders.CONTENT_LENGTH)){
            int size = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
            byte[] entity  = lines[++n].substring(0,size).getBytes();
            request.setEntity(entity);
        }else if (headers.containsKey(HttpHeaders.CONTENT_LENGTH2)){
            int size = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH2));
            byte[] entity  = lines[++n].substring(0,size).getBytes();
            request.setEntity(entity);
        }else {
            request.setEntity(new byte[0]);
        }
        return request;
    }


    /**
     * 处理请求的URL，如果请求的资源存在，就把所访问的资源放在entity里面；
     * Creates an appropriate {@link ServerHttpResponse} to the given
     * {@link ServerHttpResponse}. Note however, that this method is not yet sending
     * the response.
     *
     * @param request
     *            The {@link ServerHttpRequest} that must be handled.
     * @return
     */
    protected ServerHttpResponse handleRequest(ServerHttpRequest request)
    {
        ServerHttpResponse httpResponse = new ServerHttpResponse();
        httpResponse.setHeaders(new HashMap<String, String>());
        httpResponse.getHeaders().put(HttpHeaders.SERVER, "Langdon's NIO Http Server");
        httpResponse.getHeaders().put(HttpHeaders.DATE,new Date().toString());
        httpResponse.setVersion(request.getHttpVersion());
        // MVC 模式
        httpResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        // todo 改用更优雅的返回方式
        byte[] fileContent = ("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>"
                + "ERROR"
                + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1 align='center' > "
                + HttpStatus.NOT_FOUND
                + " </h1>\n" +
                "</body>\n" +
                "</html>").getBytes();
        httpResponse.setEntity(fileContent);
        return httpResponse;
    }

    /**
     * Sends a given {@link HttpResponse} over the given {@link OutputStream}.
     *
     * @param response
     * @param outputStream
     * @throws IOException
     */
    protected void sendResponse(HttpResponse response, OutputStream outputStream) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        writer.write(response.getHttpVersion().toString());
        writer.write(' ');
        writer.write("" + response.getHttpStatus());
        writer.write(' ');
        writer.write(response.getHttpStatus().getReasonPhrase());
        writer.write(HttpHeaders.CRLF);

        //设置header.contentLength
        if (response.getEntity() != null && response.getEntity().length > 0)
        {
            response.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "" + response.getEntity().length);
        }
        else
        {
            response.getHeaders().put(HttpHeaders.CONTENT_LENGTH, "" + 0);
        }
        //返回header信息
        if (response.getHeaders() != null)
        {
            for (String key : response.getHeaders().keySet())
            {
                writer.write(key + ": " + response.getHeaders().get(key) + HttpHeaders.CRLF);
            }
        }
        writer.write(HttpHeaders.CRLF);
        writer.flush();
        //？ 为什么这里写header使用writer，写entity用outputStream;因为entity是byte[]
        //写入entity信息
        if (response.getEntity() != null && response.getEntity().length > 0)
        {
            outputStream.write(response.getEntity());
        }
        outputStream.flush();

    }

    /**
     * 读取inputstream；直到遇到了\r\n
     * A helper method that reads an InputStream until it reads a CRLF (\r\n\).
     * Everything in front of the linefeed occured is returned as String.
     *
     * @param inputStream The stream to read from.
     * @return The character sequence in front of the linefeed.
     * @throws IOException
     */
    public String readLine(InputStream inputStream) throws IOException {
        StringBuffer result = new StringBuffer();
        boolean crRead = false;
        int n;
        while ((n = inputStream.read()) != -1) {
            if (n == '\r')
            {
                crRead = true;
                continue;
            }
            else if (n == '\n' && crRead)
            {
                return result.toString();
            }
            else
            {
                result.append((char) n);
            }
        }
        return result.toString();
    }

    /**
     * extract request uri without attributes
     * for example, /pet/get?name=tom&age=18  should be /pet/get
     * @param firstLine
     * @return
     */
    private String extractRequestUri(String firstLine){
        int start = firstLine.indexOf(" ") + 1;
        int end = firstLine.lastIndexOf(" ");
        String requestUriWithAttributes = firstLine.substring(start,end);
        int questionMarkIndex = requestUriWithAttributes.indexOf("?");
        if (questionMarkIndex > 0){
            return requestUriWithAttributes.substring(0,questionMarkIndex);
        }
        return requestUriWithAttributes;
    }

    /**
     *  extract url params from firstLine {@link HttpParser#parse(InputStream)}
     *  for example, /pet/get?name=tom&age=18  should be name->tom and age->18
     * @param firstLine
     * @return
     */
    private Map<String,Object> extractAttributes(String firstLine){
        Map<String,Object> attributes = new HashMap<String, Object>();
        // todo
        int start = firstLine.indexOf(" ") + 1;
        int end = firstLine.lastIndexOf(" ");
        String requestUriWithAttributes = firstLine.substring(start,end);
        int questionMarkIndex = requestUriWithAttributes.indexOf("?");
        // there may are not attributes in request
        if (questionMarkIndex > 0){
            String [] params =  requestUriWithAttributes.substring(questionMarkIndex+1).split("&");
            for (String p : params){
                String [] keyValue = p.split("=");
                attributes.put(keyValue[0],keyValue[1]);
            }
        }
        return attributes;
    }


}
