package com.LabManagementAppUI.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends UDP{

    public byte[] receive() {

        DatagramPacket receivePacket=super.receivePacket();

        this.address = receivePacket.getAddress();
        this.port = receivePacket.getPort();

        return receivePacket.getData();
    }

    public String receiveString() {
        DatagramPacket receivePacket=super.receivePacket();

        this.address = receivePacket.getAddress();
        this.port = receivePacket.getPort();

        return new String(receivePacket.getData());
    }

    @Override
    public void initialize(int port, String ipAddress) {
        try {
            socket=new DatagramSocket(port);

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

}
