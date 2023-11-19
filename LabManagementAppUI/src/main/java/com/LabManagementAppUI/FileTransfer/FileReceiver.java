package com.LabManagementAppUI.FileTransfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;

public class FileReceiver {
    private String ip;
    private String savePath;
    private String collectPath;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    public FileReceiver(String savePath,String collectPath,String ip) {
        this.savePath=savePath;
        this.collectPath=collectPath;
        this.ip =ip;
    }

    public void start() throws IOException, ClassNotFoundException, URISyntaxException {
        TCPClient connection= (TCPClient) ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        connection.initialize(IPorts.FILE_TRANSFER,ip);
        //Socket socket = new Socket(serverIp, 7777);
        inputStream = connection.getInputStream();
        outputStream =connection.getOutputStream();
        //send cllectionpath
        outputStream.writeUTF(collectPath);
        outputStream.flush();
        String id =inputStream.readUTF();
        savePath = savePath+"/"+id;
        Boolean isDirectory = inputStream.readBoolean();
        if (isDirectory) {
            receiveDirectory();
        } else {
            receiveFile();
        }
        connection.close();
    }

    private void receiveFile() throws IOException {
        String fileName = inputStream.readUTF();

        // Receive the file content
        int fileSize = inputStream.readInt();
        long iterations = fileSize / 1024;
        byte[] fileContent = new byte[fileSize];
        inputStream.readFully(fileContent);

        // Save the file to the local filesystem
        Path filePath = Path.of(savePath + "\\" + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, fileContent);

        System.out.println("File received: " + filePath.toString());
    }

    private void receiveDirectory() throws IOException, URISyntaxException {
        String directoryName = inputStream.readUTF();
        int numberOfFiles = inputStream.readInt();
        savePath=savePath+"\\"+directoryName;
        for(int i=0;i<numberOfFiles;i++)
            receiveFile();
    }

}
