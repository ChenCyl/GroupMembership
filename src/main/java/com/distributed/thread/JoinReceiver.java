package com.distributed.thread;

import com.distributed.Node;
import com.distributed.Util;
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
            while (true) {
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
                    // add new node to memberList
                    newNodeID = new NodeID(InetAddress.getByName(newJoinNodeSplit[0]), Integer.parseInt(newJoinNodeSplit[1]));
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
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
