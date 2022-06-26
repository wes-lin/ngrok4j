package tk.ngrok4j.util;

import com.fasterxml.jackson.databind.*;
import tk.ngrok4j.model.Message;
import tk.ngrok4j.model.MsgType;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
public class MessageUtils {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(new PropertyNamingStrategies.UpperCamelCaseStrategy())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public static Object getPayload(byte[] data) throws Exception {
        JsonNode node = mapper.readTree(data);
        JsonNode payloadNode = node.get("Payload");
        String msgType = node.get("Type").asText();
        Class clazz = MsgType.getMsgClass(msgType);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown MsgType:" + msgType);
        }
        return mapper.convertValue(payloadNode, clazz);
    }

    public static byte[] getPayloadByte(Object payload) throws Exception {
        if (payload == null) {
            return null;
        }
        MsgType msgType = MsgType.getMsgType(payload.getClass());
        if (msgType == null) {
            throw new IllegalArgumentException("Unknown PayloadType:" + payload.getClass());
        }
        Message message = new Message(msgType, payload);
        return mapper.writeValueAsBytes(message);
    }
}
