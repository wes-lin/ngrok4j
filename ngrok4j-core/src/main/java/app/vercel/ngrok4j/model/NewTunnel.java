package app.vercel.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Data
public class NewTunnel implements Serializable{

    private static final long serialVersionUID = 4186082761956945594L;
    private String reqId;
    private String protocol;
    private String url;
    private String error;
}
