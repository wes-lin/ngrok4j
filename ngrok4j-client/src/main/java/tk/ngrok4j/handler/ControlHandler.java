package tk.ngrok4j.handler;

import com.google.common.base.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import tk.ngrok4j.config.NgrokConfig;
import tk.ngrok4j.config.NgrokTunnel;
import tk.ngrok4j.model.*;
import tk.ngrok4j.util.ByteBufUtils;
import tk.ngrok4j.util.LogUtils;
import tk.ngrok4j.util.MessageUtils;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Slf4j
public class ControlHandler extends ChannelInboundHandlerAdapter {

    private String clientId;
    private NioEventLoopGroup group = new NioEventLoopGroup();

    public final Map<String, NgrokTunnel> REGISTERED_TUNNEL_MAP = new ConcurrentHashMap<>();

    private NgrokConfig config;

    public ControlHandler(NgrokConfig config) {
        this.config = config;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Auth auth = new Auth();
        auth.setVersion("2");
        auth.setMmVersion("1.7");
        auth.setUser("");
        auth.setPassword("");
        auth.setOS(System.getProperty("os.name"));
        auth.setArch(System.getProperty("os.arch"));
        auth.setClientId("");
        ctx.writeAndFlush(auth);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof AuthResponse) {
            AuthResponse response = (AuthResponse) msg;
            clientId = response.getClientId();
            for (NgrokTunnel tunnel : config.getTunnels()) {
                ReqTunnel reqTunnel = new ReqTunnel();
                String reqId = UUID.randomUUID().toString()
                        .toLowerCase().replace("-", "")
                        .substring(0, 8);
                reqTunnel.setReqId(reqId);
                reqTunnel.setRemotePort(tunnel.getRemotePort());
                reqTunnel.setSubdomain(tunnel.getSubdomain());
                reqTunnel.setProtocol(tunnel.getProtocol().name());
                ctx.channel().writeAndFlush(reqTunnel);
                REGISTERED_TUNNEL_MAP.put(reqId, tunnel);
            }
        } else if (msg instanceof ReqProxy) {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, 1024 * 1024 * 8))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws SSLException {
                            SSLEngine engine = SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                    .build()
                                    .newEngine(ch.alloc());
                            ChannelPipeline p = ch.pipeline();
                            SslHandler sslHandler = new SslHandler(engine, false);
                            sslHandler.setWrapDataSize(32 * 1024);
                            p.addFirst(sslHandler);
                            p.addLast(new ProxyHandler(config));
                        }
                    });
            ChannelFuture remoteChannel = b.connect(ctx.channel().remoteAddress())
                    .addListener((ChannelFutureListener) channelFuture -> {
                        //send reqProxy request
                        RegProxy regProxy = new RegProxy();
                        regProxy.setClientId(clientId);
                        byte[] data = MessageUtils.getPayloadByte(regProxy);
                        LogUtils.logOut(this.getClass(), new String(data));
                        channelFuture.channel().writeAndFlush(ByteBufUtils.pack(data));
                    }).sync();
            log.info("connect to proxy address {}", remoteChannel.channel().remoteAddress());
            remoteChannel.channel().closeFuture()
                    .addListener((ChannelFutureListener) channelFuture -> {
                        log.info("disconnect to proxy address " + remoteChannel.channel().remoteAddress());
                    });
        } else if (msg instanceof NewTunnel) {
            NewTunnel newTunnel = (NewTunnel) msg;
            if (Strings.isNullOrEmpty(newTunnel.getError())) {
                NgrokTunnel ngrokTunnel = REGISTERED_TUNNEL_MAP.get(newTunnel.getReqId());
                ngrokTunnel.setUrl(newTunnel.getUrl());
                log.info("Tunnel :{} register success", ngrokTunnel.getUrl());
            } else {
                log.error("Tunnel :{} register fail error:{}", newTunnel.getUrl(), newTunnel.getError());
            }
        }
        //must release buffer
        ReferenceCountUtil.release(msg);
    }
}
