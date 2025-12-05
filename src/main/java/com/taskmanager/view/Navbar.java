package com.taskmanager.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Navbar extends HBox {

    private final Label titleLabel;

    public Navbar() {
        getStyleClass().add("navbar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(20);

        titleLabel = new Label("Dashboard");
        titleLabel.getStyleClass().add("navbar-title");
        getChildren().add(titleLabel);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}
