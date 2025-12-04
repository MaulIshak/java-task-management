package com.taskmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class Navbar extends HBox {

    private final Label titleLabel;

    public Navbar() {
        getStyleClass().add("navbar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(20);

        // Title (Dynamic)
        titleLabel = new Label("Dashboard");
        titleLabel.getStyleClass().add("navbar-title");
        getChildren().add(titleLabel);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
