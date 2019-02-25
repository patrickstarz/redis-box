package com.rick.redisbox.controller;

import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.connection.ConnectionManager;
import com.rick.redisbox.connection.FileConnectionManager;
import javafx.beans.binding.Bindings;
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
import java.util.ArrayList;
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
    private VBox leftVBox;
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
            myConnections.getItems().add(item);

//            Tab tab = new Tab(connection.getConnName());
//            BorderPane borderPane = new BorderPane();

//            VBox vBox = new VBox();
//            vBox.setPrefWidth(200.0);
//            borderPane.setLeft(vBox);
//
//            borderPane.setCenter(new ScrollPane());
//            tab.setContent(borderPane);
//            connectionTabPanel.getTabs().add(tab);
        }

        //添加双击事件
//        left_list.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
////                    int index = left_list.getSelectionModel().getSelectedIndex();
////                    left_list.getSelectionModel().getSelectedItem();
//
//                    Connection connection = (Connection) left_list.getItems().get(index);
//                    Jedis jedis = jedisMap.get(connection.getId());
//                    if (jedis == null) {
//                        jedis = JedisManager.connect(connection);
//                        if (jedis != null) {
//                            jedisMap.put(connection.getId(), jedis);
//                            //TODO 展开tree
//                            TreeView treeView = new TreeView();
//
//                            TreeItem item = new TreeItem("root");
//                            treeView.setRoot(item);
//
//                        }
//                    } else {
//                        //TODO 展开tree
//                    }
//
//                    //加载tree
//                    left_list.getSelectionModel().getSelectedItem();
//                }
//            }
//        });
    }

    @Override
    public void handle(Event event) {

    }
}
