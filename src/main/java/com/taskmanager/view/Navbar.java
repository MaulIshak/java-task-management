package com.taskmanager.view;

import com.taskmanager.Main;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Navbar extends HBox {

    private MainLayout mainLayout;

    public Navbar(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
        getStyleClass().add("navbar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);

        addNavButton("Logout", "Login");
    }

    private void addNavButton(String label, String viewName) {
        Button btn = new Button(label);
        btn.getStyleClass().add("navbar-button");
        btn.setOnAction(e -> mainLayout.switchView(viewName));
        getChildren().add(btn);
    }

}
