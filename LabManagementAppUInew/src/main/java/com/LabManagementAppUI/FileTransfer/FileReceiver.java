package com.LabManagementAppUI.FileTransfer;

import com.LabManagementAppUI.Services.CommandService;
import com.LabManagementAppUI.Services.Commands;
import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.ConnectionFactory;
import com.LabManagementAppUI.network.IConnectionNames;
import com.LabManagementAppUI.network.TCPServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileReceiver implements Runnable {
    private static final Logger logger = LogManager.getLogger(FileReceiver.class);
    private final List<String> IPs;
    private final String collectPath;
    private String savePath;
    private String rootPath;
    private TCPServer connection;

    public FileReceiver(List<String> IPs, String rootPath, String collectPath) {
        this.rootPath = rootPath;
        this.collectPath = collectPath;
        this.IPs = IPs;
        logger.info("FileReceiver initialized successfully");
    }

    @Override
    public void run() {
        for (String ip : IPs) {
            CommandService.sendCommand(ip, Commands.FILE_COLLECT);
            start(ip);
        }
    }

    public void start(String ip) {
        connection = (TCPServer) ConnectionFactory.getIConnection(IConnectionNames.TCP_SERVER);
        connection.initialize(IPorts.FILE_TRANSFER, null);
        logger.info("FileReceiver connection created successfully");

        connection.sendString(collectPath);

        String id = ip;
        savePath = rootPath + "/" + id;
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

        int fileSize = connection.receiveInt();
        byte[] fileContent = new byte[fileSize];
        connection.receiveFile(fileContent);

        Path filePath = Path.of(savePath + "\\" + fileName);
        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, fileContent);
            logger.info("filePath:"+filePath+" received successfully");
        } catch (IOException e) {
            logger.error("Couldn't write file " + fileName + " to path " + filePath);
        }

        System.out.println("File received: " + filePath.toString());
    }

    private void receiveDirectory() {
        String directoryName = connection.receiveString();
        int numberOfFiles = connection.receiveInt();
        savePath = savePath + "\\" + directoryName;
        for (int i = 0; i < numberOfFiles; i++){
            receiveFile();
        }

        logger.info("receiveDirectory completed");
    }

}
