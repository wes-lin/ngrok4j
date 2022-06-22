package app.vercel.ngrok4j.codec;

import app.vercel.ngrok4j.util.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
@Log4j2
public class JsonEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = MessageUtils.getPayloadByte(msg);
        log.info(new String(data));
        out.writeBytes(data);
    }

}
