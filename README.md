# Ngrok4j
[中文](./README_zh.md)

Ngrok4j is a java project that implements the ngrok protocol based on netty. It allows the server reverse proxy to access your local services. It can be used for some local domain name debugging needs.
## Links
* [Netty](https://github.com/netty/netty)
* [Ngrok](https://ngrok.com/)
* [dingtalk](https://open.dingtalk.com/document/resourcedownload/http-intranet-penetration)
## How use ngrok4j

* add dependency:
```xml
    <dependency>
        <groupId>app.vercel.ngrok4j</groupId>
        <artifactId>ngrok4j-client</artifactId>
        <version>1.0.0</version>
    </dependency>
```
* In general Java project
```java
public class ClientTest {
    public static void main(String[] args) throws Exception{
        NgrokTunnel[] tunnels = new NgrokTunnel[]{
                NgrokTunnel.builder().subdomain("local-nginx").lhost("127.0.0.1").lport(80).protocol(Protocol.http).build(),
                NgrokTunnel.builder().remotePort(8306).lhost("127.0.0.1").lport(3306).protocol(Protocol.tcp).build()
        };
        NgrokConfig config = new NgrokConfig("vaiwan.com", 443, tunnels);
        NgrokClient client = new NgrokClient(config);
        client.start();
    }
}
```
* NgrokConfig
```java
public class NgrokConfig {

    private String serverAddr;//remote server address
    private int serverPort;//remote server port
    private NgrokTunnel[] tunnels;//tunnel config

}

public class NgrokTunnel {

    private Protocol protocol;//only support http or tcp
    private String subdomain;//subdomain，just for protocol is http
    private String lhost;//local ip address
    private int lport;//local ip address port
    private int remotePort;//remote server port，just for protocol is tcp
    private String url;//actual access address
}
```
