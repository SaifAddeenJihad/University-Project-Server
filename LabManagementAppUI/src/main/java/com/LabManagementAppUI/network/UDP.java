package com.LabManagementAppUI.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

abstract class UDP implements IConnection {

    protected DatagramSocket socket = null;
    protected InetAddress address=null;
    protected int port;
    private final int MAX_BUFFER_SIZE = 65507;


    public void send(byte[] data) {

        DatagramPacket sendPacket = new DatagramPacket(data, data.length,address , port);

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public DatagramPacket receivePacket() {

        byte[] receiveData = new byte[MAX_BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return receivePacket;
    }

    public abstract byte[] receive();

    @Override
    public void sendString(String message) {

        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,address , port);

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void close() {
        socket.close();
    }

}
