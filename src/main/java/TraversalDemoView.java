import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

public class TraversalDemoView extends DocumentationView {

    public TraversalDemoView() {
        super("Scene Graph Traversal", "Find and manipulate elements dynamically, similar to DOM traversal in JS.");

        // lookup() Demo
        VBox lookupBox = new VBox(10);
        Label targetLabel = new Label("I am the target label.");
        targetLabel.setId("target-label");

        Button findBtn = new Button("Find #target-label and change text");
        findBtn.setOnAction(e -> {
            // lookup() searches down the tree from the node it's called on
            Node foundNode = lookupBox.lookup("#target-label");
            if (foundNode instanceof Label) {
                ((Label) foundNode).setText("Found! Text changed via lookup().");
                foundNode.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            }
        });

        lookupBox.getChildren().addAll(targetLabel, findBtn);

        addSection("lookup(\"#id\") - Like querySelector", lookupBox);
        addCodeSnippet(
                "Node node = parent.lookup(\"#target-id\");\nif (node instanceof Label) {\n    ((Label) node).setText(\"Found!\");\n}");

        // getParent() Demo
        VBox parentBox = new VBox(10);
        Button parentBtn = new Button("Click to style my parent VBox");
        parentBtn.setOnAction(e -> {
            Parent parent = parentBtn.getParent();
            if (parent != null) {
                parent.setStyle("-fx-background-color: #ffccbc; -fx-padding: 10;");
            }
        });

        parentBox.getChildren().add(parentBtn);

        addSection("getParent() - Like parentNode", parentBox);
        addCodeSnippet("Parent parent = button.getParent();\nparent.setStyle(\"-fx-background-color: red;\");");

        // getChildren() Demo
        VBox childrenBox = new VBox(10);
        childrenBox.getChildren().addAll(
                new Label("Child 1"),
                new Label("Child 2"),
                new Label("Child 3"));

        Button loopBtn = new Button("Loop through children and number them");
        loopBtn.setOnAction(e -> {
            int i = 1;
            for (Node child : childrenBox.getChildren()) {
                if (child instanceof Label) {
                    ((Label) child).setText("Updated Child " + i++);
                }
            }
        });

        VBox container = new VBox(10, childrenBox, loopBtn);

        addSection("getChildren() - Like children", container);
        addCodeSnippet("for (Node child : vbox.getChildren()) {\n    // Do something with child\n}");
    }
}
