package com.LabManagementAppUI.auxiliaryClasses;

import java.util.ArrayList;
import java.util.List;

public class LanCreator {

    private LanCreator() {
        throw new RuntimeException();
    }

    public static Lan createLan(String startIP, String endIP,String multicastIP,int roomId) {

        Lan lan = new Lan(roomId);

        lan.setMulticastIP(multicastIP);

        List<String> ipAddresses = getIPRange(startIP, endIP);

        int countIds = 1;
        for (String ip : ipAddresses) {
            lan.addClient(new Client(countIds, ip, roomId));
            countIds++;
        }

        return lan;
    }

    private static List<String> getIPRange(String startIP, String endIP) {
        List<String> ipRange = new ArrayList<>();

        long start = ipToLong(startIP);
        long end = ipToLong(endIP);

        while (start <= end) {
            ipRange.add(longToIP(start));
            start++;
        }

        return ipRange;
    }

    private static long ipToLong(String ipAddress) {
        String[] octets = ipAddress.split("\\.");
        long result = 0;

        for (int i = 0; i < 4; i++) {
            result += Long.parseLong(octets[i]) << (24 - (8 * i));
        }

        return result;
    }

    private static String longToIP(long ip) {
        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            sb.insert(0, ip & 0xFF);
            if (i < 3) {
                sb.insert(0, '.');
            }
            ip >>= 8;
        }

        return sb.toString();
    }
}
