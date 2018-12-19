package com.distributed;

import java.io.*;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class Util {

    public static byte[] messageToByte(Message message) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(5000);
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(byteOutputStream));
        out.writeObject(message);


    }

    public static Message byteToMessage(byte[] bytes) {
        ByteArrayInputStream byteInputStreamStream = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(byteInputStreamStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message message = null;
        try {
            message = (Message)in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }
}
