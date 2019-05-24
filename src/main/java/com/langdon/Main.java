package com.langdon;

import com.langdon.http.HttpMessageReader;
import com.langdon.http.HttpMessageWriter;
import com.langdon.http.HttpParser;
import com.langdon.server.Reactor;
import org.apache.commons.fileupload.MultipartStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main  {
    public static void main(String[] args) throws Exception{
        final HttpParser parser = new HttpParser();
        Reactor reactor = new Reactor(8080,new HttpMessageReader(),new HttpMessageWriter(parser));
        new Thread(reactor).start();

//        ExecutorService workerPool =  Executors.newFixedThreadPool(16);
//        workerPool.submit(reactor);
//        byte [] boundary = "----WebKitFormBoundaryNDqJdQf4VJasYAKf".getBytes();
//        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
//        MultipartStream multipartStream = new MultipartStream(inputStream, boundary,4096,null);
//        boolean nextPart = multipartStream.skipPreamble();
//        while (nextPart){
//            String headersString = multipartStream.readHeaders();
//            System.out.print(headersString);
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            multipartStream.readBodyData(data);
//            System.out.println(new String(data.toByteArray()));
//            nextPart = multipartStream.readBoundary();
//        }

    }

    public static final String str = "------WebKitFormBoundaryNDqJdQf4VJasYAKf\r\n" +
            "Content-Disposition: form-data; name=\"fname\"\r\n" +
            "\r\n" +
            "&#38472;\r\n" +
            "------WebKitFormBoundaryNDqJdQf4VJasYAKf\r\n" +
            "Content-Disposition: form-data; name=\"lname\"\r\n" +
            "\r\n" +
            "&#26223;&#27849;\r\n" +
            "------WebKitFormBoundaryNDqJdQf4VJasYAKf\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"&#21151;&#33021;&#27169;&#22359;&#35774;&#35745;.md\"\r\n" +
            "Content-Type: text/markdown\r\n" +
            "\r\n" +
            "| **功能子项** | **功能说明与阐述**                                           |\r\n" +
            "| ------------ | ------------------------------------------------------------ |\r\n" +
            "| 用户管理中心 | 管理中心主要是管理用户的权限和个人数据信息。对于普通用户，可以管理个人信息和项目。个人信息管理包括更新基本信息和修改密码，项目管理请看下文。对于系统管理员，还可以通过昵称，手机号和邮箱进行模糊查询出用户，并对用户的权限进行分配。 |\r\n" +
            "\r\n" +
            "------WebKitFormBoundaryNDqJdQf4VJasYAKf\r\n" +
            "Content-Disposition: form-data; name=\"submit\"\r\n" +
            "\r\n" +
            "&#25552;&#20132;\r\n" +
            "------WebKitFormBoundaryNDqJdQf4VJasYAKf--";
}
