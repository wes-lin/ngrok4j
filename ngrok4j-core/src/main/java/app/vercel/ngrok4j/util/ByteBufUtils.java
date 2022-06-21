package app.vercel.ngrok4j.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang.ArrayUtils;

import java.nio.ByteBuffer;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
public class ByteBufUtils {

    public static ByteBuf pack(String message) {
        ByteBuf buffer = Unpooled.buffer();
        byte[] len = ByteBuffer.allocate(8).putLong(message.length()).array();
        ArrayUtils.reverse(len);
        buffer.writeBytes(len).writeBytes(message.getBytes());
        return buffer;
    }

}
