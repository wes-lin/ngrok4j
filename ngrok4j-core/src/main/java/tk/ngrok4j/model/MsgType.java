package tk.ngrok4j.model;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @Auther: WesLin
 * @Date: 2022/6/8
 * @Description:
 */
public enum MsgType {
    Auth,
    AuthResp,
    ReqProxy,
    RegProxy,
    ReqTunnel,
    Pong,
    Ping,
    NewTunnel,
    StartProxy;

    private static final Map<MsgType, Class> MAP = new ImmutableMap.Builder<MsgType, Class>()
            .put(AuthResp, AuthResponse.class)
            .put(NewTunnel, NewTunnel.class)
            .put(Pong, Pong.class)
            .put(Ping, Ping.class)
            .put(ReqProxy, ReqProxy.class)
            .put(Auth, Auth.class)
            .put(ReqTunnel, ReqTunnel.class)
            .put(StartProxy, StartProxy.class)
            .put(RegProxy, RegProxy.class)
            .build();

    public static Class getMsgClass(String type) {
        try {
            MsgType msgType = MsgType.valueOf(type);
            return MAP.get(msgType);
        } catch (Exception e) {
            return null;
        }
    }

    public static MsgType getMsgType(Class clazz) {
        for (Map.Entry<MsgType, Class> entry : MAP.entrySet()) {
            if (entry.getValue().equals(clazz)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
