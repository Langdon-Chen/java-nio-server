简体中文 | [English](./readme.md)

<h1 align="center">Nio-Server in Java </h1>

出于学习的目的，这是一个基于 Java nio 写的服务器，采用 react 模式，实现了HTTP协议，目前的功能是返回你所发出的 HTTP 请求报文。

## Usage
```
java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar
   
Or

java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar yourPort 
```

默认运行在 8001 端口。

## 参考资料

- [java-nio-server](https://github.com/jjenkov/java-nio-server)
- [webserver](https://github.com/vguzun/webserver)
- [Reactor - An Object Behavioral Pattern for Demultiplexing and Dispatching Handles for Synchronous Events ](http://www.cs.wustl.edu/~schmidt/PDF/reactor-siemens.pdf)