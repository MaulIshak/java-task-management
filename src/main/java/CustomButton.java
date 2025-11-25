import javafx.scene.control.Button;

public class CustomButton extends Button {

    public CustomButton(String text) {
        super(text);
        getStyleClass().add("custom-button");
    }
}
