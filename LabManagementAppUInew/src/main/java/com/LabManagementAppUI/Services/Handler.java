package com.LabManagementAppUI.Services;

import com.LabManagementAppUI.FileTransfer.FileReceiver;
import com.LabManagementAppUI.FileTransfer.FileSender;
import com.LabManagementAppUI.RemoteControl.Controller.UDPImageReceiver;
import com.LabManagementAppUI.Stream.UPDStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

public class Handler {
    public static volatile Queue<byte[]> baos = new LinkedList<>();
    private static Thread streamCapture, streamSender;
    static Thread controlThread=null;
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

    public static void startControl(String IP) throws IOException {
        controlThread = new Thread(new UDPImageReceiver(IP));
        CommandService.sendCommand(IP, Commands.CONTROL);
        controlThread.start();
    }

    public static void stopControl(String IP) throws IOException {
        CommandService.sendCommand(IP, Commands.STOP_CONTROL);
        UDPImageReceiver.stop();
        controlThread.interrupt();

        }

    public static void fileTransfer(List<String> IPs, String savePath, String sendPath) {
        Thread fileSender = new Thread(new FileSender(IPs, savePath, sendPath));
        fileSender.start();
    }

    public static void fileCollect(List<String> IPs, String savePath, String collectPath) {
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

    public static void openApp(List<String> IPs, String appName, String URL) throws UnknownHostException {
        CommandService.sendCommand(IPs, "start " + appName + " \"" + URL + "\"");
    }
}
