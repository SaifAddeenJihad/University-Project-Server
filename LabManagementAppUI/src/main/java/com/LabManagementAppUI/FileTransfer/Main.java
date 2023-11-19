package com.LabManagementAppUI.FileTransfer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException {
        System.out.println("Hello world!");
        //sender
        String savePath="D:\\shared";
        String sendPath="C:\\Users\\Saif\\Downloads\\network-core\\network-core.7z";
        String ipAddress="";
        FileSender fileSender =new FileSender(savePath,sendPath);
        fileSender.start();
        //receiver
        //InetAddress ip = InetAddress.getByName("127.0.0.1");
        //FileReceiver fileReceiver=new FileReceiver(ip);
        //fileReceiver.start();

    }

}