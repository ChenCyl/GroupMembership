import org.junit.Test;

import java.io.IOException;
import java.net.*;

/**
 * @author: Chen Yulei
 * @since: 2018-12-19
 **/
public class UDPTest {


    @Test
    public void client() throws UnknownHostException, SocketException {
        String string = "I am client!";
        byte[] buff = string.getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, InetAddress.getByName("localhost"), 9001);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    class Thread1 implements Runnable {

        public void run() {

            while (true) {
                DatagramSocket socket1 = null;
                try {
                    socket1 = new DatagramSocket(9001);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] buff = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                try {
                    socket1.receive(packet);
                    String receivedString = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Thread1 receive the message: " + receivedString);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket1.close();
                }
            }
        }
    }

    class Thread2 implements Runnable {

        public void run() {

            while (true) {
                DatagramSocket socket2 = null;
                try {
                    socket2 = new DatagramSocket(9001);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                byte[] buff = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                try {
                    socket2.receive(packet);
                    String receivedString = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("Thread2 receive the message: " + receivedString);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    socket2.close();
                }
            }
        }

    }

    @Test
    public void server() {
        new Thread(new Thread1()).start();
        new Thread(new Thread2()).start();
    }
}


