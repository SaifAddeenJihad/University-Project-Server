package com.LabManagementAppUI.network;

import java.net.*;

public class UDPClient extends UDP{


    public byte[] receive() {

        DatagramPacket receivePacket=super.receivePacket();

        return receivePacket.getData();
    }

    @Override
    public String receiveString() {
        DatagramPacket receivePacket=super.receivePacket();

        return new String(receivePacket.getData());
    }

    public void initialize(int port, String ipAddress) {

        try {
            socket=new DatagramSocket();
            this.address=InetAddress.getByName(ipAddress);
            this.port=port;

        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }

    }
}
