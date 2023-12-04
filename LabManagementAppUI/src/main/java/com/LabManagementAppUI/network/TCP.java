package com.LabManagementAppUI.network;

import java.io.*;
import java.net.Socket;

abstract class TCP implements IConnection {

    protected Socket socket=null;
    protected DataInputStream input = null;
    protected DataOutputStream output = null;

    public void send(byte[] data) {
        try {
            output.write(data);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] receive(){
        int bufferSize = 1024;  // You can adjust the buffer size as needed

        byte[] serverResponse = new byte[bufferSize];
        try {
            int bytesRead = input.read(serverResponse);  // Read bytes into the serverResponse byte array
            if (bytesRead == -1) {
                // Handle end of stream or other appropriate action
                throw new IOException("End of stream reached");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return serverResponse;
    }

    @Override
    public void sendString(String message) {

        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendInt(int num) {
        try {
            output.writeInt(num);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendBoolean(boolean flag){
        try {
            output.writeBoolean(flag);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendFileData(byte[] data, int size) {
        try {
            output.write(data, 0, size);
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String receiveString() {
        String serverResponse;
        try {
            serverResponse = input.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public DataInputStream getInputStream(){
        return  input;
    }
    public DataOutputStream getOutputStream(){
        return  output;
    }
}
