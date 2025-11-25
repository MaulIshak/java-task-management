import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("JavaFX Documentation App");

        MainLayout mainLayout = new MainLayout();
        Scene scene = new Scene(mainLayout, 1024, 768);

        // Load CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}