package com.LabManagementAppUI.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient extends TCP {

    private static final Logger logger = LogManager.getLogger(TCPClient.class);

    public void initialize(int port, String ipAddress) {

        if (ipAddress == null) {
            return;
        }

        try {
            this.socket = new Socket(ipAddress, port);
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
            logger.info("TCPClient socket initialized successfully ip address:" + ipAddress + " port:" + port);

        } catch (IOException e) {
            logger.error("TCPClient socket failed to initialized " + new RuntimeException());
        }

    }

    public void close() {

        try {
            socket.close();
            logger.info("TCP socket closed successfully");
        } catch (IOException e) {
            logger.error("TCP socket failed to close " + new RuntimeException());
        }


    }
}
