package com.LabManagementAppUI.Services;

import com.LabManagementAppUI.FileTransfer.FileReceiver;
import com.LabManagementAppUI.FileTransfer.FileSender;
import com.LabManagementAppUI.RemoteControl.Controller.UDPImageReceiver;
import com.LabManagementAppUI.Stream.Capture;
import com.LabManagementAppUI.Stream.Sender;
import com.LabManagementAppUI.Stream.UPDStream;
import com.LabManagementAppUI.auxiliaryClasses.Client;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

public class Handler {
    private Handler(){}
    private static Thread streamCapture, streamSender;
    private static List<Thread> udpStreamThreads;
    public static volatile Queue<byte[]> baos =new LinkedList<>();
    public static void startStream(List<String> IPs) {

        CommandService.sendCommand(IPs, Commands.STREAM);
        streamCapture = new Thread(new Capture());
        streamCapture.start();

        streamSender = new Thread(new Sender());
        streamSender.start();
    }
    public static void closeStream(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.CLOSE_STREAM);
        streamCapture.interrupt();
        streamSender.interrupt();
    }
    public static void startUdpStream(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.UDP_STREAM);
        udpStreamThreads = new ArrayList<>();
        for(String ip: IPs) {
            udpStreamThreads.add(startUdpStream(ip));
        }
    }
    private static Thread startUdpStream(String ip) {
        Thread streamThread = new Thread(new UPDStream(ip));
        streamThread.start();
        return streamThread;
    }
    public static void closeUdpStream(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.CLOSE_UDP_STREAM);
        for(Thread thread: udpStreamThreads) {
            thread.interrupt();
        }
    }
    public static void startControl(String IP) throws IOException {
        CommandService.sendCommand(IP, Commands.CONTROL);
        UDPImageReceiver.start(IP);
    }
    public static void stopControl(String IP) throws IOException {
        CommandService.sendCommand(IP, Commands.STOP_CONTROL);
        UDPImageReceiver.stop();
    }
    public static void fileTransfer(List<String> IPs, String savePath, String sendPath) {
        Thread fileSender = new Thread(new FileSender(IPs, savePath, sendPath));
        fileSender.start();
    }
    public static void fileCollect(List<String> IPs, String savePath, String collectPath){
        Thread fileReceiver = new Thread(new FileReceiver(IPs, savePath, collectPath));
        fileReceiver.start();
    }
    public static void shutdown(List<String> IPs) throws UnknownHostException {
        CommandService.sendCommand(IPs, Commands.SHUTDOWN);
    }
    public static void freeze(List<String> IPs) throws UnknownHostException {
        CommandService.sendCommand(IPs, Commands.FREEZE);
    }
    public static void unfreeze(List<String> IPs) throws UnknownHostException {
        CommandService.sendCommand(IPs, Commands.UNFREEZE);
    }
    public static void openApp(List<String> IPs,String appName, String URL) throws UnknownHostException {
        CommandService.sendCommand(IPs, "start "+appName+" \""+URL+"\"");
    }
}
