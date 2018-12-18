package com.distributed;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class Node implements Runnable {

    List<NodeID> membershipList;

    private DatagramSocket socket;
    private boolean alive;
    private byte[] buf;

//    private Integer port;
//    private InetAddress inetAddress;
    private NodeID id; // ipAddress+port

    private Boolean isIntroducer;
//    private InetAddress introducerAddress;
//    private int introducerPort;
    private NodeID instroducerID;

    // 非 introducer 的构造方法
    public Node(Integer port, InetAddress introducerAddress, int introducerPort) {
        this.membershipList = new ArrayList<NodeID>();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.buf = new byte[1024];

        try {
            this.id = new NodeID(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.isIntroducer = false;
        this.instroducerID = new NodeID(introducerAddress, introducerPort);
//        try {
//            this.inetAddress = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        if (isIntroducer) {
//            instroducerID = new NodeID(introducerAddress, introducerPort, System.currentTimeMillis());
//            membershipList.add(instroducerID);
//        }
        this.alive = true;
        membershipList.add(id);
        membershipList.add(instroducerID);
    }

    // introducer 的构造方法
    public Node(int port) {
        this.membershipList = new ArrayList<NodeID>();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.buf = new byte[1024];

        try {
            this.id = new NodeID(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.isIntroducer = true;
        this.alive = true;
        membershipList.add(id);
    }



    public void run() {
        if (isIntroducer) {
            String msg = "I join!"
            buf = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, introducerAddress, introducerPort);
            socket.send(packet);

        }
        else {

        }





        while (alive) {



            // 初始化接收包的 size
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet); //阻塞直到接收到 Client

            // 接收到 packet
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            // new 一个要返回的 packet （有 address 和 port）
            packet = new DatagramPacket(buf, buf.length, address, port);
            // 获取 packet 的 data
            String received
                    = new String(packet.getData(), 0, packet.getLength());
            // 如果收到 end 则服务器停止工作
            if (received.equals("end")) {
                running = false;
                continue;
            }
            // 发送（返回）packet
            socket.send(packet);
        }
        socket.close();
    }
}
