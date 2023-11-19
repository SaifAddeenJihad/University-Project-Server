package com.LabManagementAppUI.RemoteControl.Controller;


import com.LabManagementAppUI.network.TCPClient;

import javax.swing.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;


class   SendEvents implements KeyListener, MouseMotionListener, MouseListener{
    private Socket cSocket = null;
    private JPanel cPanel = null;
    private PrintWriter writer = null;
    String width = "", height = "";
    double w;
    double h;

    SendEvents(TCPClient tcpClient, JPanel p, String width, String height){

        cPanel = p;
        cPanel.setFocusable(true);
        this.width = width;
        this.height = height;
        w = Double.valueOf(width.trim()).doubleValue();
        h = Double.valueOf(width.trim()).doubleValue();

        //Associate event listeners to the panel

        cPanel.addKeyListener(this);
        cPanel.addMouseListener(this);
        cPanel.addMouseMotionListener(this);

        //Prepare PrintWriter which will be used to send commands to the client
        writer = new PrintWriter(tcpClient.getOutputStream());
    }

    public void mouseDragged(MouseEvent e){
    }

    public void mouseMoved(MouseEvent e){
        double xScale = (double)w/cPanel.getWidth();
        double yScale = (double)h/cPanel.getHeight();
        writer.println(Commands.MOVE_MOUSE.getAbbrev());
        writer.println((int)(e.getX()*xScale));
        writer.println((int)(e.getY()*yScale));
        writer.flush();
    }

    public void mouseClicked(MouseEvent e){
    }

    public void mousePressed(MouseEvent e){
        writer.println(Commands.PRESS_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if(button==3){
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    public void mouseReleased(MouseEvent e){
        writer.println(Commands.RELEASE_MOUSE.getAbbrev());
        int button = e.getButton();
        int xButton = 16;
        if(button==3){
            xButton = 4;
        }
        writer.println(xButton);
        writer.flush();
    }

    public void mouseEntered(MouseEvent e){
    }

    public void mouseExited(MouseEvent e){
    }

    public void keyTyped(KeyEvent e){
    }

    public void keyPressed(KeyEvent e){
        writer.println(Commands.PRESS_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }

    public void keyReleased(KeyEvent e){
        writer.println(Commands.RELEASE_KEY.getAbbrev());
        writer.println(e.getKeyCode());
        writer.flush();
    }
    public enum Commands{
        PRESS_MOUSE(-1),
        RELEASE_MOUSE(-2),
        PRESS_KEY(-3),
        RELEASE_KEY(-4),
        MOVE_MOUSE(-5);

        private int abbrev;

        Commands(int abbrev){
            this.abbrev = abbrev;
        }

        public int getAbbrev(){
            return abbrev;
        }
    }
}
