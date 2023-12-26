package com.LabManagementAppUI.RemoteControl.Controller;

import com.LabManagementAppUI.FileTransfer.FileSender;
import com.LabManagementAppUI.Manager.ConfigurationManager;
import com.LabManagementAppUI.Services.Handler;
import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.ConnectionFactory;
import com.LabManagementAppUI.network.IConnectionNames;
import com.LabManagementAppUI.network.TCPClient;
import com.LabManagementAppUI.network.UDPServer;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xerial.snappy.Snappy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.Socket;

public class UDPImageReceiver implements Runnable {
    private static final Logger logger = LogManager.getLogger(UDPImageReceiver.class);
    private static  JFrame frame ;
    private static TCPClient tcpClient;

    //JDesktopPane represents the main container that will contain all connected clients' screens

    private static JDesktopPane desktop;
    private static  JInternalFrame interFrame;
    private static  JPanel cPanel;
    private static  int maxBufferSize = 65507;
    private static boolean isRunning = true;
    private static String ipAddress;
    public UDPImageReceiver(String ipAddress){
        this.ipAddress = ipAddress;
        desktop = new JDesktopPane();
        interFrame = new JInternalFrame("Server Screen", true);
        cPanel = new JPanel();
        isRunning = true;
        frame= new JFrame();

    };
    public static void stop() {
        isRunning = false;
        interFrame.dispose();
        frame.dispose();
        tcpClient.close();
        logger.info("finish receiving image and displaying it");
    }

    public static void start() {

        UDPServer connection = (UDPServer) ConnectionFactory.getIConnection(IConnectionNames.UDP_SERVER);
        connection.initialize(IPorts.CONTROL, null);


        initializeFrame();
        startSendEvents(ipAddress);
        logger.info("start receiving image and displaying it");

        while (isRunning) {
            byte[] fullBuffer = reciveImage(connection);
            displayImage(fullBuffer);
        }
        connection.close();
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
        while (isRunning) {
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
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);        //CHECK THIS LINE
        frame.setVisible(true);
        interFrame.setLayout(new BorderLayout());
        interFrame.getContentPane().add(cPanel, BorderLayout.CENTER);
        interFrame.setSize(100, 100);//check this later
        desktop.add(interFrame);

        try {
            //Initially show the internal frame maximized
            interFrame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            logger.error(new RuntimeException());

        }
        JButton addButton = new JButton("Stop Control!");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Handler.stopControl(ConfigurationManager.getInstance().loadControlConfigurationFromFile());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    JOptionPane.showMessageDialog(frame, "Button Clicked!");
                }
            });
        interFrame.getContentPane().add(addButton, BorderLayout.SOUTH);

        //This allows to handle KeyListener events
        cPanel.setFocusable(true);
        interFrame.setVisible(true);
        logger.info("initializeFrame completed");
    }

    static void startSendEvents(String ipAddress) {
        tcpClient = (TCPClient) ConnectionFactory.getIConnection(IConnectionNames.TCP_CLIENT);
        tcpClient.initialize(IPorts.CONTROL + 10, ipAddress);
        new SendEvents(tcpClient, cPanel, "1920", "1080");
        logger.info("startSendEvents completed");
    }
    @Override
    public void run() {
        start();
    }
}
