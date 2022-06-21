package app.vercel.ngrok4j.util;

import app.vercel.ngrok4j.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
public class MessageUtils {

    private static final ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(new PropertyNamingStrategies.UpperCamelCaseStrategy())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);

    public static String toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public static String regProxy(String clientId) throws JsonProcessingException{
        RegProxy regProxy = new RegProxy();
        regProxy.setClientId(clientId);
        return toJson(new Message<>(MsgType.RegProxy,regProxy));
    }

    public static Response getResponse(String msg) throws JsonProcessingException {
        JsonNode node = mapper.readTree(msg);
        MsgType msgType = MsgType.valueOf(node.get("Type").asText());
        JsonNode payloadNode = node.get("Payload");
        Response response = new Response();
        response.setType(msgType);
        switch (msgType) {
            case AuthResp:
                AuthResponse payload = getPayload(payloadNode, AuthResponse.class);
                response.setPayload(payload);
                break;
            case ReqProxy:
            case Pong:
                response.setPayload(mapper.createObjectNode());
                break;
            case NewTunnel:
                NewTunnel newTunnel = getPayload(payloadNode, NewTunnel.class);
                response.setPayload(newTunnel);
                break;
        }
        return response;
    }

    public static <T> T getPayload(JsonNode payloadNode, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(payloadNode.toString(), clazz);
    }
}
