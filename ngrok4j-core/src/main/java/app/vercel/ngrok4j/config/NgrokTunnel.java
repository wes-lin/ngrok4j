package app.vercel.ngrok4j.config;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: WesLin
 * @Date: 2022/6/14
 * @Description:
 */
@Data
@Builder
public class NgrokTunnel {

    private Protocol protocol;
    private String hostname;
    private String subdomain;
    private String lhost;
    private int lport;
    private String url;
}
