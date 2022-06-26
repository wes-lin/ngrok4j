package net.coding.ngrok4j;

import net.coding.ngrok4j.codec.JsonDecoder;
import net.coding.ngrok4j.codec.JsonEncoder;
import net.coding.ngrok4j.config.NgrokConfig;
import net.coding.ngrok4j.handler.ControlHandler;
import net.coding.ngrok4j.handler.HeartBeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Slf4j
public class NgrokClient {

    private NgrokConfig config;
    private ChannelFuture channelFuture;
    private NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

    public NgrokClient(NgrokConfig config) {
        this.config = config;
    }

    public void start() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            SSLEngine engine = SslContextBuilder.forClient()
                                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                                    .build()
                                    .newEngine(ch.alloc());
                            ChannelPipeline p = ch.pipeline();
                            p.addFirst(new SslHandler(engine, false));
                            p.addLast(new LoggingHandler(LogLevel.INFO));
                            //length+ content encoder
                            p.addLast(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN, 8, 0, false));
                            //encoder
                            p.addLast(new JsonEncoder());
                            //length + content decoder
                            p.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 10240, 0, 8, 0, 8, true));
                            //decoder
                            p.addLast(new JsonDecoder());
                            //ping
                            p.addLast(new IdleStateHandler(5, 20, 0, TimeUnit.SECONDS));
                            p.addLast(new HeartBeatHandler());
                            p.addLast(new ControlHandler(config));
                        }
                    });
            channelFuture = bootstrap.connect(config.getServerAddr(), config.getServerPort()).sync();
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) t ->
                    log.info("disconnect to remote address " + channelFuture.channel().remoteAddress())
            ).sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void close() {
        if (channelFuture != null) {
            channelFuture.channel().close();
        }
        eventExecutors.shutdownGracefully();
    }

}
