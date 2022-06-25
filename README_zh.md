# ngrok4j
===================================

[English](./README.md)

### 如何使用ngrok4j

* 添加依赖:
```xml
    <dependency>
        <groupId>vercel.app</groupId>
        <artifactId>ngrok4j-client</artifactId>
        <version>1.0.0</version>
    </dependency>
```
* 在普通Java项目中
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
* Ngrok配置
```java
public class NgrokConfig {

    private String serverAddr;//远程服务地址
    private int serverPort;//远程服务端口
    private NgrokTunnel[] tunnels;//tunnel配置参数

}

public class NgrokTunnel {

    private Protocol protocol;//协议支持http 和tcp
    private String subdomain;//子域名，仅用于http
    private String lhost;//本地ip
    private int lport;//本地端口
    private int remotePort;//远程端口，仅用于tcp
    private String url;//实际访问的地址,无需配置
}
```
