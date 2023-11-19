package com.LabManagementAppUI.network;

public class ConnectionFactory {

    public static IConnection getIConnection(String connectionType) {
        connectionType = connectionType.toLowerCase();

        IConnection connection;

        switch (connectionType) {
            case "tcpclient":
                connection = new TCPClient();
                break;
            case "tcpserver":
                connection = new TCPServer();
                break;
            case "udpclient":
                connection = new UDPClient();
                break;
            case "udpserver":
                connection = new UDPServer();
                break;
            case "multicastsender":
                connection = new MulticastSender();
                break;
            case "multicastreceiver":
                connection = new MulticastReceiver();
                break;
            default:
                throw new IllegalArgumentException("Unknown connection type: " + connectionType);
        }

        return connection;
    }
}
