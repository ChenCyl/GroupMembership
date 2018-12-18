package com.distributed;

import java.net.InetAddress;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class NodeID {
    private InetAddress inetAddress;
    private int port;
//    private long timestamp; // System.current...

    public NodeID(InetAddress ipAddress, int port) {
        this.inetAddress = ipAddress;
        this.port = port;
//        this.timestamp = timestamp;
    }
}
