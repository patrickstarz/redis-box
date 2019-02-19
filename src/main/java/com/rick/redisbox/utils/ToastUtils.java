package com.rick.redisbox.utils;

import javafx.scene.control.Alert;

/**
 * @author rick
 * @date 2019/2/19
 */
public class ToastUtils {

    /**
     * 弹窗
     * @param alertType
     * @param title
     * @param headerText
     * @param contextText
     */
    public static void alert(Alert.AlertType alertType, String title, String headerText, String contextText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
    }
}
