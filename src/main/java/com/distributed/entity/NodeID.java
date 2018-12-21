package com.distributed.entity;

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

    @Override
    public String toString() {
        return "NodeID{" +
                "inetAddress=" + inetAddress +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeID) {
            NodeID nodeID = (NodeID) obj;
            if (this.inetAddress.equals(nodeID.getInetAddress()) && this.port == nodeID.getPort()) {
                return true;
            }
            else {
                return false;
            }
        }
        return super.equals(obj);
    }

}
