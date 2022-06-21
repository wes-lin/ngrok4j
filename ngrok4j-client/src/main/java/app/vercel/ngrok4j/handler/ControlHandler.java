package app.vercel.ngrok4j.handler;

import app.vercel.ngrok4j.model.*;
import app.vercel.ngrok4j.util.ByteBufUtils;
import app.vercel.ngrok4j.util.LogUtils;
import app.vercel.ngrok4j.util.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.util.UUID;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Log4j2
public class ControlHandler extends ChannelInboundHandlerAdapter {

    private String clientId;
    private ChannelFuture remoteChannel;
    private NioEventLoopGroup group = new NioEventLoopGroup();

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
            ReqTunnel reqTunnel = new ReqTunnel();
            String subdomain = "iyuuyuasdadsadiy";
            String reqId = UUID.randomUUID().toString()
                    .toLowerCase().replace("-", "")
                    .substring(0, 8);
            reqTunnel.setReqId(reqId);
            reqTunnel.setSubdomain(subdomain);
            reqTunnel.setProtocol("http");
            ctx.channel().writeAndFlush(reqTunnel);
        } else if (msg instanceof ReqProxy) {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws SSLException {
                            SSLEngine engine = SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                    .build()
                                    .newEngine(ch.alloc());
                            ChannelPipeline p = ch.pipeline();
                            p.addFirst(new SslHandler(engine, false));
                            p.addLast(new ProxyHandler(clientId));
                        }
                    });
            remoteChannel = b.connect("vaiwan.com", 443).sync();
            log.info("connect to proxy address {}", remoteChannel.channel().remoteAddress());
            remoteChannel.channel().closeFuture()
                    .addListener((ChannelFutureListener) channelFuture ->
                            log.info("disconnect to proxy address " + remoteChannel.channel().remoteAddress())
                    );
        } else if (msg instanceof NewTunnel) {
            NewTunnel newTunnel = (NewTunnel) msg;
            if (StringUtils.isNotEmpty(newTunnel.getError())) {
                log.error("Tunnel register fail error:{}", newTunnel.getError());
            } else {
                log.info("Tunnel register success");
            }
        }
    }

}
