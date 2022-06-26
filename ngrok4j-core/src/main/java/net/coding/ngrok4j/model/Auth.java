package net.coding.ngrok4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Data
public class Auth implements Serializable{

    private static final long serialVersionUID = -220946150060970954L;

    private String version;
    private String mmVersion;
    private String user;
    private String password;
    @JsonProperty("OS")
    private String OS;
    private String arch;
    private String clientId;
}
