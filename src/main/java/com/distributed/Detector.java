package com.distributed;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Collections;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Detector implements Runnable {

    private NodeID myId;
    private byte[] buf;
    private DatagramSocket socket;
    private DatagramPacket receivedPacket;
    private ByteArrayInputStream byteInputStreamStream;
    private ByteArrayOutputStream byteOutputStream;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Detector(NodeID myId) {
        this.myId = myId;
        socket = new DatagramSocket(myId.getPort());
        buf = new byte[5000];
        receivedPacket = new DatagramPacket(buf, buf.length);
        // handle byte[] to Object
        byteInputStreamStream = new ByteArrayInputStream(buf);
        byteOutputStream = new ByteArrayOutputStream(5000);
        in = new ObjectInputStream(new BufferedInputStream(byteInputStreamStream));
        out = new ObjectOutputStream(new BufferedOutputStream(byteOutputStream));
    }

    public void run() {
        while (true) {
            // 随机选择一个发送 ping
            Collections.shuffle(Node.membershipList);
            NodeID targetID = Node.membershipList.get(0);
            Message message = new Message("PING", myId, targetID);
            sendMessage(message, targetID);
            // 将目标结点加入到 detect 中
            Node.detectNodes.add(targetID);

            Thread.sleep(200);

            if (Node.detectNodes.contains(targetID)) {
                message = new Message("REQ", myId, targetID);
                sendMessage(message, Node.membershipList.get(1));
                sendMessage(message, Node.membershipList.get(2));

                Thread.sleep(200);

                if (Node.detectNodes.contains(targetID)) {
                    message = new Message("MOVE", targetID, null);
                    for (NodeID id : Node.membershipList) {
                        sendMessage(message, id);
                    }
                }
                else {
                    continue;
                }
            }
            else {
                Thread.sleep(200);
            }
        }
    }

    private void sendMessage(Message message, NodeID targetID) {
        // same part, copy
        out.writeObject(message);
        byte[] bytes = byteOutputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length,
                targetID.getInetAddress(), targetID.getPort());
        socket.send(sendPacket);
        out.flush();
    }
}
