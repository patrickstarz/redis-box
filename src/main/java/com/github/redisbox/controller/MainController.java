package com.github.redisbox.controller;

import com.github.redisbox.connection.ConnectionManager;
import com.github.redisbox.common.Component;
import com.github.redisbox.common.DataType;
import com.github.redisbox.connection.Connection;
import com.github.redisbox.connection.FileConnectionManager;
import com.github.redisbox.jedis.JedisManager;
import com.github.redisbox.utils.ToastUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class MainController {
    public static List<Connection> connections;
    public static Map<String, Jedis> jedisMap = new HashMap<>();
    Image k = new Image("file:/pics/k.png");
    Image l = new Image("file:/pics/l.png");
    Image s = new Image("file:/pics/s.png");
    Image h = new Image("file:/pics/h.png");
    Image z = new Image("file:/pics/z.png");
    Image n = new Image("file:/pics/n.png");

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
                    Jedis jedis = jedisMap.get(connection.getId() + "");
                    if (jedis == null) {
                        jedis = JedisManager.connect(connection);
                        if (jedis != null && jedis.isConnected()) {
                            jedisMap.put(connection.getId() + "", jedis);

                            openNewTab(connection, jedis);
                        } else {
//                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
                        }
                    } else {
                        if (!jedis.isConnected()) {
                            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection has been expired");
                            jedis = JedisManager.connect(connection);
                            if (jedis != null && jedis.isConnected()) {
                                jedisMap.put(connection.getId() + "", jedis);
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

        TreeView treeView = new TreeView();
        TreeItem root = new TreeItem(connection.getConnName());
        root.setExpanded(true);
        treeView.setRoot(root);
        treeView.getStyleClass().add("treeview");
        for (int i = 0; i < 16; i++) {
            TreeItem item = new TreeItem(i);
            root.getChildren().add(item);
        }

        //点击树形图
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    TreeItem<Integer> selectedItem = (TreeItem<Integer>) treeView.getSelectionModel().getSelectedItem();
                    int level = treeView.getTreeItemLevel(selectedItem);

                    onTreeClick(selectedItem, level, jedis, hbox);
                }
            }
        });

        hbox.getChildren().add(treeView);

//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.prefWidthProperty().bind(connectionTabPanel.prefWidthProperty().subtract(treeView.prefWidthProperty()));
//        hbox.getChildren().add(scrollPane);

        //鼠标变为竖线
        EventHandler eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                if (x >= treeView.getWidth() - 2 && x <= treeView.getWidth() + 2) {
                    connectionTabPanel.setCursor(Cursor.H_RESIZE);
                } else {
                    connectionTabPanel.setCursor(Cursor.DEFAULT);
                }
            }
        };
        treeView.setOnMouseMoved(eventHandler);
        hbox.setOnMouseMoved(eventHandler);
        //鼠标拖拽
        hbox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                event.getEventType();

                treeView.setPrefWidth(x);

                connectionTabPanel.setCursor(Cursor.DEFAULT);
            }
        });

        tab.setContent(hbox);
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                jedis.close();

                String id = tab.getId();
                jedisMap.remove(id);
            }
        });

        connectionTabPanel.getTabs().add(tab);
    }

    public void onTreeClick(TreeItem treeItem, int itemLevel, Jedis jedis, HBox hbox) {
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
                    item.setGraphic(new ImageView(getImageByType(jedis, key)));
                    treeItem.getChildren().add(item);
                }
            }
        }
        //data key节点
        if (itemLevel == 2) {
            onDataItemClick(treeItem, jedis, hbox);
        }
    }

    private Image getImageByType(Jedis jedis, String key) {
        String type = jedis.type(key);
        DataType dataType = DataType.getByType(type);

        Image image;
        switch (dataType) {
            case KEY_VALUE:
                image = k;
                break;
            case LIST:
                image = l;
                break;
            case SET:
                image = s;
                break;
            case HASH:
                image = h;
                break;
            case ZSET:
                image = z;
                break;
            default:
                image = n;
                break;
        }
        return image;
    }

    private void onDataItemClick(TreeItem treeItem, Jedis jedis, HBox hbox) {
        String key = (String) treeItem.getValue();
        String type = jedis.type(key);
        DataType dataType = DataType.getByType(type);
        switch (dataType) {
            case KEY_VALUE:
                onKeyValueClick(treeItem, jedis, hbox);
                break;
            case LIST:
                onListClick(treeItem, jedis, hbox);
                break;
            case SET:
                onSetClick(treeItem, jedis, hbox);
                break;
            case HASH:
                onHashClick(treeItem, jedis, hbox);
                break;
            case ZSET:
                onZSetClick(treeItem, jedis, hbox);
                break;
            default:
//                onKeyValueClick(treeItem, jedis);
                break;
        }
    }

    private void onKeyValueClick(TreeItem treeItem, Jedis jedis, HBox hbox) {

        ScrollPane scrollPane = new ScrollPane();
    }

    private void onListClick(TreeItem treeItem, Jedis jedis, HBox hbox) {
    }

    private void onSetClick(TreeItem treeItem, Jedis jedis, HBox hbox) {
    }

    private void onHashClick(TreeItem treeItem, Jedis jedis, HBox hbox) {
    }

    private void onZSetClick(TreeItem treeItem, Jedis jedis, HBox hbox) {
    }

    @FXML
    protected void onExit() {
        Platform.exit();
    }
}
