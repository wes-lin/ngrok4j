package app.vercel.ngrok4j.handler;

import app.vercel.ngrok4j.codec.JsonDecoder;
import app.vercel.ngrok4j.codec.JsonEncoder;
import app.vercel.ngrok4j.model.MsgType;
import app.vercel.ngrok4j.model.RegProxy;
import app.vercel.ngrok4j.model.Response;
import app.vercel.ngrok4j.model.StartProxy;
import app.vercel.ngrok4j.util.ByteBufUtils;
import app.vercel.ngrok4j.util.LogUtils;
import app.vercel.ngrok4j.util.MessageUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.CharsetUtil;
import lombok.extern.log4j.Log4j2;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
@Log4j2
public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private String clientId;
    private ChannelFuture localChannel;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private static final int BUFFER_SIZE = 10240;

    public ProxyHandler(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String regProxy = MessageUtils.regProxy(clientId);
        LogUtils.logOut(this.getClass(),regProxy);
        ctx.channel().writeAndFlush(ByteBufUtils.pack(regProxy));
//        ChannelPipeline pipeline = ctx.pipeline();
//        RegProxy regProxy = new RegProxy();
//        regProxy.setClientId(clientId);
//        ctx.writeAndFlush(regProxy);
        log.info("{}:aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",this);
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("error-----:{}", cause.getMessage());
//        String error = buildErrorMsg();
//        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(error, CharsetUtil.UTF_8));
//        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            if (msg instanceof StartProxy){
//                if (localChannel == null) {
//                    log.info("=====StartProxy=====");
//                    Bootstrap b = new Bootstrap();
//                    b.group(group)
//                            .channel(NioSocketChannel.class)
//                            .option(ChannelOption.TCP_NODELAY, true)
//                            .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(BUFFER_SIZE))
//                            .handler(new ChannelInitializer<SocketChannel>() {
//                                protected void initChannel(SocketChannel ch) {
//                                    ChannelPipeline p = ch.pipeline();
//                                    p.addLast(new FetchDataHandler(ctx.channel()));
//                                }
//                            });
//                    localChannel = b.connect("127.0.0.1", 80).sync();
//                    log.info("connect local port：" + localChannel.channel().localAddress());
//                    localChannel.channel().closeFuture().addListener((ChannelFutureListener) t -> {
//                        log.info("disconnect local port：" + localChannel.channel().localAddress());
//                    });
//                }
//            }
        ByteBuf byteBuf = (ByteBuf) msg;
        if (!byteBuf.isReadable()){
            return;
        }
        int rb = byteBuf.readableBytes();
        CharSequence charSequence = byteBuf.readCharSequence(rb, Charset.defaultCharset());
            if (localChannel == null) {
                if (rb > 8) {
                    String bufferStr = charSequence.toString();
                    LogUtils.logIn(this.getClass(), bufferStr);
                    try {
                        Response response = MessageUtils.getResponse(bufferStr);
                        if (response.getType() == MsgType.StartProxy) {
                            log.info("=====StartProxy=====");
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
                            localChannel = b.connect("127.0.0.1", 80).sync();
                            log.info("connect local port：" + localChannel.channel().localAddress());
                            localChannel.channel().closeFuture().addListener((ChannelFutureListener) t -> {
                                log.info("disconnect local port：" + localChannel.channel().localAddress());
                            });
                        }
                    } catch (Exception e) {
                        log.error("{} :{}",this,e);
                        String error = buildErrorMsg();
                        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(error, CharsetUtil.UTF_8));
                        ctx.close().sync();
                    }
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
