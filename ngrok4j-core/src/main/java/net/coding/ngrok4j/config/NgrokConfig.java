package net.coding.ngrok4j.config;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Auther: WesLin
 * @Date: 2022/6/14
 * @Description:
 */
@Data
@AllArgsConstructor
public class NgrokConfig {

    private String serverAddr;
    private int serverPort;
    private NgrokTunnel[] tunnels;

}
