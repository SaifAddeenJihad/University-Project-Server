package com.LabManagementAppUI.network;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

abstract class UDP implements IConnection {

    private static final Logger logger = LogManager.getLogger(UDP.class);
    private final int MAX_BUFFER_SIZE = 65507;
    protected DatagramSocket socket = null;
    protected InetAddress address = null;
    protected int port;

    public void send(byte[] data) {


        try {
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
            socket.send(sendPacket);
            logger.info("send UDP done successfully");
        } catch (IOException e) {
            logger.error("send UDP failed " + new RuntimeException());
        }
    }


    public DatagramPacket receivePacket() {

        byte[] receiveData = new byte[MAX_BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            socket.setSoTimeout(500);  // Set the socket timeout
            socket.receive(receivePacket);
            logger.info("UDP datagram packet received");
        } catch (SocketTimeoutException e) {
            System.out.println("Allawi");;  // Propagate the exception to indicate a timeout
        } catch (IOException e) {
            logger.error("UDP datagram packet failed to receive: " + e.getMessage());
        }

        return receivePacket;
    }

    public abstract byte[] receive();

    @Override
    public void sendString(String message) {
        if (message == null) {
            return;
        }

        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);

        try {
            socket.send(sendPacket);
            logger.info("UDP sendString done successfully");
        } catch (IOException e) {
            logger.error("UDP sendString failed " + new IOException());
        }

    }


    public void close() {
        socket.close();
        logger.info("UDP socket closed");
    }

}
