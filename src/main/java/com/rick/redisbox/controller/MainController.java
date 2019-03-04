package com.rick.redisbox.controller;

import com.rick.redisbox.common.Component;
import com.rick.redisbox.connection.Connection;
import com.rick.redisbox.connection.ConnectionManager;
import com.rick.redisbox.connection.FileConnectionManager;
import com.rick.redisbox.jedis.JedisManager;
import com.rick.redisbox.utils.ToastUtils;
import com.rick.redisbox.view.DragResizeMod;
import com.rick.redisbox.view.DragResizer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        HBox hbox = new HBox();
        hbox.requestLayout();

        ScrollPane scrollPane = new ScrollPane();

        TreeView treeView = new TreeView();
        TreeItem root = new TreeItem(connection.getConnName());
        root.setExpanded(true);
        treeView.setRoot(root);
        for (int i = 0; i < 16; i++) {
            TreeItem item = new TreeItem(i);
            root.getChildren().add(item);
        }
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TreeItem<Integer> selectedItem = (TreeItem<Integer>) treeView.getSelectionModel().getSelectedItem();
                    int level = treeView.getTreeItemLevel(selectedItem);

                    onTreeClick(selectedItem, level, jedis);
                }
            }
        });
        hbox.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
//                System.out.println(x + ":" + treeView.getWidth());
                if (x >= treeView.getWidth() - 2 && x <= treeView.getWidth() + 2) {
                    connectionTabPanel.setCursor(Cursor.H_RESIZE);
                } else {
                    connectionTabPanel.setCursor(Cursor.DEFAULT);
                }
            }
        });
        hbox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();

                treeView.setPrefWidth(x);
                scrollPane.setPrefWidth(connectionTabPanel.getPrefWidth() - treeView.getPrefWidth());

                connectionTabPanel.setCursor(Cursor.DEFAULT);
            }
        });
        hbox.getChildren().

                add(treeView);

        tab.setContent(hbox);
        connectionTabPanel.getTabs().

                add(tab);
    }

    public void onTreeClick(TreeItem treeItem, int itemLevel, Jedis jedis) {
        Object value = treeItem.getValue();
        //根节点
        if (itemLevel == 0) {
            // do nothing
        }
        //db index节点
        if (itemLevel == 1) {
            if (treeItem.isExpanded()) {
                return;
            }
            int dbIndex = Integer.parseInt(value.toString());
            jedis.select(dbIndex);
            Set<String> keys = jedis.keys("*");
            if (keys == null || keys.size() == 0) {
                ToastUtils.alert(Alert.AlertType.INFORMATION, "Tip", "", "No data in this index!");
            } else {
                for (String key : keys) {
                    TreeItem item = new TreeItem(key);
                    treeItem.getChildren().add(item);
                }
            }
        }
        //data key节点
        if (itemLevel == 2) {

        }
    }

    @Override
    public void handle(Event event) {

    }

    @FXML
    protected void onExit() {
        Platform.exit();
    }
}
