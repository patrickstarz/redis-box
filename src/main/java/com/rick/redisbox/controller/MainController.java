package com.rick.redisbox.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private MenuItem menu_new_connection;

    @FXML
    protected void onNewConnection() throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("new_connection.fxml"));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);  //模态
        stage.setScene(new Scene(root, 400, 550));
        stage.show();
    }
}
