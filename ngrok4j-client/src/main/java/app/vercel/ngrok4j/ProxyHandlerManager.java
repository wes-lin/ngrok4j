package app.vercel.ngrok4j;

import app.vercel.ngrok4j.handler.ProxyHandler;
import app.vercel.ngrok4j.model.ReqProxy;
import app.vercel.ngrok4j.model.ReqTunnel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.util.UUID;

/**
 * @Auther: WesLin
 * @Date: 2022/6/21
 * @Description:
 */
@Data
@Log4j2
public class ProxyHandlerManager {

    private NioEventLoopGroup group = new NioEventLoopGroup();

    private String clientId;
    private ChannelFuture remoteChannel;
    private Channel localChannel;

    public ProxyHandlerManager(String clientId) {
        this.clientId = clientId;
    }

    public ReqTunnel buildReqTunnel(){
        ReqTunnel reqTunnel = new ReqTunnel();
        String subdomain = "iyuuyuasdadsadiy";
        String reqId = UUID.randomUUID().toString()
                .toLowerCase().replace("-", "")
                .substring(0, 8);
        reqTunnel.setReqId(reqId);
        reqTunnel.setSubdomain(subdomain);
        reqTunnel.setProtocol("http");
        return reqTunnel;
    }

    public void buildRemoteChannel() throws Exception{
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
    }
}
