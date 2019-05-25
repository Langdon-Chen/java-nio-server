English | [简体中文](./readme.zh-CN.md)

<h1 align="center">Nio-Server in Java </h1>

For the purpose of learning, I write a web server based on Java nio. It implements the HTTP protocol using the react mode. Now it can only return the HTTP request message you sent. Based on my code, I believe you can implement any kind of application layer protocol.
## Usage
```
java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar
   
Or

java -jar nio-server-1.0-SNAPSHOT-jar-with-dependencies.jar yourPort 
```
Default port is 8001.
If you request `http://localhost:8001/` in a browser , you may get 
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

## Resources

- [java-nio-server](https://github.com/jjenkov/java-nio-server)
- [webserver](https://github.com/vguzun/webserver)
- [Reactor - An Object Behavioral Pattern for Demultiplexing and Dispatching Handles for Synchronous Events ](http://www.cs.wustl.edu/~schmidt/PDF/reactor-siemens.pdf)