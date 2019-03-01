package com.rick.redisbox.controller;

import com.rick.redisbox.common.Component;
import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.connection.ConnectionManager;
import com.rick.redisbox.connection.FileConnectionManager;
import com.rick.redisbox.jedis.JedisManager;
import com.rick.redisbox.utils.ToastUtils;
import javafx.application.Platform;
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
    private MenuItem menuNewConnection;
    @FXML
    private Menu menuOpenConnections;
    @FXML
    private Menu menuEditConnections;
    @FXML
    private TabPane connectionTabPanel;

    @FXML
    protected void onNewConnection() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/new_connection.fxml"));
        Component.newConnectionController = loader.getController();
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);  //模态
        stage.setResizable(false);
        stage.setTitle("New Connection");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadData() {
        menuOpenConnections.getItems().clear();
        menuEditConnections.getItems().clear();

        ConnectionManager manager = new FileConnectionManager();
        connections = manager.getAll();

        for (Connection connection : connections) {
            //为Open菜单添加子菜单
            MenuItem item = new MenuItem(connection.getConnName());
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Jedis jedis = jedisMap.get(connection.getId());
                    if (jedis == null) {
                        jedis = JedisManager.connect(connection);
                        if (jedis != null && jedis.isConnected()) {
                            jedisMap.put(connection.getId(), jedis);

                            openNewTab(connection, jedis);
                        } else {
//                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
                        }
                    } else {
                        if (!jedis.isConnected()) {
                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection has been expired");
                            jedis = JedisManager.connect(connection);
                            if (jedis != null && jedis.isConnected()) {
                                jedisMap.put(connection.getId(), jedis);
                            } else {
                                ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
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
            menuOpenConnections.getItems().add(item);

            //为编辑菜单添加子菜单
            MenuItem item2 = new MenuItem(connection.getConnName());
            item2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/edit_connection.fxml"));
                        Parent root = loader.load();
                        EditConnectionController controller = loader.getController();
                        Component.editConnectionController = controller;

                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);  //模态
                        stage.setResizable(false);
                        stage.setTitle("Edit Connection");
                        stage.setScene(new Scene(root));
                        stage.show();

                        //设置文本框内容
                        controller.initData(connection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            menuEditConnections.getItems().add(item2);
        }
    }

    public void openNewTab(Connection connection, Jedis jedis) {
        Tab tab = new Tab();
        tab.setId(connection.getId() + "");
        tab.setText(connection.getConnName());

        BorderPane borderPane = new BorderPane();

        BorderPane borderPane2 = new BorderPane();
        borderPane2.setPrefWidth(200.0);

        TreeView treeView = new TreeView();
        TreeItem root = new TreeItem(connection.getConnName());
        treeView.setRoot(root);
        for (int i = 0; i < 16; i++) {
            TreeItem item = new TreeItem(i);
            root.getChildren().add(item);
        }
        borderPane2.setCenter(treeView);

        borderPane.setLeft(borderPane2);
        borderPane.setCenter(new ScrollPane());
        tab.setContent(borderPane);
        connectionTabPanel.getTabs().add(tab);
    }

    @Override
    public void handle(Event event) {

    }

    @FXML
    protected void onExit() {
        Platform.exit();
    }
}
