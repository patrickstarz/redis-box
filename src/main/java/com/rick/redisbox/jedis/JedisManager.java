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
}
