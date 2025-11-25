import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DragDropDemoView extends DocumentationView {

    public DragDropDemoView() {
        super("Drag & Drop", "Transfer data between components using Drag & Drop events.");

        HBox container = new HBox(50);

        // DRAG SOURCE
        VBox sourceBox = new VBox(10);
        sourceBox.setStyle("-fx-border-color: #9e9e9e; -fx-padding: 20; -fx-background-color: #f5f5f5;");
        Label sourceTitle = new Label("Drag Source");
        sourceTitle.setFont(Font.font("System", 16));

        Label draggableText = new Label("Drag Me!");
        draggableText.setStyle(
                "-fx-background-color: #2196f3; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 5; -fx-cursor: hand;");

        // 1. Drag Detected: Start the drag
        draggableText.setOnDragDetected(event -> {
            Dragboard db = draggableText.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(draggableText.getText());
            db.setContent(content);
            event.consume();
        });

        // Optional: Visual feedback when dragging done
        draggableText.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                draggableText.setText("Done!");
            }
            event.consume();
        });

        sourceBox.getChildren().addAll(sourceTitle, draggableText);

        // DROP TARGET
        VBox targetBox = new VBox(10);
        targetBox.setStyle("-fx-border-color: #4caf50; -fx-padding: 20; -fx-background-color: #e8f5e9; -fx-min-width: 150; -fx-min-height: 100;");
        Label targetTitle = new Label("Drop Target");
        targetTitle.setFont(Font.font("System", 16));

        Label dropLabel = new Label("Drop here...");

        // 2. Drag Over: Accept the drag if it has string data
        targetBox.setOnDragOver(event -> {
            if (event.getGestureSource() != targetBox && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        // 3. Drag Dropped: Handle the data transfer
        targetBox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                dropLabel.setText("Dropped: " + db.getString());
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // Optional: Visual feedback during drag enter/exit
        targetBox.setOnDragEntered(e -> targetBox.setStyle(
                "-fx-border-color: #2e7d32; -fx-border-width: 2; -fx-padding: 20; -fx-background-color: #c8e6c9; -fx-min-width: 150; -fx-min-height: 100;"));
        targetBox.setOnDragExited(e -> targetBox.setStyle(
                "-fx-border-color: #4caf50; -fx-padding: 20; -fx-background-color: #e8f5e9; -fx-min-width: 150; -fx-min-height: 100;"));

        targetBox.getChildren().addAll(targetTitle, dropLabel);

        container.getChildren().addAll(sourceBox, targetBox);

        addSection("Basic Drag & Drop", container);
        addCodeSnippet(
                "// Source\nnode.setOnDragDetected(e -> {\n    Dragboard db = node.startDragAndDrop(TransferMode.ANY);\n    ClipboardContent content = new ClipboardContent();\n    content.putString(\"Data\");\n    db.setContent(content);\n});\n\n// Target\ntarget.setOnDragOver(e -> e.acceptTransferModes(TransferMode.COPY_OR_MOVE));\ntarget.setOnDragDropped(e -> {\n    Dragboard db = e.getDragboard();\n    if (db.hasString()) handleDrop(db.getString());\n});");
    }
}
