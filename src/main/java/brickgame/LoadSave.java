package brickgame;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * The {@code LoadSave} class is responsible for reading saved game data from a file.
 * It provides methods to populate game variables and objects based on the saved data.
 */
public class LoadSave {

    /**
     * Indicates whether a heart block exists in the saved game.
     */
    public boolean isExistHeartBlock;

    /**
     * Indicates whether the gold status is active in the saved game.
     */
    public boolean isGoldStatus;

    /**
     * Flags for the direction of the ball's movement in the saved game.
     */
    public boolean goDownBall;
    public boolean goRightBall;

    /**
     * Flags for collision status in the saved game.
     */
    public boolean collideToPaddle;
    public boolean collideToPaddleAndMoveToRight;
    public boolean collideToRightWall;
    public boolean collideToLeftWall;
    public boolean collideToRightBlock;
    public boolean collideToBottomBlock;
    public boolean collideToLeftBlock;
    public boolean collideToTopBlock;

    /**
     * The level of the game in the saved state.
     */
    public int level;

    /**
     * The score in the saved game.
     */
    public int score;

    /**
     * The remaining lives (heart count) in the saved game.
     */
    public int heart;

    /**
     * The count of destroyed blocks in the saved game.
     */
    public int destroyedBlockCount;

    /**
     * The X and Y coordinates of the ball in the saved game.
     */
    public double xBall;
    public double yBall;

    /**
     * The X and Y coordinates of the player paddle in the saved game.
     */
    public double xPlayer;
    public double yPlayer;

    /**
     * The center X coordinate of the player paddle in the saved game.
     */
    public double centerPlayerX;

    /**
     * The elapsed time and gold time in the saved game.
     */
    public long time;
    public long goldTime;

    /**
     * The velocity of the ball in the saved game.
     */
    public double vX;

    /**
     * The list of serialized block objects in the saved game.
     */
    public ArrayList<BlockSerializable> blocks = new ArrayList<>();

    /**
     * Reads saved game data from the specified file and populates the class fields.
     */
    public void read() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Config.getSavePath()))) {
            // Read primitive data types
            level = inputStream.readInt();
            score = inputStream.readInt();
            heart = inputStream.readInt();
            destroyedBlockCount = inputStream.readInt();
            xBall = inputStream.readDouble();
            yBall = inputStream.readDouble();
            xPlayer = inputStream.readDouble();
            yPlayer = inputStream.readDouble();
            centerPlayerX = inputStream.readDouble();
            time = inputStream.readLong();
            goldTime = inputStream.readLong();
            vX = inputStream.readDouble();
            isExistHeartBlock = inputStream.readBoolean();
            isGoldStatus = inputStream.readBoolean();
            goDownBall = inputStream.readBoolean();
            goRightBall = inputStream.readBoolean();
            collideToPaddle = inputStream.readBoolean();
            collideToPaddleAndMoveToRight = inputStream.readBoolean();
            collideToRightWall = inputStream.readBoolean();
            collideToLeftWall = inputStream.readBoolean();
            collideToRightBlock = inputStream.readBoolean();
            collideToBottomBlock = inputStream.readBoolean();
            collideToLeftBlock = inputStream.readBoolean();
            collideToTopBlock = inputStream.readBoolean();

            read(inputStream);

        } catch (IOException e) {
            Thread.currentThread().interrupt();
        }
    }
    /**
     * Reads the list of serialized block objects from the provided ObjectInputStream.
     * This method is part of the read process in the LoadSave class.
     *
     * @param inputStream The ObjectInputStream used to read the serialized block objects.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void read(ObjectInputStream inputStream) throws IOException {
        try {
            blocks = (ArrayList<BlockSerializable>) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            // In case the class for the serialized object is not found
            Thread.currentThread().interrupt();
        }
    }
}

