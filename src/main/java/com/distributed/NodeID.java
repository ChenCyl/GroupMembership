package com.distributed;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class NodeID implements Serializable {
    private InetAddress inetAddress;
    private int port;
//    private long timestamp; // System.current...

    public NodeID(InetAddress ipAddress, int port) {
        this.inetAddress = ipAddress;
        this.port = port;
//        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "NodeID{" +
                "inetAddress=" + inetAddress +
                ", port=" + port +
                '}';
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
