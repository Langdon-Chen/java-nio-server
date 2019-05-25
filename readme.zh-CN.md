简体中文 | [English](./readme.md)

<h1 align="center">Nio-Server in Java </h1>

出于学习的目的，这是一个基于 Java nio 写的服务器，采用 react 模式，实现了HTTP协议，目前的功能是返回你所发出的 HTTP 请求报文。基于我的代码，我相信你可以实现任何一种应用层协议。

## Usage
```
java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar
   
Or

java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar yourPort 
```

默认运行在 8001 端口。
比如在浏览器请求 `http://localhost:8001/` ，你可能看的是
```http request
GET / HTTP/1.1
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3
Upgrade-Insecure-Requests: 1
Connection: keep-alive
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36
Host: localhost:8001
Accept-Encoding: gzip, deflate, br
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
```

## 参考资料

- [java-nio-server](https://github.com/jjenkov/java-nio-server)
- [webserver](https://github.com/vguzun/webserver)
- [Reactor - An Object Behavioral Pattern for Demultiplexing and Dispatching Handles for Synchronous Events ](http://www.cs.wustl.edu/~schmidt/PDF/reactor-siemens.pdf)