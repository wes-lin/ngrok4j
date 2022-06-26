package tk.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
@Data
public class StartProxy implements Serializable {
    private static final long serialVersionUID = 818144647983398662L;

    private String url;
    private String clientAddr;
}
