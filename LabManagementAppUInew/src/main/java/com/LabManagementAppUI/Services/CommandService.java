package com.LabManagementAppUI.Services;

import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;


import java.util.List;

public class CommandService {
    private static IConnection connection;
    private static final int port = IPorts.TOKENS;

    public static void sendCommand(List<String> addressList, Commands command){
        for(String address: addressList){
            sendCommand(address, command);
        }
    }
    public static void sendCommand(String address, Commands command){
        connection = ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        connection.initialize(port, address);
        connection.sendString(command.label);
        connection.close();
    }

    public static void sendCommand(List<String> addressList, String command){
        for(String address: addressList){
            sendCommand(address, command);
        }
    }
    public static void sendCommand(String address, String command){
        connection = ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        connection.initialize(port, address);
        connection.sendString(command);
        connection.close();
    }

}
