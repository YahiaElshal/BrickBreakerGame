package brickgame;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

/**
 * The Bonus class represents a reward item in the game.
 * It implements the Serializable interface, which enables its instances to be written to streams as objects.
 */
public class Bonus implements Serializable {
    /**
     * The visual representation of the reward item.
     */
    public transient Rectangle reward;

    /**
     * The x-coordinate of the reward item.
     */
    public double x;

    /**
     * The y-coordinate of the reward item.
     */
    public double y;

    /**
     * The time when the reward item was created.
     */
    public long timeCreated;

    /**
     * A flag indicating whether the reward item has been taken.
     */
    public boolean taken = false;

    /**
     * Constructs a new Bonus with the specified row, column, block, and padding.
     *
     * @param row     the row position of the reward in the game grid
     * @param column  the column position of the reward in the game grid
     * @param block   the block associated with the reward
     * @param padding a flag indicating whether padding should be added to the reward's position
     */
    public Bonus(int row, int column, Block block, boolean padding) {
        if (padding) {
            x = (column * (block.getWidth())) + Block.getPaddingH() + ((double) block.getWidth() / 2) - 15;
            y = (row * (block.getHeight())) + block.getPaddingTop() + ((double) block.getHeight() / 2) - 15;
        }
        else{
            x = (column * (block.getWidth())) + ((double) block.getWidth() / 2) - 15;
            y = (row * (block.getHeight())) + ((double) block.getHeight() / 2) - 15;
        }
        draw();
    }

    /**
     * Draws the reward item on the screen.
     */
    private void draw() {
        reward = new Rectangle();
        reward.setWidth(45);
        reward.setHeight(30);
        reward.setX(x);
        reward.setY(y);

        String url = "bonus.png";
        reward.setFill(new ImagePattern(new Image(url)));
    }
}