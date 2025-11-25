import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

public class MainLayout extends BorderPane {

    private final ScrollPane contentArea;

    public MainLayout() {
        getStyleClass().add("main-layout");

        // Sidebar
        Sidebar sidebar = new Sidebar(this);
        setLeft(sidebar);

        // Content Area
        contentArea = new ScrollPane();
        contentArea.setFitToWidth(true);
        contentArea.getStyleClass().add("content-area");
        setCenter(contentArea);

        // Initial View
        switchView("Intro");
    }

    public void switchView(String viewName) {
        Node view;
        switch (viewName) {
            case "Layouts":
                view = new LayoutDemoView();
                break;
            case "Positioning":
                view = new PositioningDemoView();
                break;
            case "DragDrop":
                view = new DragDropDemoView();
                break;
            case "CssSelectors":
                view = new CssDemoView();
                break;
            case "Traversal":
                view = new TraversalDemoView();
                break;
            case "AdvancedStyling":
                view = new AdvancedStylingView();
                break;
            case "DataControls":
                view = new DataControlsView();
                break;
            case "Animations":
                view = new AnimationView();
                break;
            case "Components":
                view = new ComponentDemoView();
                break;
            case "Scrolling":
                view = new DocumentationView("Scrolling", "Use ScrollPane to handle overflowing content.");
                // Add more scrolling demo content here later
                break;
            case "Intro":
            default:
                view = new DocumentationView("Introduction",
                        "Welcome to the JavaFX Documentation App.\nSelect a topic from the sidebar.");
                break;
        }
        contentArea.setContent(view);
    }
}
