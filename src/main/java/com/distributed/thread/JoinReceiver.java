package com.distributed.thread;

import com.distributed.Node;
import com.distributed.util.Util;
import com.distributed.entity.Message;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class JoinReceiver implements Runnable {
    private Logger logger = Logger.getLogger(JoinReceiver.class);

    private int port;
    private ServerSocket serverSocket;

    public JoinReceiver(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
            logger.info("TCP listens " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!Node.EXIT) {
                Socket socket = null;
                Scanner scanner = null;
                NodeID newNodeID = null;
                ObjectOutputStream out = null;
                try {
                    socket = serverSocket.accept();// ----wait
                    scanner = new Scanner(socket.getInputStream());
                    scanner.useDelimiter("\n");
                    String newJoinNode = null;
                    if (scanner.hasNext()) {
                        newJoinNode = scanner.next();
                    }
                    logger.info("[Receive] From New node: " + newJoinNode);
                    String[] newJoinNodeSplit = newJoinNode.split("_");
                    // newJoinNodeSplit[0] = 192.168.1.11
                    // newJoinNodeSplit[1] = 9001
                    // add new node to memberList
                    // windows 系统会在字符串后加上\r 导致 parseInt 不能成功
                    for (int i = 0; i < newJoinNodeSplit[1].length(); i++) {
                        if (newJoinNodeSplit[1].charAt(i) == '\r') {
                            newJoinNodeSplit[1] = newJoinNodeSplit[1].substring(0, i);
                        }
                    }
                    newNodeID = new NodeID(InetAddress.getByName(newJoinNodeSplit[0]), Integer.parseInt(newJoinNodeSplit[1]));
                    if (!Node.membershipList.contains(newNodeID)) {
                        Node.membershipList.add(newNodeID);
                        logger.info("[+] New node: " + newNodeID);
                        // send memberList to the new node
                        out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(Node.membershipList);
                        logger.info("[Send to New Node] Update membership List .");
                        // use UDP to send the JOIN new node message
                        DatagramSocket socket1 = new DatagramSocket();
                        Message message = new Message("JOIN", newNodeID, null);
                        for (int i = 0; i < Node.membershipList.size(); i++) {
                            NodeID targetID = Node.membershipList.get(i);
                            Util.sendMessage(message, targetID, socket1);
                        }
                        logger.info("[SendAll] JOIN the new node.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (scanner != null) {
                            scanner.close();
                        }
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
//                    System.out.println("我自闭了");
                    logger.info("[JoinReceiver Socket Closed]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
