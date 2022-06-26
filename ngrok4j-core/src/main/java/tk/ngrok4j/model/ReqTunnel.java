package tk.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Data
public class ReqTunnel implements Serializable {

    private static final long serialVersionUID = -6623630588184292483L;
    private String reqId;
    private String protocol;
    private String hostname;
    private String subdomain;
    private String httpAuth;
    private int remotePort;
}
