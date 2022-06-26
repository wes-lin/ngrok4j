package net.coding.ngrok4j.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
public class ByteBufUtils {

    public static ByteBuf pack(byte[] data) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeLongLE(data.length).writeBytes(data);
        return buffer;
    }

}
