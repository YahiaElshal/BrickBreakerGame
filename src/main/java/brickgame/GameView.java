package brickgame;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.logging.Logger;

public class GameView {
    private final Pane root;
    private static final Logger LOGGER = Logger.getLogger(GameView.class.getName());

    public GameView(Pane root) {
        this.root = root;
    }

    public void addScoreLabelToView(Label scoreLabel) {
        if (scoreLabel != null) {
            Platform.runLater(() -> root.getChildren().add(scoreLabel));
        } else {
            LOGGER.severe("scoreLabel is null in addScoreLabelToView method of GameView.java");
        }
    }

    public void addHeartLabelToView(Label heartLabel) {
        if (heartLabel != null) {
            Platform.runLater(() -> root.getChildren().add(heartLabel));
        } else {
            LOGGER.severe("heartLabel is null in addHeartLabelToView method of GameView.java");
        }
    }

    public void addLevelLabelToView(Label levelLabel) {
        if (levelLabel != null) {
            Platform.runLater(() -> root.getChildren().add(levelLabel));
        } else {
            LOGGER.severe("levelLabel is null in addLevelLabelToView method of GameView.java");
        }
    }

    public void addBlockToView(Block block) {
        if (block.brick != null) {
            Platform.runLater(() -> root.getChildren().add(block.brick));
        } else {
            LOGGER.severe("block.brick is null in addBlockToView method of GameView.java");
        }
    }

    public void addPaddleToView(Rectangle paddle) {
        if (paddle != null) {
            Platform.runLater(() -> root.getChildren().add(paddle));
        } else {
            LOGGER.severe("paddle is null in addPaddleToView method of GameView.java");
        }
    }

    public void addBallToView(Circle ball) {
        if (ball != null) {
            Platform.runLater(() -> root.getChildren().add(ball));
        } else {
            LOGGER.severe("ball is null in addBallToView method of GameView.java");
        }
    }

    public void addBonusToView(Bonus bonus) {
        if (bonus.reward != null) {
            Platform.runLater(() -> root.getChildren().add(bonus.reward));
        } else {
            LOGGER.severe("reward.reward is null in addBonusToView method of GameView.java");
        }
    }

    public void addBulletToView(Bullet bullet) {
        if (bullet.missile != null) {
            Platform.runLater(() -> root.getChildren().add(bullet.missile));
        } else {
            LOGGER.severe("missile.shot is null in addBulletToView method of GameView.java");
        }
    }

    public void addBossLifeBarToView(ImageView heart) {
            if (heart != null) {
                Platform.runLater(() -> root.getChildren().add(heart));
            } else {
                LOGGER.severe("heart is null in addBossLifeBarToView method of GameView.java");
            }
    }

    void addLifeBarBackground() {
        Rectangle background = new Rectangle();
        background.setWidth((double)5 * (42+3));
        background.setHeight((double)5 * 4);
        background.setTranslateX(140);
        background.setTranslateY(7);
        background.setFill(Color.BLACK); // Set the fill color
        background.setArcWidth(10); // Set the corner radius for smooth edges
        background.setArcHeight(10); // Set the corner radius for smooth edges
        background.setStroke(Color.DARKBLUE); // Set the border color
        background.setStrokeWidth(2);

        // Add the background to the GameView
        Platform.runLater(() -> root.getChildren().add(background));
    }

    public void removeBossLifeBar(ImageView heart) {
        if (heart != null) {
            Platform.runLater(() -> root.getChildren().remove(heart));
        } else {
            LOGGER.severe("heart is null in removeBossLifeBar method of GameView.java");
        }
    }
    Button createButton(String text, double x, double y, Pane menuPane) {
        Button button = new Button(text);
        button.setTranslateX(x);
        button.setTranslateY(y);

        Font customFont = Font.loadFont(getClass().getResourceAsStream("/Debrosee-ALPnL.ttf"), 12);
        button.setFont(customFont);
        button.setTextFill(Color.web("#0076a3"));

        // Adding animation to the button
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.5);
            button.setScaleY(1.5);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });

        Platform.runLater(() -> menuPane.getChildren().add(button));
        return button;
    }
}
