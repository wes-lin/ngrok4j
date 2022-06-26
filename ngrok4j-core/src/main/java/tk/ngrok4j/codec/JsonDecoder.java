package tk.ngrok4j.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import tk.ngrok4j.util.LogUtils;
import tk.ngrok4j.util.MessageUtils;

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
        LogUtils.logIn(this.getClass(), new String(data));
        out.add(MessageUtils.getPayload(data));
    }
}
