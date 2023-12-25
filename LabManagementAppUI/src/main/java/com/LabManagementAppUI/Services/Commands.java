package com.LabManagementAppUI.Services;

public enum Commands {
    STREAM("Stream"),
    CLOSE_STREAM("Close Stream"),
    UDP_STREAM("UDP Stream"),
    CLOSE_UDP_STREAM("Close UDP Stream"),
    CONTROL("Control"),
    STOP_CONTROL("Stop Control"),
    FILE_TRANSFER("File Transfer"),
    FILE_COLLECT("File Collect"),
    SHUTDOWN("Shutdown"),
    FREEZE("Freeze"),
    UNFREEZE("Unfreeze"),
    OPEN_APP("Open App"),
    UNBLOCK_ALL("Unblock All"),
    BLOCK_WEBSITE("Block Website"),
    ALLOW_DEFAULT("Allow Default"),
    ALLOW_WEBSITE("Allow Website");


    public final String label;
    private Commands(String label){
        this.label = label;
    }
}