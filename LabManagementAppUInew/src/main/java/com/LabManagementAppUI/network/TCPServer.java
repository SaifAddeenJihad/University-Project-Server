package com.LabManagementAppUI.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;

public class TCPServer extends TCP {

    private ServerSocket serverSocket = null;
    private String connectedIP = "No connection yet";

    private static final Logger logger = LogManager.getLogger(TCPServer.class);



    @Override
    public void initialize(int port, String ipAddress) {

        try {
            serverSocket = new ServerSocket(port);

            this.socket = serverSocket.accept();
            this.connectedIP = this.socket.getInetAddress().getHostAddress();

            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

            logger.info("TCPServer socket initialized successfully ip address:" + ipAddress + " port:" + port);


        } catch (IOException e) {
            logger.error("TCPServer socket failed to initialized " + new RuntimeException());
        }
    }

    public void close() {
        try {
            serverSocket.close();
            logger.info("TCP socket closed successfully");
        } catch (IOException e) {
            logger.error("TCP socket failed to close " + new RuntimeException());
        }
    }
    public String getConnectedIP() { return this.connectedIP; }
}
