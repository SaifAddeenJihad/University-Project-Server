package com.LabManagementAppUI.Services;

public enum Commands {
    STREAM("Stream"),
    CLOSE_STREAM("Close Stream"),
    CONTROL("Control"),
    STOP_CONTROL("Stop Control"),
    FILE_TRANSFER("File Transfer"),
    FILE_COLLECT("File Collect"),
    SHUTDOWN("Shutdown"),
    FREEZE("Freeze"),
    UNFREEZE("Unfreeze"),
    OPEN_APP("Open App"),
    BLOCK_INTERNET("Block Internet");


    public final String label;
    private Commands(String label){
        this.label = label;
    }
}