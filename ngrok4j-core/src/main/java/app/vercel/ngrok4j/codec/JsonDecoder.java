package app.vercel.ngrok4j.codec;

import app.vercel.ngrok4j.util.MessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
public class JsonDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readableBytes();
        byte[] data = new byte[len];
        in.readBytes(data);
        out.add(MessageUtils.getPayload(data));
    }
}
