package com.distributed.thread;

import com.distributed.Util;
import com.distributed.entity.Message;
import com.distributed.Node;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Detector implements Runnable {
    private Logger logger = Logger.getLogger(Detector.class);

    private NodeID myId;
    private DatagramSocket socket;

    public Detector(NodeID myId) {
        this.myId = myId;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(200);
                // 给自己发 ping 其实也是没有问题的... 但是 ... 还是不要自己 ping 自己了吧
                List<NodeID> pingList = Util.listWithoutSelf(Node.membershipList, myId);
                logger.info("[------] pingList:" + pingList.toString() );
                int memberSize = pingList.size();
                if (memberSize > 0) {
                    // 随机选择一个发送 ping
                    Collections.shuffle(pingList);
                    NodeID targetID = pingList.get(0);
                    Message message = new Message("PING", myId, targetID);
                    Util.sendMessage(message, targetID, socket);
                    // 将目标结点加入到 detect 中
                    Node.detectNodes.add(targetID);
                    logger.info("[Detect +] " + Node.detectNodes);

                    Thread.sleep(200);

                    if (Node.detectNodes.contains(targetID) ) {
                        // 如果 membershiplist 里有2个结点（被 ping + 被 pingreq）
                        if (memberSize > 1) {
                            message = new Message("REQ", myId, targetID);
                            Util.sendMessage(message, pingList.get(1), socket);
                            if (memberSize > 2) {
                                Util.sendMessage(message, pingList.get(2), socket);
                            }
                            Thread.sleep(200);
                        }
                        if (Node.detectNodes.contains(targetID)) {
                            pingList.remove(targetID);
                            Node.membershipList.remove(targetID);
                            Node.detectNodes.remove(targetID);
                            logger.info("[-] Remove: " + targetID);
                            message = new Message("MOVE", targetID, null);
                            for (NodeID id : pingList) {
                                Util.sendMessage(message, id, socket);
                            }
                        } else {
                            continue;
                        }
                    }
                    else {
                        Thread.sleep(200);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
