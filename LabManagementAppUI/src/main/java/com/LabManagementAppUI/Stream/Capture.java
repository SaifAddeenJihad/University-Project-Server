package com.LabManagementAppUI.Stream;

import com.LabManagementAppUI.Services.Handler;
import org.xerial.snappy.Snappy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class
Capture implements Runnable{

    @Override
    public void run() {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        Rectangle capture = new Rectangle(1920, 1080);
        while (true) {

            BufferedImage image = robot.createScreenCapture(capture);
            Point mouse = MouseInfo.getPointerInfo().getLocation();

            BufferedImage cursor ;
            try {
                cursor = ImageIO.read(new File("cursor.cur"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int cursorWidth = 20;
            int cursorHeight = 20;
            Image scaledCursor = cursor.getScaledInstance(cursorWidth, cursorHeight, Image.SCALE_SMOOTH);
            BufferedImage scaledCursorImage = new BufferedImage(cursorWidth, cursorHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledCursorImage.createGraphics();
            g2d.drawImage(scaledCursor, 0, 0, null);
            Graphics2D graphics = image.createGraphics();
            graphics.drawImage(scaledCursorImage, mouse.x, mouse.y, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "jpeg", baos);
                byte[] fullBuffer = baos.toByteArray();
/*                byte[] compressed= new byte[0];
                compressed = Snappy.compress(fullBuffer);*/
                Handler.baos.add(fullBuffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Handler.baos.size());
        }
    }
}
