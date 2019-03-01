package com.rick.redisbox.connection;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 把连接数据存入一个文件
 *
 * @author rick
 * @date 2019/2/19
 */
public class FileConnectionManager implements ConnectionManager {
    private String storeFilePath = "connections.json";

    private String readFile() {
        String path = FileConnectionManager.class.getClassLoader().getResource(storeFilePath).getPath();
//        FileUtils.readFileToString()
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public void saveAll(List<Connection> list) throws IOException {
        String path = FileConnectionManager.class.getClassLoader().getResource(storeFilePath).getPath();
        File file = new File(path);
        if (list == null) {
            list = new ArrayList<>();
        }

        synchronized (FileConnectionManager.class) {
            String json = JSONArray.toJSONString(list);
            FileUtils.writeStringToFile(file, json, "UTF-8");
        }
    }

    @Override
    public List<Connection> getAll() {
        String content = readFile();
        if (StringUtils.isNotEmpty(content)) {
            synchronized (FileConnectionManager.class) {
                JSONArray array = JSONArray.parseArray(content);
                return array.toJavaList(Connection.class);
            }
        }
        return null;
    }

    @Override
    public void addOrUpdate(Connection connection) throws IOException {
        List<Connection> list = getAll();
        if (list != null) {
            for (Connection connection2 : list) {
                if (connection2.equals(connection)) {
                    list.remove(connection2);
                    break;
                }
            }
            list.add(connection);
        }
        saveAll(list);
    }

    @Override
    public void delete(Connection connection) throws IOException {
        List<Connection> list = getAll();
        if (list != null) {
            for (Connection connection2 : list) {
                if (connection2.equals(connection)) {
                    list.remove(connection2);
                    break;
                }
            }
        }
        saveAll(list);
    }

    @Override
    public void deleteAll() throws IOException {
        saveAll(new ArrayList<>());
    }
}
