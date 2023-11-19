package com.LabManagementAppUI.Manager;

import com.LabManagementAppUI.auxiliaryClasses.Lan;
import com.LabManagementAppUI.auxiliaryClasses.LanCreator;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.InetAddress;

public class NetworkManager {
    private static Lan lan;

    public static void createLan(String IPRangeFrom, String IPRangeTo, String multicastAddress, int roomID) {
        lan = LanCreator.createLan(IPRangeFrom, IPRangeTo, multicastAddress, roomID);
        System.out.println("LAN created: " + lan);
    }
    public void connectToComputers(GridPane gridPane){
        int numColumns = 7;
        for (int i = 0; i < lan.getClients().size(); i++) {
            String descriptionName = "Computer" + (i + 1);
            VBox computerWithDescription = createComputerWithDescription(descriptionName, lan.getClient(i+1).getIpAddress());
            gridPane.add(computerWithDescription, i % numColumns, i / numColumns);
        }
    }
    private VBox createComputerWithDescription(String description, String ipAddress) {
        String imageName;

        if (pingIPAddress(ipAddress)) {
            imageName = "Computers Logo/Computer_ON.png";
        } else {
            imageName = "Computers Logo/Computer_OFF.png";
        }
        ImageView imageView = new ImageView(new Image(imageName));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        Label nameLabel = new Label(description);
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(imageView, nameLabel);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
    private boolean pingIPAddress(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            return address.isReachable(100);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
}
}
