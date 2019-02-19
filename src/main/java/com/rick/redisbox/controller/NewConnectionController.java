package com.rick.redisbox.controller;

import com.rick.redisbox.utils.ToastUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

/**
 * @author rick
 * @date 2019/2/11
 */
public class NewConnectionController {
    @FXML
    private Button btnConfirmNewConn;
    @FXML
    private TextField txtConnName;
    @FXML
    private TextField txtConnHost;
    @FXML
    private TextField txtConnPort;
    @FXML
    private TextField txtConnAuth;

    @FXML
    private void onConfirmNewConn() {
        System.out.println(111111);
    }

    @FXML
    private void onCancelNewConn() {
        Stage stage = (Stage) btnConfirmNewConn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onTestNewConn() {
        //TODO ssh and ssl
        String name = txtConnName.getText();
        String host = txtConnHost.getText();
        int port = Integer.parseInt(txtConnPort.getText());
        String auth = txtConnAuth.getText();

        try {
            Jedis jedis = new Jedis(host, port);
            if (StringUtils.isNotEmpty(auth)) {
                jedis.auth(auth);
            }
            if (jedis.isConnected()) {
                ToastUtils.alert(Alert.AlertType.INFORMATION, "Tip", "", "Successful Connected to server!");
            } else {
                ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.alert(Alert.AlertType.ERROR, "Tip", "", "Connection failed");
        }
    }
}
