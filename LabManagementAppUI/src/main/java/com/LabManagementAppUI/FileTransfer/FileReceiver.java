package com.LabManagementAppUI.FileTransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.LabManagementAppUI.Services.CommandService;
import com.LabManagementAppUI.Services.Commands;
import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;

public class FileReceiver implements Runnable{
    private final List<String> IPs;
    private String savePath;
    private final String collectPath;
    private TCPServer connection;
    public FileReceiver(List<String> IPs, String savePath, String collectPath) {
        this.savePath = savePath;
        this.collectPath = collectPath;
        this.IPs = IPs;
    }

    @Override
    public void run() {
        for(String ip: IPs) {
            CommandService.sendCommand(ip, Commands.FILE_COLLECT);
            start(ip);
        }
    }
    public void start(String ip) {
        connection = (TCPServer) ConnectionFactory.getIConnection(IConnectionNames.TCP_SERVER);
        connection.initialize(IPorts.FILE_TRANSFER, null);

        connection.sendString(collectPath);

        String id = ip;
        savePath = savePath + "/" + id;
        boolean isDirectory = connection.receiveBoolean();
        if (isDirectory) {
            receiveDirectory();
        } else {
            receiveFile();
        }
        connection.close();
    }

    private void receiveFile() {
        String fileName = connection.receiveString();

        // Receive the file content
        int fileSize = connection.receiveInt();
        long iterations = fileSize / 1024;
        byte[] fileContent = new byte[fileSize];
        connection.receiveFile(fileContent);

        // Save the file to the local filesystem
        Path filePath = Path.of(savePath + "\\" + fileName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, fileContent);
        } catch (IOException e) {
            System.out.println("Couldn't write file " + fileName + " to path " + filePath);
        }

        System.out.println("File received: " + filePath.toString());
    }

    private void receiveDirectory() {
        String directoryName = connection.receiveString();
        int numberOfFiles = connection.receiveInt();
        savePath=savePath+"\\"+directoryName;
        for(int i=0;i<numberOfFiles;i++)
            receiveFile();
    }

}
