package com.LabManagementAppUI.FileTransfer;

import com.LabManagementAppUI.Services.CommandService;
import com.LabManagementAppUI.Services.Commands;
import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;


import java.io.*;
import java.util.List;


public class FileSender implements Runnable{
    private TCPServer connection;
    private final String clientSavePath;
    private final String senderFilePath;
    private final List<String> IPs;

    public FileSender(List<String> IPs, String clientSavePath, String senderFilePath) {
        this.clientSavePath = clientSavePath;
        this.senderFilePath = senderFilePath;
        this.IPs = IPs;
    }


    @Override
    public void run() {
        for(String address: IPs) {
            CommandService.sendCommand(address, Commands.FILE_TRANSFER);
            send();
        }
    }
    private void send() {
        connection= (TCPServer) ConnectionFactory.getIConnection(IConnectionNames.TCP_SERVER);
        connection.initialize(IPorts.FILE_TRANSFER,null);
        connection.sendString(clientSavePath);

        File file = new File(senderFilePath);
        if (file.isDirectory()) {
            connection.sendBoolean(true);
            sendDirectory(file);
        } else {
            connection.sendBoolean(false);
            sendFile(file);
        }
        connection.close();
    }

    private void sendFile(File file) {
        connection.sendString(file.getName());
        int fileSize = (int) file.length();
        connection.sendInt(fileSize);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                connection.sendFileData(buffer, bytesRead);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + file.getName() + " is not found.");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Couldn't read file " + file.getName());
        }
    }

    private void sendDirectory(File directory) {
        connection.sendString(directory.getName());
        File[] files = directory.listFiles();
        // Send the number of files in the directory
        connection.sendInt(files != null ? files.length : 0);
        if (files != null) {
            // Send each file in the directory
            for (File file : files) {
                if (file.isDirectory()) {
                    sendDirectory(file);
                } else {
                    sendFile(file);
                }
            }
        }

    }
}
