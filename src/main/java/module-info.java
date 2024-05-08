module redis.box {
    exports com.github.redisbox;
    opens com.github.redisbox.controller;

    opens fxml;
    opens css;
    opens pics;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires commons.io;
    requires fastjson;
    requires org.apache.commons.lang3;
    requires jedis;
}