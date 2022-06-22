package app.vercel.ngrok4j.handler;

import app.vercel.ngrok4j.config.NgrokConfig;
import app.vercel.ngrok4j.config.NgrokTunnel;
import app.vercel.ngrok4j.model.StartProxy;
import app.vercel.ngrok4j.util.LogUtils;
import app.vercel.ngrok4j.util.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.Charset;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Log4j2
public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private ChannelFuture localChannel;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private static final int BUFFER_SIZE = 10240;

    private NgrokConfig config;

    public ProxyHandler(NgrokConfig config) {
        this.config = config;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int rb = byteBuf.readableBytes();
        if (rb > 8) {
            CharSequence charSequence = byteBuf.readCharSequence(rb, Charset.defaultCharset());
            String bufferStr = charSequence.toString();
            LogUtils.logIn(this.getClass(), bufferStr);
            try {
                Object response = MessageUtils.getPayload(bufferStr.getBytes());
                if (response instanceof StartProxy) {
                    log.info("=====StartProxy=====");
                    StartProxy startProxy = (StartProxy) response;
                    for (NgrokTunnel ngrokTunnel : config.getTunnels()) {
                        if (startProxy.getUrl().equals(ngrokTunnel.getUrl())){
                            Bootstrap b = new Bootstrap();
                            b.group(group)
                                    .channel(NioSocketChannel.class)
                                    .option(ChannelOption.TCP_NODELAY, true)
                                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(BUFFER_SIZE))
                                    .handler(new ChannelInitializer<SocketChannel>() {
                                        protected void initChannel(SocketChannel ch) {
                                            ChannelPipeline p = ch.pipeline();
                                            p.addLast(new FetchDataHandler(ctx.channel()));
                                        }
                                    });
                            localChannel = b.connect(ngrokTunnel.getLhost(), ngrokTunnel.getLport()).sync();
                            log.info("connect local port：" + localChannel.channel().localAddress());
                            localChannel.channel().closeFuture().addListener((ChannelFutureListener) t -> {
                                log.info("disconnect local port：" + localChannel.channel().localAddress());
                            });
                        }
                    }
                }
            } catch (Exception e) {
                log.error("{} :{}",this,e);
                String error = buildErrorMsg();
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer(error, CharsetUtil.UTF_8));
                ctx.close().sync();
            }
        }
    }

    private String buildErrorMsg(){
        String html = String.format("<html><body style=\"background-color: #97a8b9\"><div style=\"margin:auto; width:400px;padding: 20px 60px; background-color: #D3D3D3; border: 5px solid maroon;\"><h2>Tunnel %s unavailable</h2><p>Unable to initiate connection to <strong>%s</strong>. This port is not yet available for web server.</p>",
                "http://iyuuyuasdadsadiy.vaiwan.cn","127.0.0.1:80");
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 502 Bad Gateway\r\n");
        sb.append("Content-Type: text/html\r\n");
        sb.append("Content-Length: ").append(html.length()).append("\r\n");
        sb.append("\r\n").append(html);
        return sb.toString();
    }
}
