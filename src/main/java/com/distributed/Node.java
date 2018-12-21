package com.distributed;

import com.distributed.entity.NodeID;
import com.distributed.thread.Detector;
import com.distributed.thread.JoinReceiver;
import com.distributed.thread.Receiver;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
    static Logger logger = Logger.getLogger(Node.class);

    public static List<NodeID> membershipList;
    public static List<NodeID> detectNodes;
    private DatagramSocket socket;
    private boolean alive; //no use ??? where to use?
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
        this.alive = true;
//        membershipList.add(id);
//        membershipList.add(instroducerID);
    }

    // introducer 的构造方法
    public Node(int port) {
        try {
            this.id = new NodeID(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.isIntroducer = true;
        this.alive = true;
        membershipList.add(id);
        logger.info("Introducer Created.");
    }


    public void run() throws IOException {
        new Thread(new Receiver(id.getPort()), "ReveiverThread").start();

        if (isIntroducer) {
            Thread joinReceiver = new Thread(new JoinReceiver(id.getPort() - 1), "JoinReceiverThread");
            joinReceiver.start();
        }
        else {
            // 不用单独跑线程 进来之后就必须发给 introducer 没有加入结点就做不了之后的事
            Socket socket = new Socket(instroducerID.getInetAddress().getHostAddress(), instroducerID.getPort() - 1);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(id.getInetAddress().getHostAddress() + "_" + id.getPort());
            out.flush();
            logger.info("[Send] Tell introducer I am " + id);
            // receive the membership
            try {
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
            } finally {
                out.close();
                socket.close();
            }
        }
        new Thread(new Detector(id), "DetectorThread").start();

    }
}
