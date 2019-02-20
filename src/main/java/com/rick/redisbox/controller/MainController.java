package com.rick.redisbox.controller;

import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.connection.ConnectionManager;
import com.rick.redisbox.connection.FileConnectionManager;
import com.rick.redisbox.jedis.JedisManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController implements EventHandler {
    public static List<Connection> connections;
    public static Map<Long, Jedis> jedisMap = new HashMap<>();

    @FXML
    private MenuItem menu_new_connection;
    @FXML
    private ListView left_list;

    @FXML
    protected void onNewConnection() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/new_connection.fxml"));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);  //模态
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadTree() {
        ConnectionManager manager = new FileConnectionManager();
        connections = manager.getAll();

        ObservableList list = FXCollections.observableArrayList(connections);
        left_list.setItems(list);

        //添加双击事件
        left_list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    int index = left_list.getSelectionModel().getSelectedIndex();

                    Connection connection = (Connection) left_list.getItems().get(index);
                    Jedis jedis = jedisMap.get(connection.getId());
                    if (jedis == null) {
                        jedis = JedisManager.connect(connection);
                        if (jedis != null) {
                            jedisMap.put(connection.getId(), jedis);
                            //TODO 展开tree
                        }
                    } else {
                        //TODO 展开tree
                    }

                    //加载tree
                    left_list.getSelectionModel().getSelectedItem();
                }
            }
        });
    }

    @Override
    public void handle(Event event) {

    }
}
