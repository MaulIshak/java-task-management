import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LayoutDemoView extends DocumentationView {

    public LayoutDemoView() {
        super("Layouts", "JavaFX uses layout panes to manage component positioning, similar to Flexbox in CSS.");

        // HBox Demo
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10));
        hbox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #999;");
        hbox.getChildren().addAll(new Button("Item 1"), new Button("Item 2"), new Button("Item 3"));

        addSection("HBox (Row Layout)", hbox);
        addCodeSnippet("HBox hbox = new HBox(10); // 10px spacing\nhbox.getChildren().addAll(btn1, btn2, btn3);");

        // VBox Demo
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #999;");
        vbox.getChildren().addAll(new Button("Item 1"), new Button("Item 2"), new Button("Item 3"));

        addSection("VBox (Column Layout)", vbox);
        addCodeSnippet("VBox vbox = new VBox(10); // 10px spacing\nvbox.getChildren().addAll(btn1, btn2, btn3);");

        // FlowPane Demo
        FlowPane flow = new FlowPane();
        flow.setHgap(10);
        flow.setVgap(10);
        flow.setPadding(new Insets(10));
        flow.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #999;");
        flow.setPrefWrapLength(200); // Force wrap for demo
        for (int i = 1; i <= 6; i++) {
            flow.getChildren().add(new Button("Item " + i));
        }

        addSection("FlowPane (Wrap Layout)", flow);
        addCodeSnippet(
                "FlowPane flow = new FlowPane();\nflow.setHgap(10);\nflow.setVgap(10);\nflow.getChildren().add(btn);");

        // Percentage Width Demo (GridPane)
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10;");

        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints();
        col1.setPercentWidth(50);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints();
        col2.setPercentWidth(50);

        grid.getColumnConstraints().addAll(col1, col2);

        Button btnLeft = new Button("Left (50%)");
        btnLeft.setMaxWidth(Double.MAX_VALUE);
        Button btnRight = new Button("Right (50%)");
        btnRight.setMaxWidth(Double.MAX_VALUE);

        grid.add(btnLeft, 0, 0);
        grid.add(btnRight, 1, 0);

        addSection("Percentage Width (GridPane)", grid);
        addCodeSnippet(
                "ColumnConstraints col1 = new ColumnConstraints();\ncol1.setPercentWidth(50);\ngrid.getColumnConstraints().addAll(col1, col2);");

        // Percentage Width Demo (Binding)
        VBox bindingBox = new VBox();
        bindingBox.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 10;");
        Button responsiveBtn = new Button("I am always 80% of my parent");
        // Bind width to 80% of the container's width
        responsiveBtn.prefWidthProperty().bind(bindingBox.widthProperty().multiply(0.8));

        bindingBox.getChildren().add(responsiveBtn);

        addSection("Percentage Width (Binding)", bindingBox);
        addCodeSnippet("button.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));");
    }
}
