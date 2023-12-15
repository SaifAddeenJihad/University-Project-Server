package com.LabManagementAppUI.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

abstract class TCP implements IConnection {

    private static final Logger logger = LogManager.getLogger(TCP.class);

    protected Socket socket = null;
    protected DataInputStream input = null;
    protected DataOutputStream output = null;

    public void send(byte[] data) {
        try {
            output.write(data);
            output.flush();
            logger.info("send TCP done successfully");
        } catch (IOException e) {
            logger.error("send TCP failed " + new RuntimeException());
        }

    }

    public byte[] receive() {
        int bufferSize = 1024;

        byte[] serverResponse = new byte[bufferSize];
        try {
            int bytesRead = input.read(serverResponse);
            if (bytesRead == -1) {
                logger.info("TCP receive end of stream reached" + new IOException());
            }
        } catch (IOException e) {
            logger.error("TCP receive failed " + new RuntimeException());
        }

        return serverResponse;
    }

    @Override
    public void sendString(String message) {

        try {
            output.writeUTF(message);
            output.flush();
            logger.info("TCP sendString done successfully");
        } catch (IOException e) {
            logger.error("TCP sendString failed " + new RuntimeException());
        }
    }

    public void sendInt(int num) {
        try {
            output.writeInt(num);
            output.flush();
            logger.info("TCP sendInt done successfully");
        } catch (IOException e) {
            logger.error("TCP sendInt failed " + new RuntimeException());
        }
    }

    public void sendBoolean(boolean flag) {
        try {
            output.writeBoolean(flag);
            output.flush();
            logger.info("TCP sendBoolean done successfully");
        } catch (IOException e) {
            logger.error("TCP sendBoolean failed " + new RuntimeException());
        }
    }

    public void sendFileData(byte[] data, int size) {
        try {
            output.write(data, 0, size);
            output.flush();
            logger.info("TCP sendFileData done successfully");
        } catch (IOException e) {
            logger.error("TCP sendFileData failed " + new RuntimeException());
        }
    }

    @Override
    public String receiveString() {
        String serverResponse=null;
        try {
            serverResponse = input.readUTF();
            logger.info("TCP receiveString done successfully");
        } catch (IOException e) {
            logger.error("TCP sendFileData failed " + new RuntimeException());
        }

        return serverResponse;
    }

    public boolean receiveBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int receiveInt() {
        try {
            return input.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void receiveFile(byte[] fileContent) {
        try {
            input.readFully(fileContent);
            logger.info("TCP receiveFile done successfully");
        } catch (IOException e) {
            logger.error("TCP receiveFile" +
                    " failed " + new RuntimeException());
        }
    }

    public DataInputStream getInputStream() {
        return input;
    }

    public DataOutputStream getOutputStream() {
        return output;
    }
}
