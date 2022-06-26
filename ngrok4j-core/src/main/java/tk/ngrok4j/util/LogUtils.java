package tk.ngrok4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: WesLin
 * @Date: 2022/6/9
 * @Description:
 */
public class LogUtils {

    private static final String READING = "Reading";
    private static final String WRITING = "Writing";

    public static void logIn(Class clazz, String msg) {
        log(clazz,msg,READING);
    }

    public static void logOut(Class clazz, String msg) {
        log(clazz,msg,WRITING);
    }

    public static void log(Class clazz, String msg, String type) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("{}: {} ", type, msg);
    }
}
