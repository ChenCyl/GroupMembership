package com.distributed.util;

import com.distributed.entity.Message;
import com.distributed.entity.NodeID;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Util {
    private static Logger logger = Logger.getLogger(Util.class);

    public static void sendMessage(Message message, NodeID targetID, DatagramSocket socket) {
//        Message message = simulateLoss(0.3, realMessage);
        ByteArrayOutputStream bos = null;
        ObjectOutputStream out = null;
        try {
            // new 一块内存
            bos = new ByteArrayOutputStream(1024);
            // 将要输出的对象放在内存流中
            out = new ObjectOutputStream(bos);
            // 写入对象
            out.writeObject(message);
            // 获取内存流中的对象并转为字节
            byte[] bytes = bos.toByteArray();
            // 新建 upd 包并通过 sock 发送
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, targetID.getInetAddress(), targetID.getPort());
            socket.send(packet);
            logger.info("[Send] Message: " + message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Message toMessageObj(byte[] buf) {
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        ObjectInputStream in = null;
        Message message = null;
        try {
            in = new ObjectInputStream(bis);
            message = (Message) in.readObject();
            logger.info("[Receive] Message: " + message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public static List<NodeID> listWithoutSelf(List<NodeID> list, NodeID selfId) {
        List<NodeID> reList = new ArrayList<NodeID>();
        for (int i = 0; i < list.size(); i++) {
            NodeID tempId = list.get(i);
            if (!tempId.equals(selfId)) {
                reList.add(tempId);
            }
        }
        return reList;
    }

    public static Message simulateLoss(double lossRate, Message relMessage) {
        if (lossRate == 0) {
            return relMessage;
        }
        else {
            Message lossMessage = new Message("LOSS", null, null);
            long l = System.currentTimeMillis();
            int i = (int)( l % 100 ); // [0, 100)
            if (lossRate == 0.03) {
                if (i < 3) {
                    return lossMessage;
                }
            }
            else if (lossRate == 0.1) {
                if (i < 10) {
                    return lossMessage;
                }
            }
            else if (lossRate == 0.3) {
                if (i < 30) {
                    return lossMessage;
                }
            }
            else {
                logger.error("[传入的丢包率有误！]");
            }
        }
        return relMessage;
    }

}
