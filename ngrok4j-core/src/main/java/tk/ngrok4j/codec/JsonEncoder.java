package tk.ngrok4j.codec;

import tk.ngrok4j.util.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
public class JsonEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = MessageUtils.getPayloadByte(msg);
        out.writeBytes(data);
    }

}
