package com.LabManagementAppUI.Manager;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private String multicastAddress;
    private String roomID;
    private String IPRangeFrom;
    private String IPRangeTo;

    private ConfigurationManager() {
    }
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    public void loadConfigurationFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 4) {
                    multicastAddress = parts[0];
                    roomID = parts[1];
                    IPRangeFrom = parts[2];
                    IPRangeTo = parts[3];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfigurationToFile(String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("config.txt", false))) {
            writer.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidRoomID(String roomID) {
        String idPattern = "\\d{1,2}";
        Pattern pattern = Pattern.compile(idPattern);
        Matcher matcher = pattern.matcher(roomID);
        return matcher.matches();
    }

    public boolean isValidIPAddress(String ipAddress) {
        String ipPattern = "^(10\\..*|172\\.(1[6-9]|2[0-9]|3[0-1])\\..*|192\\.168\\..*|127\\.0\\.0\\.1)$";

        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(ipAddress);

        return matcher.matches();
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getIPRangeFrom() {
        return IPRangeFrom;
    }

    public String getIPRangeTo() {
        return IPRangeTo;
    }
}
