module brickGame {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.logging;

    opens brickGame to javafx.fxml;
    exports brickGame;
}