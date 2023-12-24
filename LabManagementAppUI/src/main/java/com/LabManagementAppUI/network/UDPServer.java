package com.LabManagementAppUI.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends UDP {

    private static final Logger logger = LogManager.getLogger(UDPServer.class);

    public byte[] receive() {

        DatagramPacket receivePacket = super.receivePacket();

        this.address = receivePacket.getAddress();
        this.port = receivePacket.getPort();

        return receivePacket.getData();
    }

    public String receiveString() {
        DatagramPacket receivePacket = super.receivePacket();

        this.address = receivePacket.getAddress();
        this.port = receivePacket.getPort();

        return new String(receivePacket.getData());
    }

    @Override
    public void initialize(int port, String ipAddress) {
        try {
            socket = new DatagramSocket(port);
            logger.info("UDPServer socket initialized successfully port:" + this.port);
        } catch (SocketException e) {
            logger.error("UDPClient socket failed to initialized " + new RuntimeException());
        }
    }

}
