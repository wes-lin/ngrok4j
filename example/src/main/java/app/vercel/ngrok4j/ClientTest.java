package app.vercel.ngrok4j;

import app.vercel.ngrok4j.config.NgrokConfig;
import app.vercel.ngrok4j.config.NgrokTunnel;
import app.vercel.ngrok4j.config.Protocol;

/**
 * @Auther: WesLin
 * @Date: 2022/6/25
 * @Description:
 */
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
