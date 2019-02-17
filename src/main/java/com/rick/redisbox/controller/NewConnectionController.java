package com.rick.redisbox.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * @author rick
 * @date 2019/2/11
 */
public class NewConnectionController {
    @FXML
    private Button btnConfirmNewConn;

    @FXML
    private void onConfirmNewConn() {
        System.out.println(111111);
    }

    @FXML
    private void onCancelNewConn() {
        Stage stage = (Stage)btnConfirmNewConn.getScene().getWindow();
        stage.close();
    }
}
