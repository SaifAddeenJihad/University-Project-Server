package com.LabManagementAppUI.auxiliaryClasses;

import com.LabManagementAppUI.auxiliaryClasses.Client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Lan implements Serializable {
    private static final String FILE_NAME = "lan.ser";
    private Map<Integer, Client> clients = new HashMap<>();
    private int roomId;

    private String multicastIP;

    public Lan(int roomId) {
        this.roomId = roomId;
    }

    public static Lan deserializeFromFile() {
        try (FileInputStream fileIn = new FileInputStream(FILE_NAME);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return (Lan) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public Client getClient(int id) {
        return id != 0 ? clients.get(id) : null;
    }

    public Client getClientByIpAddress(String ipAddress) {
        return clients.values()
                .stream()
                .filter(client -> client.getIpAddress().equals(ipAddress))
                .findFirst()
                .orElse(null);
    }

    public void serializeToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(this);
            System.out.println("Serialized data is saved in " + FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClient(Client client) {
        if (client != null && client.getId() != 0) {
            clients.put(client.getId(), client);
            serializeToFile();
        }
    }

    public void removeClient(int clientId) {
        if (clientId != 0) {
            clients.remove(clientId);
            serializeToFile();
        }
    }

    public int getRoomId() {
        return roomId;
    }

    public String getMulticastIP() {
        return multicastIP;
    }

    public void setMulticastIP(String multicastIP) {
        this.multicastIP = multicastIP;
    }

    @Override
    public String toString() {
        return "Lan{" +
                "clients=" + clients +
                ", roomId=" + roomId +
                ", multicastIP='" + multicastIP + '\'' +
                '}';
    }
}
