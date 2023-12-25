package com.LabManagementAppUI.MiniServices;

public class MiniServices {
    private MiniServices(){}

    public static String getLocalMac() throws Exception {
        return CommandRunner.executePowerShellCommand("(Get-NetAdapter | Where-Object { $.Status -eq 'Up' -and $.InterfaceDescription -notmatch 'Virtual|VPN' } | Sort-Object -Property InterfaceIndex)[0].MacAddress");
    }
    public static String getRemoteMac(String ipAddress) throws Exception {
        return CommandRunner.executePowerShellCommand("(Get-NetNeighbor -IPAddress '" + ipAddress + "' | Where-Object { $_.State -ne 'Unreachable' }).LinkLayerAddress");
    }
}
