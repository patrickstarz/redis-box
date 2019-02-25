package com.rick.redisbox.controller;

import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.connection.ConnectionManager;
import com.rick.redisbox.connection.FileConnectionManager;
import com.rick.redisbox.jedis.JedisManager;
import com.rick.redisbox.utils.ToastUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
    private Menu myConnections;
    @FXML
    private TabPane connectionTabPanel;

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

        for (Connection connection : connections) {
            MenuItem item = new MenuItem(connection.getConnName());
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Jedis jedis = jedisMap.get(connection.getId());
                    if (jedis == null) {
                        jedis = JedisManager.connect(connection);
                        if (jedis != null && jedis.isConnected()) {
                            jedisMap.put(connection.getId(), jedis);

                            Tab tab = new Tab();
                            tab.setId(connection.getId() + "");
                            tab.setText(connection.getConnName());

                            BorderPane borderPane = new BorderPane();
                            VBox vBox = new VBox();
                            vBox.setPrefWidth(200.0);
                            borderPane.setLeft(vBox);

                            borderPane.setCenter(new ScrollPane());
                            tab.setContent(borderPane);
                            connectionTabPanel.getTabs().add(tab);
                        } else {
                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
                            //打开编辑窗口
                        }
                    } else {
                        if (!jedis.isConnected()) {
                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection has been expired");
                            jedis = JedisManager.connect(connection);
                            if (jedis != null && jedis.isConnected()) {
                                jedisMap.put(connection.getId(), jedis);
                            } else {
                                ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
                                //打开编辑窗口
                                return;
                            }
                        }
                        ObservableList tabs = connectionTabPanel.getTabs();
                        for (Object o : tabs) {
                            Tab tab = (Tab) o;
                            if (tab.getId().equals(connection.getId() + "")) {
                                connectionTabPanel.getSelectionModel().select(tab);
                                break;
                            }
                        }
                    }
                }
            });
            myConnections.getItems().add(item);
        }
    }

    @Override
    public void handle(Event event) {

    }
}
