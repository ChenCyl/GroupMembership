package com.distributed;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: Hitoka
 * @since: 2018-12-20
 **/
public class NodeMain {
    public static void main(String[] args) throws IOException {
        String introIp = "127.0.0.1";
        new Node(9005, InetAddress.getByName(introIp), 9001).run();
    }
}
