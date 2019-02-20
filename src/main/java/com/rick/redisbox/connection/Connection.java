package com.rick.redisbox.connection;

import java.io.Serializable;

/**
 * @author rick
 * @date 2019/2/19
 */
public class Connection implements Serializable {
    private long id;
    private String connName;
    private String connHost;
    private int connPort;
    private String connAuth;
    private long sort;

    public Connection() {}

    public Connection(String connName, String connHost, int connPort, String connAuth) {
        this.connName = connName;
        this.connHost = connHost;
        this.connPort = connPort;
        this.connAuth = connAuth;
    }

    public Connection(long id, String connName, String connHost, int connPort, String connAuth, long sort) {
        this.id = id;
        this.connName = connName;
        this.connHost = connHost;
        this.connPort = connPort;
        this.connAuth = connAuth;
        this.sort = sort;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getConnName() {
        return connName;
    }

    public void setConnName(String connName) {
        this.connName = connName;
    }

    public String getConnHost() {
        return connHost;
    }

    public void setConnHost(String connHost) {
        this.connHost = connHost;
    }

    public int getConnPort() {
        return connPort;
    }

    public void setConnPort(int connPort) {
        this.connPort = connPort;
    }

    public String getConnAuth() {
        return connAuth;
    }

    public void setConnAuth(String connAuth) {
        this.connAuth = connAuth;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public long getSort() {
        return sort;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Connection)) {
            return false;
        }
        Connection that = (Connection) obj;
        return this.id == that.id;
    }

    @Override
    public String toString() {
        return this.connName;
    }
}