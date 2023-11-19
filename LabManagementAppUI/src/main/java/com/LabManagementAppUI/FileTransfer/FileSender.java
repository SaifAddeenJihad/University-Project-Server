package com.LabManagementAppUI.FileTransfer;

import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class FileSender{
    private String clientSavePath;
    private String senderFilePath;

    private DataOutputStream outputStream;

    public FileSender(String clientSavePath, String senderFilePath) {
        this.clientSavePath = clientSavePath;
        this.senderFilePath = senderFilePath;
    }


    public void start() {
        TCPServer connection= (TCPServer) ConnectionFactory.getIConnection(IConnectionNames.TCP_SERVER);
        connection.initialize(IPorts.FILE_TRANSFER,null);
        outputStream = connection.getOutputStream();
        try {
            outputStream.writeUTF(clientSavePath);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File(senderFilePath);
        try {
            if (file.isDirectory()) {
                outputStream.writeBoolean(true);
                outputStream.flush();
                sendDirectory(file);
            } else {
                outputStream.writeBoolean(false);
                outputStream.flush();
                sendFile(file);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            connection.close();
        }
    }

    private void sendFile(File file) throws IOException {

        outputStream.writeUTF(file.getName());
        outputStream.flush();
        int fileSize = (int) file.length();
        outputStream.writeInt(fileSize);
        outputStream.flush();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            outputStream.flush();
        }

        fileInputStream.close();
    }

    private void sendDirectory(File directory) throws IOException {
        outputStream.writeUTF(directory.getName());
        outputStream.flush();
        File[] files = directory.listFiles();
        // Send the number of files in the directory
        outputStream.writeInt(files != null ? files.length : 0);
        outputStream.flush();
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
