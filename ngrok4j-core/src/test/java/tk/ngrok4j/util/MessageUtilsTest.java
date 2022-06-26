package tk.ngrok4j.util;

import org.junit.Test;
import tk.ngrok4j.model.Ping;
import tk.ngrok4j.model.Pong;

import static org.junit.Assert.*;

/**
 * @Auther: WesLin
 * @Date: 2022/6/26
 * @Description:
 */
public class MessageUtilsTest {

    @Test
    public void getPayload() throws Exception {
        try {
            String json = "{\"Type\":\"Pxng\",\"Payload\":{}}";
            MessageUtils.getPayload(json.getBytes());
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage().indexOf("Unknown MsgType") > -1);
        }

        String json = "{\"Type\":\"Ping\",\"Payload\":{}}";
        Object payload = MessageUtils.getPayload(json.getBytes());
        assertTrue(payload instanceof Ping);
    }

    @Test
    public void getPayloadByte() throws Exception {
        byte[] payloadByte = MessageUtils.getPayloadByte(null);
        assertNull(payloadByte);

        try {
            MessageUtils.getPayloadByte(new PayloadTest());
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertTrue(e.getMessage().indexOf("Unknown PayloadType") > -1);
        }

        byte[] data = MessageUtils.getPayloadByte(new Pong());
        assertNotNull(data);
    }

    private static class PayloadTest {

    }
}