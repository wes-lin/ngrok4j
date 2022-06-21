package app.vercel.ngrok4j.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Data
public class Response implements Serializable{

    private static final long serialVersionUID = -6694127877940336186L;

    private MsgType type;
    private Object payload;

}
