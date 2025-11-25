import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DocumentationView extends VBox {

    public DocumentationView(String titleText, String descriptionText) {
        getStyleClass().add("doc-view");
        setSpacing(20);

        Label title = new Label(titleText);
        title.getStyleClass().add("doc-title");

        Label description = new Label(descriptionText);
        description.getStyleClass().add("doc-description");
        description.setWrapText(true);

        getChildren().addAll(title, description);
    }

    public void addSection(String header, Node content) {
        Label sectionHeader = new Label(header);
        sectionHeader.getStyleClass().add("doc-section-header");

        VBox sectionBox = new VBox(10, sectionHeader, content);
        sectionBox.getStyleClass().add("doc-section");

        getChildren().add(sectionBox);
    }

    public void addCodeSnippet(String code) {
        Label codeLabel = new Label(code);
        codeLabel.getStyleClass().add("code-snippet");
        getChildren().add(codeLabel);
    }
}
