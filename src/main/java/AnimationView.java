import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationView extends DocumentationView {

    public AnimationView() {
        super("Animations", "JavaFX provides powerful transition animations.");

        // Fade Transition
        VBox fadeBox = new VBox(10);
        Button fadeBtn = new Button("Fade Me");
        fadeBtn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), fadeBtn);
            ft.setFromValue(1.0);
            ft.setToValue(0.1);
            ft.setCycleCount(2);
            ft.setAutoReverse(true);
            ft.play();
        });
        fadeBox.getChildren().add(fadeBtn);

        addSection("FadeTransition", fadeBox);
        addCodeSnippet(
                "FadeTransition ft = new FadeTransition(Duration.millis(1000), node);\nft.setFromValue(1.0);\nft.setToValue(0.0);\nft.play();");

        // Translate Transition
        VBox moveBox = new VBox(10);
        Button moveBtn = new Button("Move Me");
        moveBtn.setOnAction(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(1000), moveBtn);
            tt.setByX(100);
            tt.setCycleCount(2);
            tt.setAutoReverse(true);
            tt.play();
        });
        moveBox.getChildren().add(moveBtn);

        addSection("TranslateTransition", moveBox);
        addCodeSnippet(
                "TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);\ntt.setByX(100);\ntt.play();");

        // Rotate Transition
        VBox rotateBox = new VBox(10);
        Rectangle rect = new Rectangle(50, 50, Color.DODGERBLUE);
        Button rotateBtn = new Button("Rotate Rectangle");
        rotateBtn.setOnAction(e -> {
            RotateTransition rt = new RotateTransition(Duration.millis(2000), rect);
            rt.setByAngle(360);
            rt.play();
        });
        rotateBox.getChildren().addAll(rect, rotateBtn);

        addSection("RotateTransition", rotateBox);
        addCodeSnippet(
                "RotateTransition rt = new RotateTransition(Duration.millis(2000), node);\nrt.setByAngle(360);\nrt.play();");
    }
}
