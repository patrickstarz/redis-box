package com.rick.redisbox.jedis;

import com.rick.redisbox.connection.Connection;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @author rick
 * @date 2019/2/20
 */
public class JedisManager {

    public static Jedis connect(Connection connection) {
        Jedis jedis = new Jedis(connection.getConnHost(), connection.getConnPort());
        if (StringUtils.isNotEmpty(connection.getConnAuth())) {
            jedis.auth(connection.getConnAuth());
        }
        if (jedis.isConnected()) {
            return jedis;
        }
        return null;
    }
}
