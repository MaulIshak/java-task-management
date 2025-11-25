import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CssDemoView extends DocumentationView {

    public CssDemoView() {
        super("CSS Selectors", "JavaFX supports various CSS selectors for styling components.");

        // ID Selector Demo
        VBox idBox = new VBox();
        Label idLabel = new Label("I am styled using an ID selector (#unique-id-demo)");
        idLabel.setId("unique-id-demo");
        idBox.getChildren().add(idLabel);

        addSection("ID Selector", idBox);
        addCodeSnippet(
                "#unique-id-demo {\n    -fx-background-color: #ffeb3b;\n    /* ... */\n}\n\nlabel.setId(\"unique-id-demo\");");

        // Descendant Selector Demo
        VBox parentBox = new VBox();
        parentBox.getStyleClass().add("parent-demo");

        Label childLabel = new Label("I am a child inside .parent-demo");
        childLabel.getStyleClass().add("child-demo");

        VBox nestedBox = new VBox();
        Label nestedChildLabel = new Label("I am a nested child inside .parent-demo");
        nestedChildLabel.getStyleClass().add("child-demo");
        nestedBox.getChildren().add(nestedChildLabel);

        parentBox.getChildren().addAll(childLabel, nestedBox);

        addSection("Descendant Selector (.parent .child)", parentBox);
        addCodeSnippet(".parent-demo .child-demo {\n    -fx-text-fill: #1565c0;\n}");

        // Direct Child Selector Demo
        VBox directParentBox = new VBox();
        directParentBox.getStyleClass().add("parent-demo");

        Label directChild = new Label("I am a direct child (styled)");
        directChild.getStyleClass().add("direct-child");

        VBox intermediateBox = new VBox();
        Label indirectChild = new Label("I am NOT a direct child (not styled)");
        indirectChild.getStyleClass().add("direct-child");
        intermediateBox.getChildren().add(indirectChild);

        directParentBox.getChildren().addAll(directChild, intermediateBox);

        addSection("Direct Child Selector (.parent > .child)", directParentBox);
        addCodeSnippet(".parent-demo > .direct-child {\n    -fx-background-color: #bbdefb;\n}");

        // Pseudo-class Demo
        VBox pseudoBox = new VBox();
        Label hoverLabel = new Label("Hover over me!");
        hoverLabel.getStyleClass().add("pseudo-demo");
        pseudoBox.getChildren().add(hoverLabel);

        addSection("Pseudo-classes (:hover)", pseudoBox);
        addCodeSnippet(".pseudo-demo:hover {\n    -fx-background-color: #b2dfdb;\n}");
    }
}
