package brickgame;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.logging.Logger;
/**
 * The Score class is responsible for displaying score updates, messages, and game status (win/lose) on the screen.
 */
public class Score {
    // Logger for logging messages
    private static final Logger LOGGER = Logger.getLogger(Score.class.getName());

    /**
     * Displays a score update at the specified coordinates.
     *
     * @param x     The x-coordinate for the score display.
     * @param y     The y-coordinate for the score display.
     * @param score The score to be displayed.
     */
    public void show(final double x, final double y, int score) {
        String sign = (score >= 0) ? "+" : "";
        final Label label = createLabel(sign + score, x, y);

        Platform.runLater(() -> {
            if (Main.getRoot() != null) {
                Main.getRoot().getChildren().add(label);
            } else {
                LOGGER.severe("Failed to add label to root in show method of Score.java. Either main.root is null.");
            }
        });

        animateLabel(label);
    }

    /**
     * Displays a message at a fixed location on the screen.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(String message) {
        final Label label = createLabel(message, 220, 340);

        Platform.runLater(() -> {
            if (Main.getRoot() != null) {
                Main.getRoot().getChildren().add(label);
            } else {
                LOGGER.severe("Failed to add label to root in showMessage method of Score.java. main.root is null.");
            }
        });

        animateLabel(label);
    }

    /**
     * Displays a "Game Over" message and a restart button on the screen.
     *
     * @param main The main game object.
     */
    public void showGameOver(final Main main) {
        Platform.runLater(() -> {
            Label label = createLabel("Game Over :(", 200, 250);
            label.setScaleX(2);
            label.setScaleY(2);

            Button restart = createRestartButton(main);

            Main.getRoot().getChildren().addAll(label, restart);
        });
    }

    /**
     * Displays a "You Win" message on the screen.
     */
    public void showWin() {
        Platform.runLater(() -> {
            Label label = createLabel("You Win :)", 200, 250);
            label.setScaleX(2);
            label.setScaleY(2);

            Main.getRoot().getChildren().addAll(label);
        });
    }

    /**
     * Creates a new label with the specified text and coordinates.
     *
     * @param text The text for the label.
     * @param x    The x-coordinate for the label.
     * @param y    The y-coordinate for the label.
     * @return The created label.
     */
    private Label createLabel(String text, double x, double y) {
        Label label = new Label(text);
        label.setTranslateX(x);
        label.setTranslateY(y);
        return label;
    }

    /**
     * Animates a label by gradually increasing its scale and decreasing its opacity.
     *
     * @param label The label to be animated.
     */
    private void animateLabel(Label label) {
        new Thread(() -> {
            for (int i = 0; i < 21; i++) {
                final int scale = i;
                try {
                    Platform.runLater(() -> {
                        label.setScaleX(scale);
                        label.setScaleY(scale);
                        label.setOpacity((20 - scale) / 20.0);
                    });
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    /**
     * Creates a new restart button.
     *
     * @param main The main game object.
     * @return The created restart button.
     */
    private Button createRestartButton(Main main) {
        Button restart = new Button("Restart");
        restart.setTranslateX(220);
        restart.setTranslateY(300);
        restart.setOnAction(event -> main.restartGame());
        return restart;
    }
}