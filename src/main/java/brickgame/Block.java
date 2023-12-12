package brickgame;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * The {@code GameBlock} class represents a block in a brick game. Blocks can have different types
 * such as normal, alien, star, heart, indestructible_Blocks, and boss. It also manages the appearance
 * and behavior of each block.
 */
public class Block implements Serializable {

    public int row;
    public int column;

    public boolean isDestroyed = false;
    public static boolean isExistHeartBlock = false;

    public  int type;
    public  int x;
    public  int y;

    private int width = 100;
    private static int height = 30;
    private static final int PADDING_TOP = height * 2;
    private static final int PADDING_H = 50;
    public transient Rectangle brick;
    static Random random = new Random();

    // Block types
    public static final int BLOCK_NORMAL = 99;
    public static final int BLOCK_ALIEN = 100;
    public static final int BLOCK_STAR = 101;
    public static final int BLOCK_HEART = 102;
    public static final int BLOCK_INDESTRUCTIBLE = 103;
    public static final int BOSS = 104;
    public static int indestructible_Blocks = 0;

    static ArrayList<String> imageUris = new ArrayList<>();
    static final ArrayList<Block> blocksArrayList = new ArrayList<>();

    /**
     * Initializes the list of normal block images.
     */
    static void initBricks() {
        imageUris.add(Config.getSilver());
        imageUris.add(Config.getRed());
        imageUris.add(Config.getBlue());
        imageUris.add(Config.getGreen());
        imageUris.add(Config.getPurple());
        imageUris.add(Config.getOrange());
        imageUris.add(Config.getYellow());
        imageUris.add(Config.getLight_Blue());
        imageUris.add(Config.getLight_Green());
    }

    /**
     * Creates a block with the specified parameters.
     *
     * @param row    The row index of the block.
     * @param column The column index of the block.
     * @param color  The color or image file name of the block.
     * @param type   The type of the block (e.g., normal, alien, star).
     * @param padding Determines if padding should be applied to the block's position.
     */
    private static Block block = new Block(-1, -1, "YELLOW_BRICK.PNG", 99, true);

    public Block(int row, int column, String color, int type, boolean padding) {
        this.row = row;
        this.column = column;
        this.type = type;

        if(type == BOSS){
            drawboss();
        } else {
            draw(color, padding);
        }
    }

    /**
     * Draws a block with the specified color or image.
     *
     * @param color   The color or image file name of the block.
     * @param padding Determines if padding should be applied to the block's position.
     */
    private void draw(String color, boolean padding) {
        width = 100;
        height = 30;
        if (padding) {
            x = (column * width) + PADDING_H;
            y = (row * height) + PADDING_TOP;
        } else {
            x = (column * width);
            y = (row * height);
        }
        brick = new Rectangle();
        brick.setWidth(width);
        brick.setHeight(height);
        brick.setX(x);
        brick.setY(y);

        if (type == BLOCK_ALIEN) {
            Image image = new Image("bonusblock.png");
            ImagePattern pattern = new ImagePattern(image);
            brick.setFill(pattern);
        } else if (type == BLOCK_HEART) {
            Image image = new Image("heart.png");
            ImagePattern pattern = new ImagePattern(image);
            brick.setFill(pattern);
            isExistHeartBlock = true;
        } else if (type == BLOCK_STAR) {
            Image image = new Image("star.png");
            ImagePattern pattern = new ImagePattern(image);
            brick.setFill(pattern);
        } else {
            Image image = new Image(color);
            ImagePattern pattern = new ImagePattern(image);
            brick.setFill(pattern);
        }
    }

    /**
     * Draws a boss block with a special pattern.
     */
    public void drawboss() {
        x = 100;
        y = 50;
        width = 350;
        height = 90;

        brick = new Rectangle();
        brick.setWidth(width);
        brick.setHeight(height);
        brick.setX(x);
        brick.setY(y);
        Image image = new Image("boss.png");
        ImagePattern pattern = new ImagePattern(image);
        brick.setFill(pattern);
        Bullet.startBulletTimer(block);
    }

    /**
     * Gets the top padding of the block.
     *
     * @return The top padding.
     */
    public int getPaddingTop() {
        return PADDING_TOP;
    }

    /**
     * Gets the horizontal padding of the block.
     *
     * @return The horizontal padding.
     */
    public static int getPaddingH() {
        return PADDING_H;
    }

    /**
     * Gets the height of the block.
     *
     * @return The height of the block.
     */
    public int getHeight() {
        return Block.height;
    }

    /**
     * Gets the width of the block.
     *
     * @return The width of the block.
     */
    public int getWidth() {
        return block.width;
    }

    /**
     * Determines the type of the block based on a random number.
     *
     * @param r The random number.
     * @return The type of the block.
     */
    private static int determineBlockType(int r) {
        if (r % 10 == 1) {
            return Block.BLOCK_ALIEN;
        } else if (r % 10 == 2) {
            if (!isExistHeartBlock) {
                isExistHeartBlock = true;
                return Block.BLOCK_HEART;
            }
        } else if (r % 10 == 3) {
            return Block.BLOCK_STAR;
        }
        return Block.BLOCK_NORMAL;
    }

    /**
     * Initializes the game board with blocks for a specific level.
     *
     * @param level The level of the game.
     */
    static void initBoard(int level) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < level + 3; j++) {
                int r = random.nextInt(0, 8);
                int type = determineBlockType(r);
                blocksArrayList.add(new Block(j, i, imageUris.get(r), type, true));
            }
        }
    }

    /**
     * Initializes the game board with a boss block.
     */
    static void initBoardBoss() {
        blocksArrayList.add(new Block(2, 1, "boss.png", BOSS, true));
        for (int i = 0; i < 5; i++) {
            for (int j = 6; j < 8; j++) {
                int r = random.nextInt(0, 8);
                int type = determineBlockType(r);
                blocksArrayList.add(new Block(j, i, imageUris.get(r), type, false));
            }
        }
    }

    /**
     * Initializes a hard game board for a specific level.
     *
     * @param level The level of the game.
     */
    static void initHardBoard(int level) {
        // Add a row of indestructible_Blocks blocks at the bottom
        for (int j = 0; j <= 4; j++) {
            if ((level < 3 && j == 2) || (level >= 3 && (j == 1 || j == 3))) {
                continue;
            }

            blocksArrayList.add(new Block(level * 5, j, "indestructible.png", Block.BLOCK_INDESTRUCTIBLE, false));
            indestructible_Blocks++;
        }
        for (int i = 3; i < 9; i++) {
            for (int j = 0; j < 5; j++) {
                if (j == 2) {
                    continue;
                }
                int r = random.nextInt(0, 8);
                int type = determineBlockType(r);
                blocksArrayList.add(new Block(i, j, imageUris.get(r), type, false));
            }
        }
    }
}

