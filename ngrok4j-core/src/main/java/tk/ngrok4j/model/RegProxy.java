package tk.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Data
public class RegProxy implements Serializable {

    private static final long serialVersionUID = 1511964539539922624L;
    private String clientId;
}
