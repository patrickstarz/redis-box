package com.rick.redisbox.connection;

import java.io.IOException;
import java.util.List;

/**
 * @author rick
 * @date 2019/2/19
 */
public interface ConnectionManager {

    void saveAll(List<Connection> list) throws IOException;

    List<Connection> getAll();

    void addOrUpdate(Connection connection) throws IOException;

    void delete(Connection connection) throws IOException;

    void deleteAll() throws IOException;
}
