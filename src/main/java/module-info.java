module brickGame {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.logging;

    opens brickgame to javafx.fxml;
    exports brickgame;
}