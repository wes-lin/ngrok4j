package net.coding.ngrok4j.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Slf4j
public class FetchDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Channel channel;

    public FetchDataHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.channel.isActive()){
            this.channel.close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (msg.isReadable()) {
            byte[] data = new byte[msg.readableBytes()];
            msg.readBytes(data);
            channel.writeAndFlush(Unpooled.wrappedBuffer(data));
        }
    }
}
