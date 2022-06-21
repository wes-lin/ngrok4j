package app.vercel.ngrok4j;

import app.vercel.ngrok4j.codec.JsonDecoder;
import app.vercel.ngrok4j.codec.JsonEncoder;
import app.vercel.ngrok4j.handler.ControlHandler;
import app.vercel.ngrok4j.handler.HeartBeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLEngine;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
@Log4j2
@AllArgsConstructor
public class NgrokClient {

    private String serverHost;
    private int serverPort;

    public void start(){
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
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
                            //length+ content encoder
                            p.addLast(new LengthFieldPrepender(ByteOrder.LITTLE_ENDIAN,8,0,false));
                            //encoder
                            p.addLast(new JsonEncoder());
                            //length + content decoder
                            p.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,10240,0,8,0,8,true));
                            //decoder
                            p.addLast(new JsonDecoder());
                            //ping
                            p.addLast(new IdleStateHandler(5, 20, 0, TimeUnit.SECONDS));
                            p.addLast(new HeartBeatHandler());
                            p.addLast(new ControlHandler());
                        }
                    });
            //连接服务端
            ChannelFuture channelFuture = bootstrap.connect(serverHost, serverPort).sync();
            //对通道关闭进行监听
            channelFuture.channel().closeFuture().addListener((ChannelFutureListener) t ->
                    log.info("disconnect to remote address "+channelFuture.channel().remoteAddress())
            ).sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
       NgrokClient client = new NgrokClient("vaiwan.com",443);
       client.start();
    }

}
