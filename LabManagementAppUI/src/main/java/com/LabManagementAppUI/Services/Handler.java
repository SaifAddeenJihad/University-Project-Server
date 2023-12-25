package com.LabManagementAppUI.Services;

import com.LabManagementAppUI.FileTransfer.FileReceiver;
import com.LabManagementAppUI.FileTransfer.FileSender;
import com.LabManagementAppUI.RemoteControl.Controller.UDPImageReceiver;
import com.LabManagementAppUI.Stream.UPDStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Handler {
    public static volatile Queue<byte[]> baos = new LinkedList<>();
    private static Thread streamCapture, streamSender;
    private static List<Thread> udpStreamThreads;
    private Handler() {
    }

    /* public static void closeStream(List<String> IPs) {
         CommandService.sendCommand(IPs, Commands.CLOSE_STREAM);
         streamCapture.interrupt();
         streamSender.interrupt();
     }*/
    public static void startUdpStream(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.UDP_STREAM);
        udpStreamThreads = new ArrayList<>();
        for (String ip : IPs) {
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
        for (Thread thread : udpStreamThreads) {
            thread.interrupt();
        }
    }

    public static void startControl(String IP) {
        CommandService.sendCommand(IP, Commands.CONTROL);
        UDPImageReceiver.start(IP);
    }

    public static void stopControl(String IP) {
        CommandService.sendCommand(IP, Commands.STOP_CONTROL);
        UDPImageReceiver.stop();//thread?
    }

    public static void fileTransfer(List<String> IPs, String savePath, String sendPath) {
        Thread fileSender = new Thread(new FileSender(IPs, savePath, sendPath));
        fileSender.start();
    }

    public static void fileCollect(List<String> IPs, String savePath, String collectPath) {
        Thread fileReceiver = new Thread(new FileReceiver(IPs, savePath, collectPath));
        fileReceiver.start();
    }

    public static void shutdown(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.SHUTDOWN);
    }

    public static void freeze(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.FREEZE);
    }

    public static void unfreeze(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.UNFREEZE);
    }

    public static void openApp(List<String> IPs, String appName, String URL) {
        CommandService.sendCommand(IPs, "start " + appName + " \"" + URL + "\"");
    }
    public static void allowDefault(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.ALLOW_DEFAULT);
    }
    public static void unblockAll(List<String> IPs) {
        CommandService.sendCommand(IPs, Commands.UNBLOCK_ALL);
    }
    public static void allowWebsite(List<String> IPs, String URL) {
        String command = Commands.ALLOW_WEBSITE.label + ":" + URL;
        CommandService.sendCommand(IPs, command);
    }
    public static void blockWebsite(List<String> IPs, String URL) {
        String command = Commands.BLOCK_WEBSITE.label + ":" + URL;
        CommandService.sendCommand(IPs, command);
    }
}
