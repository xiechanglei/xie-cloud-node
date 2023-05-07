package com.xiechanglei.code.cloud.node.discover;

import java.io.IOException;
import java.net.*;

public class TestClient {
    public static void main(String[] args) throws Exception {
      testSend();
        testReceive();
    }

    public static void testSend() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        byte[] arr = "discover".getBytes();
        DatagramPacket packet = new DatagramPacket(arr, arr.length, InetAddress.getByName("255.255.255.255"), 19001);
        socket.send(packet);
        socket.close();
    }

    /**
     * 接收消息,最多等待5秒
     * @throws Exception
     */
    public static  void testReceive() throws Exception {
        DatagramSocket socket = new DatagramSocket(19002);
        new Thread(()->{
            try {
                Thread.sleep(5000);
                socket.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        byte[] arr = new byte[1024];
        DatagramPacket packet = new DatagramPacket(arr, arr.length);
        socket.receive(packet);
        //远程地址
        InetAddress address = packet.getAddress();
        System.out.println(address.getHostAddress());
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println(message);
        socket.close();
    }
}
