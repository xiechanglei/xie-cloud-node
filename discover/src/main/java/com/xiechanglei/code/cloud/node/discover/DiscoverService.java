package com.xiechanglei.code.cloud.node.discover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Service
public class DiscoverService {
    private static final Logger logger = LoggerFactory.getLogger(DiscoverService.class);

    @Value("${discover.listen.port}")
    private int listenerPort;

    @Value("${discover.send.port}")
    private int sendPort;


    @PostConstruct
    public void init(){
        new Thread(this::receiveMessage).start();
    }
    /**
     * 监听udp广播，并且告知自己的ip地址和服务端口
     */
    public void receiveMessage() {
        try (DatagramSocket socket = new DatagramSocket(listenerPort)){
            byte[] arr = new byte[1024];
            DatagramPacket packet = new DatagramPacket(arr, arr.length);
            while(true){
                try {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    dealMessage(message,packet.getAddress());
                } catch (IOException e) {
                    break;
                }
            }
        } catch (SocketException e) {
            System.exit(1);
        }
    }
    public void dealMessage(String message,InetAddress address) throws IOException {
        if("discover".equals(message)){
            sendMessage("register",address);
        }
    }

    public void sendMessage(String message,InetAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, address, sendPort);
        socket.send(packet);
        socket.close();
    }
}
