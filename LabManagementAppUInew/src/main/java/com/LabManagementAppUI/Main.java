package com.LabManagementAppUI;

import com.LabManagementAppUI.Manager.ConfigurationManager;
import com.LabManagementAppUI.Services.Handler;
import com.LabManagementAppUI.Services.WakeOnLan;
import com.LabManagementAppUI.auxiliaryClasses.Client;
import com.LabManagementAppUI.auxiliaryClasses.Lan;
import com.LabManagementAppUI.auxiliaryClasses.LanCreator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    private GridPane gridPane;
    private BorderPane borderPane;
    static Stage primaryStage;
    static Lan lan;
    static HBox hBox;
    static String pathFrom;
    static String pathTo;
    private String multicastAddress;
    private String roomID;
    private String IPRangeFrom;
    private String IPRangeTo;
    private ScheduledExecutorService executorService;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        this.borderPane = new BorderPane();
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop[]{
                new Stop(0, Color.web("#FFFFFF")),
                new Stop(1, Color.web("#BDCDEF"))
        });

        hBox = new HBox(20);
        hBox.setPrefHeight(25);
        hBox.setBackground(createBackground(Color.web("#D4D2D4")));

        this.gridPane = new GridPane();
        gridPane.setBackground(createBackground(gradient));
        gridPane.setBorder(createSolidBorder(Color.GRAY, 1));


        Button configButton = new Button("Config");
        configButton.getStyleClass().add("button-21");
        configButton.setOnAction(e -> {
            config();
        });

/*        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(this::updateComputersStatus, 0, 30, TimeUnit.SECONDS);
        primaryStage.setOnCloseRequest(event -> {
            executorService.shutdown();
            Platform.exit();
        });*/ 


        Button previousConfigButton = new Button("Last Config");
        previousConfigButton.getStyleClass().add("button-21"); // Add the style class
        previousConfigButton.setOnAction(e -> {
            ConfigurationManager.getInstance().loadConfigurationFromFile();
            lan= LanCreator.createLan(ConfigurationManager.getInstance().getIPRangeFrom(),
                    ConfigurationManager.getInstance().getIPRangeTo(),
                    ConfigurationManager.getInstance().getMulticastAddress(),
                    Integer.parseInt(ConfigurationManager.getInstance().getRoomID()));
            connectToComputers();
        });
        Image logoImage = new Image("FetLogo.png");
        ImageView logoImageView = new ImageView(logoImage);
        logoImageView.setFitWidth(300);
        logoImageView.setFitHeight(300);
        VBox logoVbox = new VBox(10);
        logoVbox.getChildren().add(logoImageView);
        logoVbox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to Lab Management Application");
        welcomeLabel.getStyleClass().add("welcome-text"); // Add the style class
        welcomeLabel.setFont(Font.font("System", FontWeight.NORMAL, 30));
        welcomeLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: #3a9bff;");
        VBox welcomeVbox = new VBox(10);
        VBox welcomeButtons= new VBox(10);
        welcomeVbox.getChildren().add(welcomeLabel);
        welcomeButtons.getChildren().addAll(configButton,previousConfigButton);
        welcomeVbox.setAlignment(Pos.CENTER);
        welcomeButtons.setAlignment(Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(logoVbox, 0, 0);


        gridPane.add(welcomeLabel, 0, 1);
        gridPane.add(welcomeButtons, 0, 2);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        borderPane.setCenter(gridPane);
        borderPane.setTop(hBox);
        Image windowIcon = new Image("WindowIcon.png");
        primaryStage.getIcons().add(windowIcon);
        Scene scene = new Scene(borderPane, 1000, 700);
        scene.getStylesheets().add(this.getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Lab Management Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
/*
    private void loadConfigurationFromFile() {
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
*/

    private void config(){
        gridPane.getChildren().clear();
        Label multicastAddressLabel = new Label("Multicast Address:");
        TextField multicastAddressText = new TextField();
        Label multicastAddressDefault  = new Label("/Default 239.0.0.1 ");
        Label roomIDLabel = new Label("Room ID:");
        TextField roomIDText = new TextField();

        Label IPRangeFromLabel = new Label("IP Range From:");
        TextField IPRangeFromText = new TextField();

        Label IPRangeToLabel = new Label("To:");
        TextField IPRangeToText = new TextField();

        Button configButton = new Button("Save Config");
        configButton.getStyleClass().add("button-21");
        VBox configVbox = new VBox(10);
        configVbox.getChildren().add(configButton);
        configVbox.setAlignment(Pos.CENTER);

        StringBuilder errorStatement = new StringBuilder();
        Label errorText = new Label(errorStatement.toString());
        errorText.setStyle("-fx-text-fill: red;");
        multicastAddressDefault.setStyle("-fx-text-fill: #3a9bff;");

        gridPane.add(roomIDLabel, 0, 0);
        gridPane.add(roomIDText, 1, 0);
        gridPane.add(IPRangeFromLabel, 0, 1);
        gridPane.add(IPRangeFromText, 1, 1);
        gridPane.add(IPRangeToLabel, 2, 1);
        gridPane.add(IPRangeToText, 3, 1);
        gridPane.add(multicastAddressLabel, 0, 2);
        gridPane.add(multicastAddressText, 1, 2);
        gridPane.add(multicastAddressDefault, 3, 2);
        gridPane.add(errorText, 1, 3);
        gridPane.add(configVbox, 1, 4);


        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(20);
        gridPane.setVgap(20);




        borderPane.setCenter(gridPane);


        configButton.setOnAction(e -> {
            roomID = roomIDText.getText();
            IPRangeFrom = IPRangeFromText.getText();
            IPRangeTo = IPRangeToText.getText();
            multicastAddress = multicastAddressText.getText();
            errorStatement.setLength(0);

            if (multicastAddress.isEmpty()) {
                multicastAddress = "239.0.0.1";
            }
            if (!isValidIPAddress(IPRangeFrom) || !isValidIPAddress(IPRangeTo)) {
                errorStatement.append("Invalid IP Range. ");
            }
            if (!isValidRoomID(roomID)) {
                errorStatement.append("Invalid Room ID. ");
            }
            errorText.setText(errorStatement.toString());
            if (errorStatement.isEmpty()) {
                lan= LanCreator.createLan(IPRangeFrom, IPRangeTo, multicastAddress, Integer.parseInt(roomID));
                ConfigurationManager.getInstance().saveConfigurationToFile(multicastAddress + ":" + roomID + ":" + IPRangeFrom + ":" + IPRangeTo);
                connectToComputers();
            }
        });
    }
/*    private void saveToFile( String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("config.txt", true))) {
            writer.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void connectToComputers(){
        gridPane.getChildren().clear();
        hBox.getChildren().clear();
        int numColumns = 7;
        //int numRows = 5;
        for (int i = 0; i < lan.getClients().size(); i++) {

            String descriptionName = "Computer" + (i + 1);
            VBox computerWithDescription = createComputerWithDescription(descriptionName, lan.getClient(i+1).getIpAddress());

            gridPane.add(computerWithDescription, i % numColumns, i / numColumns);
        }
        Runnable[] actions = {
                this::shareToStudents,
                this::shareFromStudent,
                this::takeTheControl,
                this::powerManagement,
                this::sendFile,
                this::receiveFile,
                this::lockScreen,
                this::openWebsite,
        };
        VBox leftButtonBox = new VBox(20);
        leftButtonBox.setBackground(createBackground(Color.web("#F6F5F4")));

        for (int i = 0; i < 8; i++) {
            ImageView imageView = createImageView("icon"+(i+1)+".png");

            Button button = createButtonWithAction(imageView, actions[i]);
            button.getStyleClass().add("button-11");

            leftButtonBox.getChildren().add(button);
        }
        borderPane.setLeft(leftButtonBox);
        Hyperlink link = new Hyperlink("Connection");
        link.setStyle("-fx-border-style: none;-fx-border-color: none; -fx-text-fill: black;");
        link.setOnAction((event) -> {
            connectToComputers();
        });

        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(link);
    }

    private boolean isValidRoomID(String roomID) {
        String idPattern = "\\d{1,2}";
        Pattern pattern = Pattern.compile(idPattern);
        Matcher matcher = pattern.matcher(roomID);
        return matcher.matches();
    }
    private boolean isValidIPAddress(String ipAddress) {
        String ipPattern = "^(10\\..*|172\\.(1[6-9]|2[0-9]|3[0-1])\\..*|192\\.168\\..*|127\\.0\\.0\\.1)$";

        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(ipAddress);

        return matcher.matches();
    }
    private void shareToStudents(){
        gridPane.getChildren().clear();
        System.out.println("Sharing Screen To Students...");
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(100, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);

        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        Button udpShareButton = new Button("Start Sharing!");
        udpShareButton.getStyleClass().add("button-21");

        udpShareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            Handler.startUdpStream(selectedIPs);
        });
        Button stopUdpShareButton = new Button("Stop Sharing!");
        stopUdpShareButton.getStyleClass().add("button-21");

        stopUdpShareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            Handler.closeUdpStream(selectedIPs);
        });

        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView,  udpShareButton, stopUdpShareButton);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.getChildren().add(shareVbox);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);
    }
    private void shareFromStudent(){
        gridPane.getChildren().clear();
       /* System.out.println("Sharing Screen from Student...");

        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(150, 100);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        //studentIPs.addAll("192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5", "192.168.1.6", "192.168.1.7");
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button shareButton = new Button("Sharing Screen from Student...");
        shareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            shareScreen(selectedIPs);
            // MulticastImageReceiver.receivingStream();
        });
        HBox hboxx = new HBox(10);  // 10 is the spacing between the children
        hboxx.getChildren().addAll(studentIPsListView, shareButton);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.getChildren().add(hboxx);  // Added at column 0, row 0
        hboxx.setAlignment(Pos.CENTER);
        hboxx.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);*/
    }
    private void takeTheControl(){gridPane.getChildren().clear();
        gridPane.getChildren().clear();
        System.out.println("Take Control from Student");
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        Button takeControlButton = new Button("Take Control!");
        takeControlButton.getStyleClass().add("button-21");

        takeControlButton.setOnAction(e -> {
            String selectedIP = studentIPsListView.getSelectionModel().getSelectedItems().get(0);

                    ConfigurationManager.getInstance().saveControlConfigurationToFile(selectedIP);
            try {
                Handler.startControl(selectedIP);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
     /*   Button stopControlButton = new Button("Stop Control!");
        stopControlButton.getStyleClass().add("button-21");
        stopControlButton.setOnAction(e -> {
            try {
                Handler.stopControl(studentIPsListView.getSelectionModel().getSelectedItems().get(0));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });*/

        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView, takeControlButton);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.getChildren().add(shareVbox);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);

    }
    private void powerManagement(){
        gridPane.getChildren().clear();
        System.out.println("Power Management...");
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button turnOnButton = new Button("Turn On!");
        turnOnButton.getStyleClass().add("button-21");
        turnOnButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            WakeOnLan.wakeOnLan(selectedIPs);
        });
        Button turnOffButton = new Button("Turn Off!");
        turnOffButton.getStyleClass().add("button-21");
        turnOffButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            try {
                Handler.shutdown(selectedIPs);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });
        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView, turnOnButton,turnOffButton);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.getChildren().add(shareVbox);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);
    }
    private void sendFile(){gridPane.getChildren().clear();
        System.out.println("Send file to student...");
        gridPane.getChildren().clear();
        FileChooser fileChooser = new FileChooser();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        VBox vBox = getFileFolder( fileChooser, directoryChooser);
        vBox.setAlignment(Pos.BASELINE_CENTER);
        vBox.setSpacing(15);
        Button button1 = new Button("Select Destination");
        Label label2 = new Label("Select the Destination in the Student Side:");
        vBox.getChildren().add(label2);
        button1.getStyleClass().add("button-21");
        vBox.getChildren().add(button1);
        button1.setOnAction(e -> {
            File selectedDirectory;
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory!=null)
                pathTo = selectedDirectory.getAbsolutePath();
        });

        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button shareButton = new Button("Send File");
        shareButton.getStyleClass().add("button-21");
        shareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            Handler.fileTransfer(selectedIPs,pathTo,pathFrom);
        });
        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView, shareButton);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.add(vBox,0,0);
        gridPane.add(shareVbox,0,1);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);
    }
    private void receiveFile(){
        System.out.println("Receiving Files From Students...");
        gridPane.getChildren().clear();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Button button = new Button("Select From Location");
        button.getStyleClass().add("button-21");

        button.setOnAction(e -> {
            File selectedDirectory;
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory!=null)
                pathFrom = selectedDirectory.getAbsolutePath();
        });
        Button button1 = new Button("Select Destination");
        button1.getStyleClass().add("button-21");
        button1.setOnAction(e -> {
            File selectedDirectory;
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory!=null)
                pathTo = selectedDirectory.getAbsolutePath();
        });
        VBox vBox = new VBox(10, button, button1); // add spacing of 10 between the buttons
        vBox.setAlignment(Pos.CENTER); // center the buttons
        vBox.setAlignment(Pos.BASELINE_CENTER);
        vBox.setSpacing(15);
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        //studentIPs.addAll("192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5", "192.168.1.6", "192.168.1.7");
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button shareButton = new Button("Collect Files");
        shareButton.getStyleClass().add("button-21");
        shareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            Handler.fileCollect(selectedIPs,pathTo,pathFrom);
        });
        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView, shareButton);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.add(vBox,0,0);
        gridPane.add(shareVbox,0,1);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);
    }
    private void lockScreen(){gridPane.getChildren().clear();
        System.out.println("Power Management...");
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        //studentIPs.addAll("192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5", "192.168.1.6", "192.168.1.7");
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button lockScreenButton = new Button("Lock Screen!");
        lockScreenButton.getStyleClass().add("button-21");
        lockScreenButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            try {
                Handler.freeze(selectedIPs);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }

        });

        Button unlockButton = new Button("Unlock Screen!");
        unlockButton.getStyleClass().add("button-21");
        unlockButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            try {
                Handler.unfreeze(selectedIPs);
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox shareVbox = new VBox(10);  // 10 is the spacing between the children
        shareVbox.getChildren().addAll(studentIPsListView, lockScreenButton,unlockButton);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.getChildren().add(shareVbox);  // Added at column 0, row 0
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);}
    private void openWebsite() {
        gridPane.getChildren().clear();
        System.out.println("Opening website...");

        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(340, 230);
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);

        for (Client client : lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }

        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<String> browserListView = new ListView<>();
        browserListView.setPrefSize(100, 100);
        ObservableList<String> browsers = FXCollections.observableArrayList();
        browserListView.setItems(browsers);
        browsers.addAll("chrome", "Brave", "msedge", "Firefox");
        browserListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button openButton = new Button("Open!");
        openButton.getStyleClass().add("button-21");
        openButton.setOnAction(e -> {
            String selectedBrowser = browserListView.getSelectionModel().getSelectedItems().get(0);
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();

            Label URLLabel = new Label("Enter URL:");
            TextField URLText = new TextField();

            Button sendURLButton = new Button("Send URL");
            sendURLButton.getStyleClass().add("button-21");
            sendURLButton.setOnAction(event -> {
                try {
                    Handler.openApp(selectedIPs, selectedBrowser, URLText.getText());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            VBox vbox = new VBox(10);
            vbox.getChildren().addAll(URLLabel, URLText, sendURLButton);
            vbox.setAlignment(Pos.CENTER);

            gridPane.add(vbox, 0, 1); // Add to column 0, row 1
        });

        VBox shareVbox = new VBox(10);
        shareVbox.getChildren().addAll(studentIPsListView,browserListView, openButton);
        shareVbox.setAlignment(Pos.CENTER);
        shareVbox.setPadding(new Insets(10, 10, 10, 10));

        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(shareVbox, 0, 0); // Add to column 0, row 0
        borderPane.setCenter(gridPane);
    }




    private static ImageView createImageView(String iconName) {
        ImageView imageView = new ImageView(new Image(iconName));
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);
        return imageView;
    }
    private static Button createButtonWithAction(ImageView imageView, Runnable action) {
        Button button = new Button("", imageView);
        button.getStyleClass().add("custom-button");
        button.setOnAction(e -> action.run());
        return button;
    }
    private Background createBackground(Paint fill) {
        return new Background(new BackgroundFill(fill, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY));
    }
    private Border createSolidBorder(Color color, double width) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(5), new javafx.scene.layout.BorderWidths(width)));
    }

    private void shareScreen(ObservableList<String> selectedIPs) {
        System.out.println(selectedIPs);
    }

    private void IPsList(String buttonName) {
        ListView<String> studentIPsListView = new ListView<>();
        studentIPsListView.setPrefSize(150, 100);  // Set the width and height as needed
        ObservableList<String> studentIPs = FXCollections.observableArrayList();
        studentIPsListView.setItems(studentIPs);
        //studentIPs.addAll("192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5", "192.168.1.6", "192.168.1.7");
        for (Client client: lan.getClients().values()) {
            studentIPs.add(client.getIpAddress());
        }
        studentIPsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Button shareButton = new Button(buttonName);
        shareButton.setOnAction(e -> {
            ObservableList<String> selectedIPs = studentIPsListView.getSelectionModel().getSelectedItems();
            shareScreen(selectedIPs);
        });

        HBox hboxx = new HBox(10);  // 10 is the spacing between the children
        hboxx.getChildren().addAll(studentIPsListView, shareButton);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.getChildren().add(hboxx);  // Added at column 0, row 0
        hboxx.setAlignment(Pos.CENTER);
        hboxx.setPadding(new Insets(10, 10, 10, 10));
        borderPane.setCenter(gridPane);
    }
    private static VBox getFileFolder(FileChooser fileChooser, DirectoryChooser directoryChooser) {
        Label label1 = new Label("Select File or Folder:");
        Button button = new Button("Select File");
        Label label = new Label("OR");
        button.getStyleClass().add("button-21");
        Button button1 = new Button("Select Folder");
        button1.getStyleClass().add("button-21");
        button.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile!=null)
                pathFrom = selectedFile.getAbsolutePath();
        });
        button1.setOnAction(e -> {
            File selectedDirectory;
            selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory!=null)
                pathFrom = selectedDirectory.getAbsolutePath();
        });
        VBox vBox = new VBox(10, label1,button,label, button1 ); // add spacing of 10 between the buttons
        vBox.setAlignment(Pos.CENTER); // center the buttons
        return vBox;
    }

    private static VBox createComputerWithDescription(String description, String ipAddress) {
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
    private static boolean pingIPAddress(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            return address.isReachable(100);
        } catch (IOException e) {
            return false;
        }
    }
    private VBox createComputerWithDescription(String description, boolean isReachable) {
        String imageName = isReachable ? "Computers Logo/Computer_ON.png" : "Computers Logo/Computer_OFF.png";
        ImageView imageView = new ImageView(new Image(imageName));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        Label nameLabel = new Label(description);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(imageView, nameLabel);
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }
    private void updateComputersStatus() {
        int numColumns = 7;
        if(lan == null){
            return;
        }
        for (int i = 0; i < lan.getClients().size(); i++) {
            String descriptionName = "Computer" + (i + 1);
            String ipAddress = lan.getClient(i + 1).getIpAddress();
            boolean isReachable = pingIPAddress(ipAddress);
            int finalI = i;
            Platform.runLater(() -> {
                gridPane.getChildren().clear();
                VBox computerWithDescription = createComputerWithDescription(descriptionName, isReachable);
                gridPane.add(computerWithDescription, finalI % numColumns, finalI / numColumns);
            });
        }
    }
}


