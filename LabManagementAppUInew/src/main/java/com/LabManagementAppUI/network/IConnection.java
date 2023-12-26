package com.LabManagementAppUI.network;

public interface IConnection {

    void send(byte[] data);

    void sendString(String message);

    byte[] receive();

    String receiveString();

    void initialize(int port, String ipAddress);

    void close();
}
