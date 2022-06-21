package app.vercel.ngrok4j.codec;

import app.vercel.ngrok4j.model.Message;
import app.vercel.ngrok4j.model.MsgType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
@Log4j2
public class JsonEncoder extends MessageToByteEncoder<Object>{

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(new PropertyNamingStrategies.UpperCamelCaseStrategy())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg != null) {
            MsgType msgType = MsgType.getMsgType(msg.getClass());
            if (msgType != null) {
                Message message = new Message(msgType,msg);
                log.info(message);
                out.writeBytes(mapper.writeValueAsBytes(message));
                return;
            }
        }
        out.writeBytes(mapper.writeValueAsBytes(msg));
    }

}
