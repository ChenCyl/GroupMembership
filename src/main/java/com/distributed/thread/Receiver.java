package com.distributed.thread;

import com.distributed.Util;
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
    static org.apache.log4j.Logger logger = Logger.getLogger(Receiver.class);

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
            while (true) {
                byte[] buf = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivedPacket); // -------- wait
                Message receivedMessage = Util.toMessageObj(buf);
                String receivedType = receivedMessage.getType();

                if (receivedType.equals("PING")) {
                    Message message = new Message("ACK", receivedMessage.getSinkID(), receivedMessage.getSourceID());
                    Util.sendMessage(message, receivedMessage.getSourceID(), socket);
                } else if (receivedType.equals("REQ")) {
                    Message message = new Message("PING", receivedMessage.getSourceID(), receivedMessage.getSinkID());
                    Util.sendMessage(message, receivedMessage.getSinkID(), socket);
                } else if (receivedType.equals("ACK")) {
                    NodeID detectID = receivedMessage.getSourceID();
                    logger.info("[------] detectNode" + detectID);
                    if (Node.detectNodes.contains(detectID)) {
                        Node.detectNodes.remove(detectID);
                        logger.info("[Detect] " + Node.detectNodes);
                    } else {
                        logger.info("已经收到过 ack 了哦，谢谢你帮我 ping");
                    }
                } else if (receivedType.equals("MOVE")) {
                    Node.membershipList.remove(receivedMessage.getSourceID());
                    logger.info("[-] Failure node: " + receivedMessage.getSourceID());
                } else {
                    logger.error("咋回事啊，你是我没有定义类型的消息。");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
