package tk.ngrok4j.util;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Auther: WesLin
 * @Date: 2022/6/26
 * @Description:
 */
public class ByteBufUtilsTest {

    @Test
    public void pack() {
        String json = "{\"Type\":\"Pong\",\"Payload\":{}}";
        ByteBuf pack = ByteBufUtils.pack(json.getBytes());
        byte[] data = new byte[pack.readableBytes()];
        pack.readBytes(data);
        String encode = BaseEncoding.base16().encode(data);
        StringBuilder lenBuilder = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            lenBuilder.append(encode.substring(2 * i, 2 * (i + 1)));
        }
        int len = Integer.parseInt(lenBuilder.toString(), 16);
        Assert.assertEquals(json.getBytes().length, len);
    }
}