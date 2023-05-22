package com.github.redisbox.controller;

import com.github.redisbox.common.Component;
import com.github.redisbox.connection.Connection;
import com.github.redisbox.connection.ConnectionManager;
import com.github.redisbox.connection.FileConnectionManager;
import com.github.redisbox.jedis.JedisManager;
import com.github.redisbox.utils.ToastUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author rick
 * @date 2019/2/26
 */
public class EditConnectionController {
    @FXML
    private Button btnConfirmNewConn;
    @FXML
    private TextField txtConnId;
    @FXML
    private TextField txtConnName;
    @FXML
    private TextField txtConnHost;
    @FXML
    private TextField txtConnPort;
    @FXML
    private TextField txtConnAuth;

    @FXML
    private boolean onTestConn() {
        String host = txtConnHost.getText();
        int port = Integer.parseInt(txtConnPort.getText());
        String auth = txtConnAuth.getText();

        return JedisManager.testConnection(host, port, auth);
    }

    @FXML
    private void onConfirmEditConn() {
        long id = Long.parseLong(txtConnId.getText());
        String name = txtConnName.getText();
        String host = txtConnHost.getText();
        int port = Integer.parseInt(txtConnPort.getText());
        String auth = txtConnAuth.getText();

        Connection connection = new Connection(name, host, port, auth);
        connection.setId(id);
        connection.setSort(System.currentTimeMillis());  //sort to latest
        try {
            ConnectionManager manager = new FileConnectionManager();
            manager.addOrUpdate(connection);

            //reload menus
            Component.mainController.loadData();

            Stage stage = (Stage) btnConfirmNewConn.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Add failed");
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) btnConfirmNewConn.getScene().getWindow();
        stage.close();
    }

    public void initData(Connection connection) {
        txtConnId.setText(connection.getId() + "");
        txtConnName.setText(connection.getConnName());
        txtConnHost.setText(connection.getConnHost());
        txtConnPort.setText(connection.getConnPort() + "");
        txtConnAuth.setText(connection.getConnAuth());
    }
}