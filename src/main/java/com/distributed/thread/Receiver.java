package com.distributed.thread;

import com.distributed.util.Util;
import com.distributed.entity.Message;
import com.distributed.Node;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Receiver implements Runnable {
    static Logger logger = Logger.getLogger(Receiver.class);

    private static final int TIMEOUT = 1000;  //设置接收数据的超时时间
    private int port;
    private DatagramSocket socket;


    public Receiver(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
            logger.info("UDP listens " + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            while (!Node.EXIT) {
                byte[] buf = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivedPacket); // -------- wait
                Message receivedMessage = Util.toMessageObj(buf);
                String receivedType = receivedMessage.getType();

                if (receivedType.equals("PING")) {
                    // 这条可以作用于 rejoin 的 introducer
                    // 因为 rejoin 的 introducer 有着大家的名单 但是大家却没有它
                    if (!Node.membershipList.contains(receivedMessage.getSourceID())) {
                        Node.membershipList.add(receivedMessage.getSourceID());
                    }
                    Message message = new Message("ACK", receivedMessage.getSinkID(), receivedMessage.getSourceID());
                    Util.sendMessage(message, receivedMessage.getSourceID(), socket);
                } else if (receivedType.equals("REQ")) {
                    Message message = new Message("PING", receivedMessage.getSourceID(), receivedMessage.getSinkID());
                    Util.sendMessage(message, receivedMessage.getSinkID(), socket);
                } else if (receivedType.equals("ACK")) {
                    NodeID detectID = receivedMessage.getSourceID();
                    if (Node.detectNodes.contains(detectID)) {
                        Node.detectNodes.remove(detectID);
                        logger.info("[Detect -] " + Node.detectNodes);
                    } else {
                        logger.info("Having received ack, thanks for ping.");
                    }
                } else if (receivedType.equals("MOVE")) {
                    Node.membershipList.remove(receivedMessage.getSourceID());
                    logger.info("[-] Failure node: " + receivedMessage.getSourceID());
                } else if (receivedType.equals("JOIN")) {
                    NodeID newNodeId = receivedMessage.getSourceID();
                    if (!Node.membershipList.contains(newNodeId)) {
                        Node.membershipList.add(newNodeId);
                    }
                } else if (receivedType.equals("CLOSE")) {
                    ;
                } else {
                    logger.error("The type of message is not defined.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
//                System.out.println("我自闭了");
                logger.info("[Receiver Socked Closed]");
            }
        }
    }
}
