package com.LabManagementAppUI.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender implements IConnection {

    private int port;
    private InetAddress multicastGroup = null;
    private MulticastSocket multicastSocket = null;


    public void initialize(int port, String ipAddress) {

        try {
            multicastGroup = InetAddress.getByName(ipAddress);
            this.multicastSocket = new MulticastSocket();
            this.port = port;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void send(byte[] data) {


        DatagramPacket sendPacket = new DatagramPacket(data, data.length, multicastGroup, port);
        try {
            multicastSocket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendString(String message) {

    }

    @Override
    public byte[] receive() {
        return null;
    }

    @Override
    public String receiveString() {
        return null;
    }


    public void close() {
        multicastSocket.close();
    }
}
