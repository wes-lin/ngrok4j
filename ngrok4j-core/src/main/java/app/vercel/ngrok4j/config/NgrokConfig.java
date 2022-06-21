package app.vercel.ngrok4j.config;

import lombok.Data;

/**
 * @Auther: WesLin
 * @Date: 2022/6/14
 * @Description:
 */
@Data
public class NgrokConfig {

    private String serverAddr;
    private int serverPort;
    private NgrokTunnel tunnels;

}
