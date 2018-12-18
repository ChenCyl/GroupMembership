package com.distributed;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class NodeMain {
    public static void main(String[] args) {

        Boolean isIntroducer = false;
        Integer port = 9002;
        InetAddress introducerInetAddress = InetAddress.getByName("");
        int introPort = 9001;

        // 非 intro node
        new Thread(new Node(port, introducerInetAddress, introPort)).start();
        // intro node
        new Thread(new Node(port)).start();



    }
}
