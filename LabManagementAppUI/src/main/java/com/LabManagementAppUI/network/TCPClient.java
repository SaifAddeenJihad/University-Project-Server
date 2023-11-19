package com.LabManagementAppUI.network;

import java.io.*;
import java.net.Socket;

public class TCPClient extends TCP {

    public void initialize(int port, String ipAddress) {

        try {
            this.socket = new Socket(ipAddress, port);
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
