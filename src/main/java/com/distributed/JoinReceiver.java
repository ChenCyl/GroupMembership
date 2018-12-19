package com.distributed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class JoinReceiver implements Runnable {

    private int port;
    private ServerSocket serverSocket;

    public JoinReceiver(int port) {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // recerved message is ip_port
                String newJoinNode = in.readLine();
                String[] newJoinNodeSplit = newJoinNode.split("_");
                // add new node to memberList
                Node.membershipList.add(new NodeID(InetAddress.getByName(newJoinNodeSplit[0]), newJoinNodeSplit[1]));
                // send memberList to the new node
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(Node.membershipList);
                socket.close();
            }
        }
        finally {
            serverSocket.close();
        }
    }
}
