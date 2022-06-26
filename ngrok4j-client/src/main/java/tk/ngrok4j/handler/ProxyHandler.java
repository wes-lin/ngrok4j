package tk.ngrok4j.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import tk.ngrok4j.config.NgrokConfig;
import tk.ngrok4j.config.NgrokTunnel;
import tk.ngrok4j.model.StartProxy;
import tk.ngrok4j.util.LogUtils;
import tk.ngrok4j.util.MessageUtils;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Slf4j
public class ProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private ChannelFuture localChannel;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private static final int BUFFER_SIZE = 10240;

    private NgrokConfig config;

    public ProxyHandler(NgrokConfig config) {
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if (!msg.isReadable()) {
            return;
        }
        int rb = msg.readableBytes();
        if (rb > 8) {
            byte[] data = new byte[rb];
            msg.readBytes(data);
            LogUtils.logIn(this.getClass(), new String(data));
            if (localChannel == null) {
                Object response;
                try {
                    response = MessageUtils.getPayload(data);
                } catch (Exception e) {
                    log.error("getPayLoad fail,close proxy...");
                    ctx.close();
                    return;
                }
                if (response instanceof StartProxy) {
                    log.info("=====StartProxy=====");
                    StartProxy startProxy = (StartProxy) response;
                    NgrokTunnel localTunnel = getLocalTunnel(startProxy.getUrl());
                    if (localTunnel == null) {
                        log.error("cannot find localTunnel:{}", startProxy.getUrl());
                        return;
                    }
                    try {
                        Bootstrap b = new Bootstrap();
                        b.group(group)
                                .channel(NioSocketChannel.class)
                                .option(ChannelOption.TCP_NODELAY, false)
                                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(BUFFER_SIZE))
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    protected void initChannel(SocketChannel ch) {
                                        ChannelPipeline p = ch.pipeline();
                                        p.addLast(new FetchDataHandler(ctx.channel()));
                                    }
                                });
                        localChannel = b.connect(localTunnel.getLhost(), localTunnel.getLport()).sync();
                        log.info("connect local port：" + localChannel.channel().localAddress());
                        localChannel.channel().closeFuture().addListener((ChannelFutureListener) t -> {
                            log.info("disconnect local port：" + localChannel.channel().localAddress());
                            ctx.close();
                        });
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        String error = buildErrorMsg(localTunnel);
                        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(error, CharsetUtil.UTF_8));
                    }
                }
            } else {
                log.info("ProxyHandler write message to local port " + localChannel.channel().localAddress());
                localChannel.channel().writeAndFlush(Unpooled.wrappedBuffer(data));
            }
        }
    }

    private NgrokTunnel getLocalTunnel(String url) {
        for (NgrokTunnel ngrokTunnel : config.getTunnels()) {
            if (url.equals(ngrokTunnel.getUrl())) {
                return ngrokTunnel;
            }
        }
        return null;
    }

    private String buildErrorMsg(NgrokTunnel localTunnel) {
        String html = String.format("<html><body style=\"background-color: #97a8b9\"><div style=\"margin:auto; width:400px;padding: 20px 60px; background-color: #D3D3D3; border: 5px solid maroon;\"><h2>Tunnel %s unavailable</h2><p>Unable to initiate connection to <strong>%s</strong>. This port is not yet available for web server.</p>",
                localTunnel.getUrl(), localTunnel.getLhost() + ":" + localTunnel.getLport());
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.0 502 Bad Gateway\r\n");
        sb.append("Content-Type: text/html\r\n");
        sb.append("Content-Length: ").append(html.length()).append("\r\n");
        sb.append("\r\n").append(html);
        return sb.toString();
    }
}
