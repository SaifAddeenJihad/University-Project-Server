package com.LabManagementAppUI.network;

import java.io.*;
import java.net.ServerSocket;

public class TCPServer extends TCP {

    private ServerSocket serverSocket = null;
    private String connectedIP = "No connection yet";


    @Override
    public void initialize(int port, String ipAddress) {
        try {
            serverSocket = new ServerSocket(port);

            this.socket = serverSocket.accept();
            this.connectedIP = this.socket.getInetAddress().getHostAddress();

            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getConnectedIP() { return this.connectedIP; }
}
