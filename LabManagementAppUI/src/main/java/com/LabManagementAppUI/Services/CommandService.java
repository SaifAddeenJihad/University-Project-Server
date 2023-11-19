package com.LabManagementAppUI.Services;

import com.LabManagementAppUI.network.*;


import java.net.InetAddress;
import java.util.List;

public class CommandService {
    private static IConnection connection;
    private static final int port = 50000;

    public static void sendCommand(List<InetAddress> addressList, Commands command){
        for(InetAddress address: addressList){
            sendCommand(address, command);
        }
    }
    public static void sendCommand(InetAddress address, Commands command){
        connection = ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        connection.initialize(port, address.getHostAddress());
        connection.sendString(command.label);
        connection.close();
    }

    public static void sendCommand(List<InetAddress> addressList, String command){
        for(InetAddress address: addressList){
            sendCommand(address, command);
        }
    }
    public static void sendCommand(InetAddress address, String command){
        connection = ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        connection.initialize(port, address.getHostAddress());
        connection.sendString(command);
        connection.close();
    }

}
