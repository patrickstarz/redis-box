package com.rick.redisbox.jedis;

import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.utils.ToastUtils;
import javafx.scene.control.Alert;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @author rick
 * @date 2019/2/20
 */
public class JedisManager {

    public static Jedis connect(Connection connection) {
        try {
            Jedis jedis = new Jedis(connection.getConnHost(), connection.getConnPort());
            if (StringUtils.isNotEmpty(connection.getConnAuth())) {
                jedis.auth(connection.getConnAuth());
            }
            if (jedis.isConnected()) {
                return jedis;
            }
        } catch (Exception e) {
            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
        }
        return null;
    }

    public static boolean testConnection(String host, int port, String auth) {
        try {
            Jedis jedis = new Jedis(host, port);
            if (StringUtils.isNotEmpty(auth)) {
                jedis.auth(auth);
            }
            if (jedis.isConnected()) {
                ToastUtils.alert(Alert.AlertType.INFORMATION, "Tip", "", "Successful Connected to server!");
                return true;
            } else {
                ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
        }
        return false;
    }
}
