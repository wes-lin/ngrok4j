package app.vercel.ngrok4j.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Log4j2
public class FetchDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;

    public FetchDataHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg.isReadable()) {
            log.info("FatchDataHandler write message to remote address:{} ", channel.remoteAddress());
            channel.writeAndFlush(msg.copy());
        }
    }
}
