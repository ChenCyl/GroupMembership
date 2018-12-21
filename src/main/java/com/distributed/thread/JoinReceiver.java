package com.distributed.thread;

import com.distributed.Node;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class JoinReceiver implements Runnable {
    static Logger logger = Logger.getLogger(JoinReceiver.class);

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
                    logger.info("[SendAll] Update membership List .");
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
