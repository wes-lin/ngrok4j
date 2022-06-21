package app.vercel.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Data
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = -7057803007373267353L;

    private String version;
    private String mmVersion;
    private String clientId;
    private String error;
}
