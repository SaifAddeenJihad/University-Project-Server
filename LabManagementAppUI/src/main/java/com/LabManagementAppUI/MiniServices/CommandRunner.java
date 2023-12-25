package com.LabManagementAppUI.MiniServices;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandRunner {

//38:F3:AB:91:3E:2D
    public static boolean executeCommand(String command) throws Exception {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        Process process = builder.start();
        int exitCode = process.waitFor();
        ProcessOutputReader.printProcessOutput(process);
        if (exitCode == 0) {
            return true;
        } else {
            return false;
        }
    }
    public static String executePowerShellCommand(String command) throws Exception {

        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("Powershell.exe", "/c", command);
        } else {
            builder.command("sh", "-c", command);
        }
        Process process = builder.start();
        String output = ProcessOutputReader.getProcessOutput(process);
        return output;
    }
}

class ProcessOutputReader {
    static void printProcessOutput(Process process) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
        InputStreamReader errorStream = new InputStreamReader(process.getErrorStream());
        BufferedReader inputReader = new BufferedReader(inputStream);
        BufferedReader errorReader = new BufferedReader(errorStream);
        String line;
        if ((line = inputReader.readLine()) != null) {
            System.out.println("Standard Output:");
            System.out.println(line);
            while ((line = inputReader.readLine()) != null) {
                System.out.println(line);
            }
        } else {
            System.err.println("Error Output:");
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
        }
    }
    static String getProcessOutput(Process process) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
        BufferedReader inputReader = new BufferedReader(inputStream);
        String line;
        line = inputReader.readLine();
        if (line == null)
            return "";
        return line;
    }

}

