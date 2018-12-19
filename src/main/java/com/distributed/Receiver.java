package com.distributed;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Receiver implements Runnable {

    private static final int TIMEOUT = 1000;  //设置接收数据的超时时间
    private int port;
    private DatagramSocket socket;
    private byte[] buf;
    private DatagramPacket receivedPacket;
    private ByteArrayInputStream byteInputStreamStream;
    private ByteArrayOutputStream byteOutputStream;
    private ObjectInputStream in;
    private ObjectOutputStream out;


    public Receiver(int port) {
        this.port = port;
        socket = new DatagramSocket(port);
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
            socket.receive(receivedPacket); // -------- wait
            Message receivedMessage = (Message) in.readObject();
            String receivedType = receivedMessage.getType();

            if (receivedType.equals("PING")) {
                Message message = new Message("ACK", receivedMessage.getSinkID(), receivedMessage.getSourceID());
                sendMessage(message, receivedMessage.getSourceID());
            }
            else if (receivedType.equals("REQ")) {
                Message message = new Message("PING", receivedMessage.getSourceID(), receivedMessage.getSinkID());
                sendMessage(message, receivedMessage.getSinkID());
            }
            else if (receivedType.equals("ACK")) {
                NodeID detectID = receivedMessage.getSourceID();
                if (Node.detectNodes.contains(detectID)) {
                    Node.detectNodes.remove(detectID);
                }
                else {
                    System.out.println("已经收到过 ack 了哦，谢谢你帮我 ping");
                }
            }
            else if (receivedType.equals("MOVE")) {
                Node.membershipList.remove(receivedMessage.getSourceID());
            }
            else {
                System.out.println("咋回事啊，你是我没有定义类型的消息。");
            }
        }
    }

    /**
     * upd receive object process:
     * 1. receive byte[]
     * 2. byte to byteInputStream
     * 3. byteInputStream to ObjectInputStream
     * 4. ObjectInputStream.readObject()
     *
     * upd send object process:
     * 1. ObjectOutputStream.writeObject()
     * 2. // ObjectOutputStream to ByteOutputStream
     * 3. ByteOutputStream to byte
     * 4. send byte[]
     */
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
