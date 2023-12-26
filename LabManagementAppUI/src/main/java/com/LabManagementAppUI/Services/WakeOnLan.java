package com.LabManagementAppUI.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class WakeOnLan {

    public static final int PORT = 9;

    public static void wakeOnLan(List<String> IPs) {

        for (String ipAddress : IPs) {
            try {
                // Get the MAC address using ARP
                String macAddress = getMacAddress(ipAddress);

                if (macAddress != null) {
                    byte[] macBytes = getMacBytes(macAddress);
                    byte[] bytes = new byte[6 + 16 * macBytes.length];

                    for (int i = 0; i < 6; i++) {
                        bytes[i] = (byte) 0xff;
                    }

                    for (int i = 6; i < bytes.length; i += macBytes.length) {
                        System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
                    }

                    InetAddress address = InetAddress.getByName(ipAddress);
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);

                    DatagramSocket socket = new DatagramSocket();
                    socket.send(packet);
                    socket.close();

                    System.out.println("Wake-on-LAN packet sent to " + ipAddress + " with MAC address " + macAddress);
                } else {
                    System.out.println("Failed to get MAC address for " + ipAddress);
                }
            } catch (Exception e) {
                System.out.println("Failed to send Wake-on-LAN packet to " + ipAddress + ": " + e.getMessage());
            }
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");

        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    private static String getMacAddress(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("arp -a " + ipAddress);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(ipAddress)) {
                    // Extract the MAC address from the ARP table entry
                    int startIndex = line.indexOf("at") + 3;
                    int endIndex = line.indexOf(" ", startIndex);
                    return line.substring(startIndex, endIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
