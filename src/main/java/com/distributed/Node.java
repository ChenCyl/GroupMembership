package com.distributed;

import com.distributed.entity.Message;
import com.distributed.entity.NodeID;
import com.distributed.thread.Detector;
import com.distributed.thread.JoinReceiver;
import com.distributed.thread.Receiver;
import org.apache.log4j.Logger;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * bug 总结：
 *
 * 数据格式：一般的传输都是 Message[String type, NodeID nodeID]
 * 传 membershipList 时
 *          /组播的失效结点 udp packet
 *          /新 join 的节点返回 tcp objectStream
 *
 * 何时新建线程：
 *  tcp:
 *      while(true)
 *      new Thread(xxx(server.accept())).start() // 传入的是连接上的 socket
 *  udp:
 *      while(true)
 *      socket.receive(packet);
 *      new Thread(xxx(packet)).start()
 *
 * @author: Chen Yulei
 * @since: 2018-12-18
 **/
public class Node {
    private Logger logger = Logger.getLogger(Node.class);
    public static List<NodeID> membershipList;
    public static List<NodeID> detectNodes;
    private DatagramSocket socket;
    private NodeID id; // ipAddress+port
    private Boolean isIntroducer;
    private NodeID instroducerID;

    static {
        membershipList = new ArrayList<NodeID>();
        detectNodes = new ArrayList<NodeID>();
    }

    // 非 introducer 的构造方法
    public Node(int port, InetAddress introducerAddress, int introducerPort) {

        try {
            this.id = new NodeID(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.isIntroducer = false;
        this.instroducerID = new NodeID(introducerAddress, introducerPort);
    }

    // introducer 的构造方法
    public Node(int port) {
        try {
            this.id = new NodeID(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.isIntroducer = true;
        membershipList.add(id);
        logger.info("Introducer Created.");
    }


    public List<NodeID> run(List<NodeID> msList) {
        if (msList != null) {
            membershipList = msList;
        }
        Thread reveiverThread = new Thread(new Receiver(id.getPort()), "ReveiverThread");
        reveiverThread.setDaemon(true);
        reveiverThread.start();

        if (isIntroducer) {
            Thread joinReceiver = new Thread(new JoinReceiver(id.getPort() - 1), "JoinReceiverThread");
            joinReceiver.setDaemon(true);
            joinReceiver.start();
        }
        else {
            // 不用单独跑线程 进来之后就必须发给 introducer 没有加入结点就做不了之后的事
            connectToIntroducer();
        }

        Thread detectorThread = new Thread(new Detector(id), "DetectorThread");
        detectorThread.setDaemon(true);
        detectorThread.start();

        System.out.println("You can input: [q] to leave the group. [m] to show the membership list.");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            if (scanner.next().equals("q")) {
                if (!isIntroducer) {
                    membershipList = null;
                }
                System.out.println("you hava left the group.");
                System.out.println("You can input [r] to rejoin in the group.");
                scanner.close();
                logger.info("[Leave] I am " + id);
                return membershipList;
            }
            else if (scanner.next().equals("m")) {
                System.out.println(membershipList.toString());
            }
            else {
                System.out.println("Invalid input.");
            }
        }
        return membershipList;
    }

    public void connectToIntroducer() {
        Socket socket = null;
        PrintWriter out = null;
        try {
            socket = new Socket(instroducerID.getInetAddress().getHostAddress(), instroducerID.getPort() - 1);
            out = new PrintWriter(socket.getOutputStream());
            out.println(id.getInetAddress().getHostAddress() + "_" + id.getPort());
            out.flush();
            logger.info("[Send] Tell introducer I am " + id);
            // receive the membership

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object o = in.readObject();
            if (o instanceof List) {
                membershipList = (List<NodeID>) o;
                logger.info("[Receive] MemberList: " + membershipList.toString());
            }
            else {
                logger.error("[Receive] The type of member list from instroducer is not List.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

