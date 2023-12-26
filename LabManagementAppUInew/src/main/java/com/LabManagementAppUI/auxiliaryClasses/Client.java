package com.LabManagementAppUI.auxiliaryClasses;

import java.io.Serializable;

public class Client implements Serializable {


    private int id;
    private String ipAddress;

    private int roomId;

    public Client(int id, String ipAddress, int roomId) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.roomId = roomId;
    }

    public int getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", roomId=" + roomId +
                '}';
    }
}
