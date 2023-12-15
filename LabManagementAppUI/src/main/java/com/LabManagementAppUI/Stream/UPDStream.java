package com.LabManagementAppUI.Stream;

import com.LabManagementAppUI.auxiliaryClasses.IPorts;
import com.LabManagementAppUI.network.ConnectionFactory;
import com.LabManagementAppUI.network.IConnectionNames;
import com.LabManagementAppUI.network.TCPClient;
import com.LabManagementAppUI.network.UDPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class UPDStream implements Runnable {
    private static final Logger logger = LogManager.getLogger(TCPClient.class);
    private final String clientIP;
    private UDPClient udpClient;

    public UPDStream(String clientIP) {
        this.clientIP = clientIP;
    }

    @Override
    public void run() {
        udpClient = (UDPClient) ConnectionFactory.getIConnection(IConnectionNames.UDP_CLIENT);
        udpClient.initialize(IPorts.UDP_STREAM, clientIP);

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            logger.error(new AWTException(""));
        }
        Rectangle capture = new Rectangle(1920, 1080);
        logger.info("start capturing");
        while (!Thread.currentThread().isInterrupted()) {

            BufferedImage image = robot.createScreenCapture(capture);
            Point mouse = MouseInfo.getPointerInfo().getLocation();

            /*BufferedImage cursor;
            try {
                cursor = ImageIO.read(new File("cursor.cur"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
            int cursorWidth = 20;
            int cursorHeight = 20;
            //Image scaledCursor = cursor.getScaledInstance(cursorWidth, cursorHeight, Image.SCALE_SMOOTH);
            BufferedImage scaledCursorImage = new BufferedImage(cursorWidth, cursorHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledCursorImage.createGraphics();
            //g2d.drawImage(scaledCursor, 0, 0, null);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(scaledCursorImage, mouse.x, mouse.y, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] fullBuffer;
            try {
                ImageIO.write(image, "jpeg", baos);
                fullBuffer = baos.toByteArray();
/*                byte[] compressed= new byte[0];
                compressed = Snappy.compress(fullBuffer);*/
            } catch (IOException e) {
                throw new RuntimeException();
            }
            int chunkSize = 65507;
            int totalChunks = (int) Math.ceil((double) fullBuffer.length / chunkSize);

            for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                int offset = chunkIndex * chunkSize;
                int length = Math.min(chunkSize, fullBuffer.length - offset);
                byte[] chunkBuffer = new byte[length];
                System.arraycopy(fullBuffer, offset, chunkBuffer, 0, length);

                //DatagramPacket datagramPacket = new DatagramPacket(chunkBuffer, chunkBuffer.length, multicastGroup, 1234);
                udpClient.send(chunkBuffer);
            }
        }
    }
}
