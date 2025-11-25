import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PositioningDemoView extends DocumentationView {

    public PositioningDemoView() {
        super("Positioning", "Absolute vs Relative Positioning and Anchoring.");

        // Absolute Positioning (Pane)
        Pane absolutePane = new Pane();
        absolutePane.setPrefHeight(150);
        absolutePane.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #9e9e9e;");

        Rectangle rect1 = new Rectangle(50, 50, Color.RED);
        rect1.setLayoutX(10);
        rect1.setLayoutY(10);

        Rectangle rect2 = new Rectangle(50, 50, Color.BLUE);
        rect2.setLayoutX(80); // Manually positioned
        rect2.setLayoutY(40);

        Label absLabel = new Label("Absolute (X=80, Y=40)");
        absLabel.setLayoutX(80);
        absLabel.setLayoutY(95);

        absolutePane.getChildren().addAll(rect1, rect2, absLabel);

        addSection("Absolute Positioning (Pane)", absolutePane);
        addCodeSnippet("Pane pane = new Pane();\nrect.setLayoutX(80);\nrect.setLayoutY(40);");

        // Relative Positioning (StackPane)
        StackPane relativePane = new StackPane();
        relativePane.setPrefHeight(150);
        relativePane.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #ffb74d;");

        Rectangle centerRect = new Rectangle(100, 100, Color.ORANGE);
        Label centerLabel = new Label("Centered (Relative)");
        centerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        Label topLeftLabel = new Label("Top Left");
        StackPane.setAlignment(topLeftLabel, Pos.TOP_LEFT);

        Label bottomRightLabel = new Label("Bottom Right");
        StackPane.setAlignment(bottomRightLabel, Pos.BOTTOM_RIGHT);

        relativePane.getChildren().addAll(centerRect, centerLabel, topLeftLabel, bottomRightLabel);

        addSection("Relative Positioning (StackPane)", relativePane);
        addCodeSnippet("StackPane.setAlignment(node, Pos.TOP_LEFT);\n// Elements stack on top of each other");

        // Anchoring (AnchorPane)
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(150);
        anchorPane.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #64b5f6;");

        Rectangle pinnedRect = new Rectangle(50, 50, Color.GREEN);
        AnchorPane.setTopAnchor(pinnedRect, 10.0);
        AnchorPane.setRightAnchor(pinnedRect, 10.0);

        Label pinnedLabel = new Label("Pinned Top-Right (10px)");
        AnchorPane.setTopAnchor(pinnedLabel, 65.0);
        AnchorPane.setRightAnchor(pinnedLabel, 10.0);

        Rectangle stretchedRect = new Rectangle(50, 50, Color.PURPLE);
        AnchorPane.setBottomAnchor(stretchedRect, 10.0);
        AnchorPane.setLeftAnchor(stretchedRect, 10.0);
        AnchorPane.setRightAnchor(stretchedRect, 100.0); // Stretches width

        Label stretchedLabel = new Label("Pinned Bottom-Left-Right");
        AnchorPane.setBottomAnchor(stretchedLabel, 30.0);
        AnchorPane.setLeftAnchor(stretchedLabel, 20.0);

        anchorPane.getChildren().addAll(pinnedRect, pinnedLabel, stretchedRect, stretchedLabel);

        addSection("Anchoring (AnchorPane)", anchorPane);
        addCodeSnippet(
                "AnchorPane.setTopAnchor(node, 10.0);\nAnchorPane.setRightAnchor(node, 10.0);\n// Resizes with parent if multiple anchors set");
    }
}
