package com.LabManagementAppUI.RemoteControl.Controller;

import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.*;

import org.xerial.snappy.Snappy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.Socket;

public class UDPImageReceiver {
    private static final JFrame frame = new JFrame();

    //JDesktopPane represents the main container that will contain all connected clients' screens

    private static final JDesktopPane desktop = new JDesktopPane();
    private static final Socket cSocket = null;
    private static final JInternalFrame interFrame = new JInternalFrame("Server Screen", true, true, true);
    private static final JPanel cPanel = new JPanel();
    private static final int maxBufferSize = 65507;
    private static boolean isRunning=true;
    public static void stop(){
        isRunning=false;
    }

    public static void start(String ipAddress) throws IOException {
        UDPServer connection = (UDPServer) ConnectionFactory.getIConnection(IConnectionNames.UDP_SERVER);
        connection.initialize(IPorts.CONTROL, null);
        //DatagramSocket datagramSocket =new DatagramSocket(1234);
        startSendEvents(ipAddress);

        initializeFrame();

        while (isRunning) {
            byte[] fullBuffer = reciveImage(connection);
            displayImage(fullBuffer);
        }
        frame.dispose();
    }

    private static void displayImage(byte[] fullBuffer) {
        try {
            byte[] uncompressedBuffer = Snappy.uncompress(fullBuffer);
            InputStream inStream = new ByteArrayInputStream(uncompressedBuffer);
            Image image = ImageIO.read(inStream);


            Graphics graphics = cPanel.getGraphics();
            graphics.drawImage(image, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] reciveImage(UDPServer connection) {
        byte[] fullBuffer = new byte[0];
        while (true) {
            DatagramPacket datagramPacket = connection.receivePacket();

            byte[] receivedData = datagramPacket.getData();
            int receivedLength = datagramPacket.getLength();

            // Concatenate received chunk to the full buffer
            byte[] newBuffer = new byte[fullBuffer.length + receivedLength];
            System.arraycopy(fullBuffer, 0, newBuffer, 0, fullBuffer.length);
            System.arraycopy(receivedData, 0, newBuffer, fullBuffer.length, receivedLength);
            fullBuffer = newBuffer;

            if (receivedLength < maxBufferSize) {
                break;
            }
        }
        return fullBuffer;
    }

    private static void initializeFrame() {
        frame.add(desktop, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);        //CHECK THIS LINE
        frame.setVisible(true);
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
        interFrame.setSize(100, 100);
        desktop.add(interFrame);

        try {
            //Initially show the internal frame maximized
            interFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }
        //This allows to handle KeyListener events
        cPanel.setFocusable(true);
        interFrame.setVisible(true);
    }

    static void startSendEvents(String ipAddress) {
        TCPClient tcpClient = (TCPClient) ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        tcpClient.initialize(IPorts.CONTROL+10, ipAddress);
        new SendEvents(tcpClient, cPanel, "1920", "1080");
    }
}
