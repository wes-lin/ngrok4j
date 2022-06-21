package app.vercel.ngrok4j.codec;

import app.vercel.ngrok4j.model.MsgType;
import com.fasterxml.jackson.databind.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * @Auther: WesLin
 * @Date: 2022/6/16
 * @Description:
 */
@Log4j2
public class JsonDecoder extends ByteToMessageDecoder {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(new PropertyNamingStrategies.UpperCamelCaseStrategy())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readableBytes();
        byte[] data = new byte[len];
        in.readBytes(data);
        log.info(new String(data));
        JsonNode node = mapper.readTree(data);
        MsgType msgType = MsgType.valueOf(node.get("Type").asText());
        JsonNode payloadNode = node.get("Payload");
        Class clazz = MsgType.getMsgClass(msgType);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown MsgType:" + msgType);
        }
        out.add(mapper.convertValue(payloadNode, clazz));
    }
}
