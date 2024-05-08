package com.github.redisbox;

import com.github.redisbox.common.Component;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;

public class RedisBoxApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Redis Box");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/stylesheet.css");
        primaryStage.setScene(scene);
        primaryStage.setOnShown(event -> {
            // 加载左侧树形图
            Component.mainController = loader.getController();
            Component.mainController.loadData();
        });
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}
