package com.LabManagementAppUI.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;

public class UDPClient extends UDP {

    private static final Logger logger = LogManager.getLogger(UDPClient.class);

    public byte[] receive() {

        DatagramPacket receivePacket = super.receivePacket();

        return receivePacket.getData();
    }

    @Override
    public String receiveString() {
        DatagramPacket receivePacket = super.receivePacket();

        return new String(receivePacket.getData());
    }

    public void initialize(int port, String ipAddress) {
        if (ipAddress != null) {
            try {
                socket = new DatagramSocket();
                this.address = InetAddress.getByName(ipAddress);
                this.port = port;
                logger.info("UDPClient socket initialized successfully ip address:" + this.address + " port:" + this.port);
            } catch (UnknownHostException | SocketException e) {
                logger.error("UDPClient socket failed to initialized " + new RuntimeException());
            }

        }
    }
}
