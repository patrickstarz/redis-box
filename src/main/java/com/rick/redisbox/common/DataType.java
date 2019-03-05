package com.rick.redisbox.common;

public enum DataType {
    KEY_VALUE("string"),
    LIST("list"),
    SET("set"),
    HASH("hash"),
    ZSET("zset"),
    NONE("none");

    private String code;

    DataType(String code) {
        this.code = code;
    }

    public static DataType getByType(String type) {
        DataType[] values = DataType.values();
        for (DataType dataType : values) {
            if (type.equals(dataType.code)) {
                return dataType;
            }
        }
        return NONE;
    }
}
