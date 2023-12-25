package com.LabManagementAppUI.FrontEnd;

import javafx.scene.control.Button;

public class UIHelper {
    private UIHelper() {}

    public static Button createNormalButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button-21");

        return button;
    }
}
