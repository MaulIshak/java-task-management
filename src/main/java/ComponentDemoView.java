import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ComponentDemoView extends DocumentationView {

    public ComponentDemoView() {
        super("Components", "Common UI controls and how to style them.");

        // Buttons
        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        Button btn = new Button("Standard Button");
        CustomButton customBtn = new CustomButton("Custom Styled Button");
        buttons.getChildren().addAll(btn, customBtn);

        addSection("Buttons", buttons);
        addCodeSnippet("Button btn = new Button(\"Click Me\");\nbtn.getStyleClass().add(\"my-style\");");

        // Inputs
        VBox inputs = new VBox(10);
        inputs.setPadding(new Insets(10));
        TextField tf = new TextField();
        tf.setPromptText("Enter text here...");
        CheckBox cb = new CheckBox("Check me!");
        inputs.getChildren().addAll(new Label("Text Field:"), tf, cb);

        addSection("Inputs", inputs);
        addCodeSnippet("TextField tf = new TextField();\ntf.setPromptText(\"Prompt...\");");
    }
}
