package brickgame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code Explode} class represents an animated explosion effect. It extends {@code Pane} to provide
 * a container for the explosion animation.
 */
public class Explode extends Pane {

    /**
     * The logger for logging messages related to the explosion.
     */
    private final Logger LOGGER = Logger.getLogger(Explode.class.getName());

    /**
     * The number of times the explosion animation should loop.
     */
    private static final int NUM_LOOPS = 3; // Adjust the number of loops as needed

    /**
     * Creates a new instance of the {@code Explode} class. Initializes the explosion animation and sets up
     * the sprite sheet.
     */
    public Explode() {
        // Load the sprite sheet for the explosion animation
        Image spriteSheet = new Image("explosion2.png");
        double frameWidth = 32;
        double frameHeight = 32;

        // Create an ImageView for displaying the explosion animation
        ImageView spriteView = new ImageView(spriteSheet);
        spriteView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        // Set up the animation parameters
        Duration duration = Duration.millis(1500);
        int numFrames = 16;
        KeyFrame[] keyFrames = new KeyFrame[numFrames * NUM_LOOPS]; // Calculate total key frames for desired loops

        // Create individual key frames for each frame of the explosion animation and for each loop
        for (int loop = 0; loop < NUM_LOOPS; loop++) {
            for (int i = 0; i < numFrames; i++) {
                final int frameIndex = i;
                KeyFrame keyFrame = new KeyFrame(
                        duration.multiply((double)loop * numFrames + i).divide((double)numFrames * NUM_LOOPS),
                        e -> spriteView.setViewport(new Rectangle2D(
                                frameIndex * frameWidth,
                                0,
                                frameWidth,
                                frameHeight
                        ))
                );
                keyFrames[loop * numFrames + i] = keyFrame;
            }
        }

        // Create a Timeline for the sprite animation and set it to play a specific number of loops
        Timeline spriteAnimation = new Timeline(keyFrames);
        spriteAnimation.setCycleCount(NUM_LOOPS);

        // Log a message indicating that an explosion has occurred
        LOGGER.log(Level.SEVERE, "EXPLODE");

        // Add the spriteView to the Explosion pane
        getChildren().add(spriteView);

        // Start the sprite animation
        spriteAnimation.play();
    }
}

