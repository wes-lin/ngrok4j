package net.coding.ngrok4j.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Data
@AllArgsConstructor
public class Message<T> implements Serializable{

    private static final long serialVersionUID = -5174339582320840463L;

    private MsgType type;
    private T payload;
}
