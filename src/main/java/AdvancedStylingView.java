import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AdvancedStylingView extends DocumentationView {

    public AdvancedStylingView() {
        super("Advanced Styling", "Gradients, Effects, and CSS Variables.");

        // Gradients
        HBox gradientBox = new HBox(20);

        Label linearLabel = new Label("Linear Gradient");
        linearLabel.getStyleClass().add("linear-gradient-demo");

        Label radialLabel = new Label("Radial Gradient");
        radialLabel.getStyleClass().add("radial-gradient-demo");

        gradientBox.getChildren().addAll(linearLabel, radialLabel);

        addSection("Gradients", gradientBox);
        addCodeSnippet(
                "-fx-background-color: linear-gradient(to right, #ff7e5f, #feb47b);\n-fx-background-color: radial-gradient(center 50% 50%, radius 50%, #2196f3, #0d47a1);");

        // Effects
        VBox effectBox = new VBox();
        Label shadowLabel = new Label("I have a Drop Shadow");
        shadowLabel.getStyleClass().add("shadow-demo");
        effectBox.getChildren().add(shadowLabel);

        addSection("Effects (DropShadow)", effectBox);
        addCodeSnippet("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 5, 5);");

        // CSS Variables
        VBox variableBox = new VBox();
        Label varLabel = new Label("I use a CSS Variable (-fx-base-color)");
        varLabel.getStyleClass().add("variable-demo");
        variableBox.getChildren().add(varLabel);

        addSection("CSS Variables (Looked-up Colors)", variableBox);
        addCodeSnippet(".root { -fx-base-color: #6200ea; }\n.my-class { -fx-background-color: -fx-base-color; }");
    }
}
