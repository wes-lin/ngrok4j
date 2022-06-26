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

    public static String byte2hex(byte[] a) {
        /*StringBuilder sb = new StringBuilder(a.length * 2);

        for (byte b : a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();*/
        String hexString ="";
        for(int i = 0; i < a.length; i++){
            String thisByte ="".format("%02x ", a[i]);
            hexString += thisByte;
        }

        return hexString;

    }

    public static void main(String[] args) {
        String json = "{\"Type\":\"Pong\",\"Payload\":{}}";
        ByteBuf pack = ByteBufUtils.pack(json.getBytes());
        byte[] data = new byte[pack.readableBytes()];
        pack.readBytes(data);
        System.out.println(byte2hex(data));

    }

}
