package brickgame;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static brickgame.Block.imageUris;

/**
 * The main class for the Brick Game application.
 * This class extends the JavaFX Application and implements
 * the EventHandler<KeyEvent> and GameEngine.OnAction interfaces.
 */
public class GameApplication extends Application implements EventHandler<KeyEvent>, GameEngine.OnAction {

    private static final Logger LOGGER = Logger.getLogger(GameApplication.class.getName());
    private static final int SCENE_WIDTH = 500;
    static final int SCENE_HEIGHT = 700;
    public static final int PLAYER_WIDTH = 130;
    public static final int PLAYER_HEIGHT = 30;
    private static final int HALF_PLAYER_WIDTH = PLAYER_WIDTH / 2;
    private static final int BALL_RADIUS = 10;
    private static final int LEFT  = 1;
    private static final int RIGHT = 2;
    private static final double MAX_BALL_SPEED = 0.9;
    public static final String FONT = "/Debrosee-ALPnL.ttf";
    private int bossHitCooldown = 0;
    private static final int BOSS_HIT_COOLDOWN_DURATION = 8; // Adjust as needed

    private static final String SAVE_PATH = Config.getSavePath();
    private static final String SAVE_PATH_DIR = Config.getSavePathDir();

    public static final int BOSS_LEVEL = 4;
    private static int level = 4;
    private static double xPlayer = 190.0f;
    private static double centerPlayerX = 255.0f;
    private static double yPlayer = 640.0f;
    private static double xBall;
    private static double yBall;
    private double ballXSpeed = 0.500;
    private double ballYSpeed = 0.500;
    private static int playerLife = 3;
    private int bossLife = 30; // Set the initial Boss life count

    private static int  score    = 0;

    /**
     * Gets the x-coordinate of the player.
     *
     * @return The x-coordinate of the player.
     */
    public static double getxPlayer() {
        return xPlayer;
    }

    /**
     * Gets the y-coordinate of the player.
     *
     * @return The y-coordinate of the player.
     */
    public static double getyPlayer() {
        return yPlayer;
    }

    /**
     * Gets the current game time.
     *
     * @return The current game time.
     */
    public static long getTime() {
        return time;
    }

    static long time     = 0;
    private long goldTime = 0;
    Label scoreLabel;
    Label heartLabel;
    private int destroyedBlockCount = 0;
    private boolean isGoldStatus = false;

    private static boolean canMove = false;
    private boolean goDownBall = true;
    private boolean goRightBall = true;
    private boolean collideToPaddle = false;
    private boolean collideToPaddleAndMoveToRight = true;
    private boolean collideToRightWall = false;
    private boolean collideToLeftWall = false;
    private boolean collideToRightBlock = false;
    private boolean collideToBottomBlock = false;
    private boolean collideToLeftBlock = false;
    private boolean collideToTopBlock = false;
    private boolean loadFromSave = false;

    private Circle ball;
    private Rectangle paddle;
    private static GameEngine engine;
    Stage  primaryStage;
    private final ImageView[] bossLifeHearts = new ImageView[bossLife];
    private final ArrayList<Bonus> bonuses = new ArrayList<>();
    private static final Random random = new Random();

    private static Pane root;
    private Scene scene;

    /**
     * Entry point for launching the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Gets the root pane of the application.
     *
     * @return The root pane.
     */
    public static Pane getRoot() {
        return root;
    }

    /**
     * Sets the root pane of the application.
     *
     * @param root The new root pane.
     */
    public static void setRoot(Pane root) {
        GameApplication.root = root;
    }

    /**
     * Initializes the game when the application starts.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        setRoot(new Pane());
        scene = new Scene(getRoot(), SCENE_WIDTH, SCENE_HEIGHT);
        Block.initBricks();
        initMenu(primaryStage);
    }

    /**
     * Initializes the main menu for the Brick Game application.
     *
     * @param primaryStage The primary stage for the application.
     */
    private void initMenu(Stage primaryStage) {
        // Create a Pane for the menu layout
        Pane menuPane = new Pane();
        GameView gameView = new GameView(getRoot());

        // Set a space-themed background image for the menu scene
        Image backgroundImage = new Image("menu_bg.jpg");
        menuPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));
        // Add a Label for the game title
        Label title = new Label("Brick Game");
        title.setTranslateX(170);
        title.setTranslateY(80);
        title.setFont(Font.loadFont(Objects.requireNonNull(getClass().getResource(FONT)).toExternalForm(), 32));
        title.setTextFill(Color.web("#0076a3")); // Use a color that matches the space theme
        menuPane.getChildren().add(title);

        // Add a TextArea for the rules and instructions
        TextArea instructions = new TextArea(Config.getInstructions());
        instructions.setTranslateX(50);
        instructions.setTranslateY(130);
        instructions.setPrefWidth(400);
        instructions.setPrefHeight(350);
        instructions.setEditable(false);
        title.setFont(Font.loadFont(Objects.requireNonNull(getClass().getResource(FONT)).toExternalForm(), 32));
        instructions.setStyle("-fx-text-fill: #0076a3;"); // Use a color that matches the space theme
        menuPane.getChildren().add(instructions);

        Button startButton = gameView.createButton("Start Game", 140, 500, menuPane);
        Button loadButton = gameView.createButton("Load Game", 260, 500, menuPane);

        Font customFont = Font.loadFont(getClass().getResourceAsStream(FONT), 12);
        instructions.setFont(customFont);
        // Create a Scene for the menu
        Scene menuScene = new Scene(menuPane, SCENE_WIDTH, SCENE_HEIGHT);

        // Set an action for the start button
        startButton.setOnAction(event -> {
        try {
            // Create a FadeTransition to fade out the current scene
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), primaryStage.getScene().getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            // Set an onFinished event handler for the fade out transition
            fadeOut.setOnFinished(e -> {
                // Initialize the game and switch to the game scene
                initGame(primaryStage);
                primaryStage.setScene(scene);

                // Create a FadeTransition to fade in the new scene
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), scene.getRoot());
                fadeIn.setFromValue(0);
                fadeIn.setToValue(1);

                // Start the fade in transition
                fadeIn.play();
            });

                // Start the fade out transition
                fadeOut.play();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An exception was thrown in startGame", e);
            }
        });
        loadButton.setOnAction(event -> {
            try {
                // Create a FadeTransition to fade out the current scene
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), primaryStage.getScene().getRoot());
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                // Set an onFinished event handler for the fade out transition
                fadeOut.setOnFinished(e -> {
                    // Initialize the game and switch to the game scene
                    loadGame();
                    initGame(primaryStage);
                    primaryStage.setScene(scene);

                    // Create a FadeTransition to fade in the new scene
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), scene.getRoot());
                    fadeIn.setFromValue(0);
                    fadeIn.setToValue(1);

                    // Start the fade in transition
                    fadeIn.play();
                });

                // Start the fade out transition
                fadeOut.play();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An exception was thrown in startGame", e);
            }
        });

        // Set the menu scene as the initial scene
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    /**
     * Initializes the game, either starting a new game or loading a saved game.
     *
     * @param primaryStage The primary stage for the application.
     */
    private void initGame(Stage primaryStage) {
        if (!loadFromSave) {
            initializeNewGame();
        }

        setupScene(primaryStage);

        if (!loadFromSave) {
            startGameEngine();
        } else {
            engine = new GameEngine();
            engine.setOnAction(this);
            engine.setFps(120);
            engine.start();
            loadFromSave = false;
        }
    }

    /**
     * Initializes a new game, setting up the initial state.
     * Displays a message if the player levels up.
     */
    private void initializeNewGame() {
        if (level > 0) {
            new DisplayScores().showMessage("Level Up :)");
        }

        initBall();
        initPaddle();

        if (level < 2) {
            Block.initBoard(level);
        } else if (level < BOSS_LEVEL) {
            Block.initHardBoard(level);
        } else {
            Block.initBoardBoss();
        }

        setRoot(new Pane());
        GameView gameView = new GameView(getRoot());
        addGameComponentsToView(gameView);
    }

    /**
     * Adds various game components to the view, including labels, blocks, paddle, and ball.
     * Also initializes the boss life bar if the current level is the boss level.
     *
     * @param gameView The GameView instance to add components to.
     */
    private void addGameComponentsToView(GameView gameView) {
        scoreLabel = new Label("Score: " + score);
        Label levelLabel = new Label("Level: " + (level+1));
        levelLabel.setTranslateY(20);
        heartLabel = new Label("Heart : " + playerLife);
        heartLabel.setTranslateX(SCENE_WIDTH - 70.0);

        if (paddle != null && ball != null) {
            gameView.addScoreLabelToView(scoreLabel);
            gameView.addHeartLabelToView(heartLabel);
            gameView.addLevelLabelToView(levelLabel);
        } else {
            LOGGER.severe("One or more nodes are null in start method of GameApplication.java");
        }

        for (Block block : Block.blocksArrayList) {
            gameView.addBlockToView(block);
        }

        gameView.addPaddleToView(paddle);
        gameView.addBallToView(ball);

        if (level == BOSS_LEVEL) {
            initializeBossLifeBar(gameView);
        }

        scene.setRoot(getRoot());
        scene.getStylesheets().add("style.css");
        scene.setOnKeyPressed(this);
    }

    /**
     * Initializes the boss life bar, adding heart images to represent the boss's life.
     *
     * @param gameView The GameView instance to add the boss life bar to.
     */
    private void initializeBossLifeBar(GameView gameView) {
        gameView.addLifeBarBackground();
        for (int i = 0; i < bossLife; i++) {
            ImageView heart = new ImageView(new Image("boss_heart.png"));
            heart.setFitWidth(5);
            heart.setFitHeight(5);
            heart.setTranslateX(((double) i * 7) + 150);
            heart.setTranslateY(15);
            bossLifeHearts[i] = heart;
            gameView.addBossLifeBarToView(heart);
        }
    }

    /**
     * Sets up the game scene by setting the root, stylesheets, and key event handler.
     *
     * @param primaryStage The primary stage for the application.
     */
    private void setupScene(Stage primaryStage) {
        scene.setRoot(getRoot());
        scene.getStylesheets().add("style.css");
        scene.setOnKeyPressed(this);

        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts the game engine, initializing it with event handling and frame rate.
     */
    private void startGameEngine() {
        engine = new GameEngine();
        engine.setOnAction(this);
        engine.setFps(120);
        engine.start();
    }

    /**
     * Handles key events for player input, such as moving the paddle and saving the game.
     *
     * @param event The KeyEvent instance representing the key event.
     */
    @Override
    public void handle(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:
                move(LEFT);
                break;
            case RIGHT:
                move(RIGHT);
                break;
            case UP:
                canMove = true;
                break;
            case DOWN:
                break;
            case S:
                saveGame();
                break;
            default:
                LOGGER.info("Wrong Input, Please only use allowed keybindings");
        }
    }

    /**
     * Moves the player paddle in the specified direction using a separate thread for smooth animation.
     *
     * @param direction The direction of movement (LEFT or RIGHT).
     */
    private void move(final int direction) {
        new Thread(() -> {
            int sleepTime = 4;
            for (int i = 0; i < 30; i++) {
                if (xPlayer == (SCENE_WIDTH - PLAYER_WIDTH) && direction == RIGHT) {
                    return;
                }
                if (xPlayer == 0 && direction == LEFT) {
                    return;
                }
                if (direction == RIGHT) {
                    xPlayer++;
                } else {
                    xPlayer--;
                }
                centerPlayerX = xPlayer + HALF_PLAYER_WIDTH;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (i >= 20) {
                    sleepTime = i;
                }
            }
        }).start();
    }

    /**
     * Initializes the ball by setting its initial position and appearance.
     */
    private void initBall() {
        xBall = centerPlayerX;
        yBall = yPlayer + 5;
        ball = new Circle();
        ball.setRadius(BALL_RADIUS);
        ball.setFill(new ImagePattern(new Image("ball.png")));
    }

    /**
     * Initializes the paddle by setting its initial position and appearance.
     */
    private void initPaddle() {
        paddle = new Rectangle();
        paddle.setWidth(PLAYER_WIDTH);
        paddle.setHeight(PLAYER_HEIGHT);
        paddle.setX(xPlayer);
        paddle.setY(yPlayer);
        ImagePattern pattern = new ImagePattern(new Image("paddle.png"));
        paddle.setFill(pattern);
    }


    /**
     * Resets collision flags for various game elements to their default states.
     */
    private void resetCollideFlags() {
        collideToPaddle = false;
        collideToPaddleAndMoveToRight = false;
        collideToRightWall = false;
        collideToLeftWall = false;

        collideToRightBlock = false;
        collideToBottomBlock = false;
        collideToLeftBlock = false;
        collideToTopBlock = false;
    }


    /**
     * Resets the ball and player paddle to their initial positions.
     */
    private static void resetBall() {
        canMove = false;
        xBall = centerPlayerX;
        yBall = yPlayer + 3;
        xPlayer = 190.0f;
        centerPlayerX = 255.0f;
        yPlayer = 640.0f;
    }

    /**
     * Resets the position of the ball to the initial position on the player paddle.
     */
    private void resetBallPosition() {
        xBall = centerPlayerX;
        yBall = yPlayer + 3;
    }

    /**
     * Applies physics to the ball, updating its position based on speed and direction.
     * Handles collisions with walls, blocks, and the player paddle.
     */
    private void setPhysicsToBall() {
        yBall += goDownBall ? ballYSpeed : -ballYSpeed;
        xBall += goRightBall ? ballXSpeed : -ballXSpeed;

        if(!canMove && !isGoldStatus){
            resetBallPosition();
        }

        if (goRightBall) {
            xBall += ballXSpeed;
        } else {
            xBall -= ballXSpeed;
        }
        if (goDownBall) {
            yBall += ballYSpeed;
        } else {
            yBall -= ballYSpeed;
        }

        if (yBall <= 0) {
            resetCollideFlags();
            goDownBall = true;
            return;
        }
        if (yBall >= SCENE_HEIGHT-20) {
            resetCollideFlags();
            goDownBall = false;
            if (!isGoldStatus) {
                loseheart();
            }
            return;
        }

        balltoPaddleCollision();

        if (xBall >= SCENE_WIDTH) {
            resetCollideFlags();
            collideToRightWall = true;
        }

        if (xBall <= 0) {
            resetCollideFlags();
            collideToLeftWall = true;
        }

        if (collideToPaddle) {
            goRightBall = collideToPaddleAndMoveToRight;
        }

        //Wall Collide

        if (collideToRightWall) {
            goRightBall = false;
        }

        if (collideToLeftWall) {
            goRightBall = true;
        }

        //Block Collide

        if (collideToRightBlock) {
            goRightBall = true;
        }

        if (collideToLeftBlock) {
            goRightBall = false;
        }

        if (collideToTopBlock) {
            goDownBall = false;
        }

        if (collideToBottomBlock) {
            goDownBall = true;
        }
    }

    /**
     * Handles the collision between the ball and the player paddle.
     * Adjusts the ball's speed and direction based on the collision.
     */
    private void balltoPaddleCollision() {
        if (yBall >= yPlayer - BALL_RADIUS && (xBall >= xPlayer && xBall <= xPlayer + PLAYER_WIDTH)) {
            resetCollideFlags();
            collideToPaddle = true;
            goDownBall = false;
             double relation = (xBall - centerPlayerX) / ((double) PLAYER_WIDTH / 2);

             if (Math.abs(relation) <= 0.3) {
                 ballXSpeed = Math.abs(relation);
             } else if (Math.abs(relation) > 0.3 && Math.abs(relation) <= 0.7) {
                 ballXSpeed = (Math.abs(relation) * 1.2) + (level / 4.00);
                 ballYSpeed = (Math.abs(relation) * 1.2) + (level / 4.00);
             } else {
                 ballXSpeed = (Math.abs(relation) * 1.5) + (level / 4.00);
                 ballYSpeed = (Math.abs(relation) * 1.5) + (level / 4.00);
             }
            if(ballXSpeed > MAX_BALL_SPEED){
                 ballXSpeed = MAX_BALL_SPEED;
             }
            if(ballYSpeed > MAX_BALL_SPEED){ // Ensure ballYSpeed doesn't exceed MAX_BALL_SPEED
                ballYSpeed = MAX_BALL_SPEED;
            }
            collideToPaddleAndMoveToRight = xBall - centerPlayerX > 0;
        }
    }

    /**
     * Handles the loss of a player life.
     * Resets the ball and decrements the player life count.
     * Shows a message if the player runs out of lives and stops the game engine.
     */
    static void loseheart() {
        resetBall();
        playerLife--;
        new DisplayScores().show((double) SCENE_WIDTH / 2, (double) SCENE_HEIGHT / 2, -1);

        if (playerLife == 0) {
            GameApplication gameApplication = new GameApplication();
            new DisplayScores().showGameOver(gameApplication);
            engine.stop();
        }
    }

    /**
     * Checks the count of destroyed blocks and the boss hit cooldown to determine if all blocks are destroyed.
     * If all blocks are destroyed and the boss hit cooldown is over, advances to the next level.
     */
    private void checkDestroyedCount() {
        if (bossHitCooldown == 0 && (destroyedBlockCount + Block.indestructible_Blocks >= Block.blocksArrayList.size())) {
                // If all blocks are destroyed, advance to the next level
                nextLevel();
            bossHitCooldown = BOSS_HIT_COOLDOWN_DURATION;

        }
    }

    /**
     * Decreases the boss's life when the boss hit cooldown is over.
     * Updates the visual representation of the boss's life bar.
     */
    private void decreaseBossLife() {
        if (bossHitCooldown == 0) {
            bossLife--;
            GameView gameView = new GameView(getRoot());
            gameView.removeBossLifeBar(bossLifeHearts[bossLife]);
            bossHitCooldown = BOSS_HIT_COOLDOWN_DURATION;
            // Update the visual representation of the boss's life bar
        }
    }

    /**
     * Checks if the boss block is eliminated, and if so, displays a win message, stops the game engine, and updates the background.
     */
    private void bossEliminated() {
        if (!Block.blocksArrayList.isEmpty() && Block.blocksArrayList.get(0) != null && (level == BOSS_LEVEL && Block.blocksArrayList.get(0).isDestroyed)) {
            Image backgroundImage = new Image("winbg.png");
            root.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));
                new DisplayScores().showWin();
                engine.stop();
        }
    }

    /**
     * Saves the current game state to a file in a separate thread.
     * Displays a message indicating that the game is saved.
     */
    private void saveGame() {
        new Thread(() -> {
            try {
                new File(SAVE_PATH_DIR).mkdirs();
                try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(SAVE_PATH))) {
                    writeGameData(outputStream);
                    new DisplayScores().showMessage("Game Saved");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "An IO exception was thrown in saveGame", e);
            }
        }).start();
    }

    /**
     * Loads a saved game state from a file using the LoadSave class.
     * Updates the game state and displays a message indicating that the game is loaded.
     */
    private void loadGame() {
        LoadSave loadSave = new LoadSave();
        try {
            loadSave.read();
            updateGameState(loadSave);
            new DisplayScores().showMessage("Game Loaded");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown in loadGame", e);
        }
    }

    /**
     * Writes the game data to an ObjectOutputStream for saving.
     *
     * @param outputStream The ObjectOutputStream to write the data to.
     * @throws IOException If an I/O error occurs during writing.
     */
    private void writeGameData(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(level);
        outputStream.writeInt(score);
        outputStream.writeInt(playerLife);
        outputStream.writeInt(destroyedBlockCount);

        outputStream.writeDouble(xBall);
        outputStream.writeDouble(yBall);
        outputStream.writeDouble(xPlayer);
        outputStream.writeDouble(yPlayer);
        outputStream.writeDouble(centerPlayerX);
        outputStream.writeLong(time);
        outputStream.writeLong(goldTime);
        outputStream.writeDouble(ballXSpeed);

        outputStream.writeBoolean(Block.isExistHeartBlock);
        outputStream.writeBoolean(isGoldStatus);
        outputStream.writeBoolean(goDownBall);
        outputStream.writeBoolean(goRightBall);
        outputStream.writeBoolean(collideToPaddle);
        outputStream.writeBoolean(collideToPaddleAndMoveToRight);
        outputStream.writeBoolean(collideToRightWall);
        outputStream.writeBoolean(collideToLeftWall);
        outputStream.writeBoolean(collideToRightBlock);
        outputStream.writeBoolean(collideToBottomBlock);
        outputStream.writeBoolean(collideToLeftBlock);
        outputStream.writeBoolean(collideToTopBlock);

        ArrayList<BlockSerializable> blockSerializable = new ArrayList<>();
        for (Block block : Block.blocksArrayList) {
            if (!block.isDestroyed) {
                blockSerializable.add(new BlockSerializable(block.row, block.column, block.type));
            }
        }
        outputStream.writeObject(blockSerializable);
    }

    /**
     * Updates the game state based on the data loaded from a saved game.
     *
     * @param loadSave The LoadSave object containing the loaded game data.
     */
    private void updateGameState(LoadSave loadSave) {
        Block.isExistHeartBlock = loadSave.isExistHeartBlock;
        isGoldStatus = loadSave.isGoldStatus;
        goDownBall = loadSave.goDownBall;
        goRightBall = loadSave.goRightBall;
        collideToPaddle = loadSave.collideToPaddle;
        collideToPaddleAndMoveToRight = loadSave.collideToPaddleAndMoveToRight;
        collideToRightWall = loadSave.collideToRightWall;
        collideToLeftWall = loadSave.collideToLeftWall;
        collideToRightBlock = loadSave.collideToRightBlock;
        collideToBottomBlock = loadSave.collideToBottomBlock;
        collideToLeftBlock = loadSave.collideToLeftBlock;
        collideToTopBlock = loadSave.collideToTopBlock;
        level = loadSave.level;
        score = loadSave.score;
        playerLife = loadSave.heart;
        destroyedBlockCount = loadSave.destroyedBlockCount;
        xBall = loadSave.xBall;
        yBall = loadSave.yBall;
        xPlayer = loadSave.xPlayer;
        yPlayer = loadSave.yPlayer;
        centerPlayerX = loadSave.centerPlayerX;
        time = loadSave.time;
        goldTime = loadSave.goldTime;
        ballXSpeed = loadSave.vX;

        Block.blocksArrayList.clear();
        bonuses.clear();
        Bullet.bulletsArrayList.clear();

        for (BlockSerializable ser : loadSave.blocks) {
            int r = random.nextInt(0,8);
            Block.blocksArrayList.add(new Block(ser.row, ser.column, imageUris.get(r), ser.type, true));
        }

        try {
            loadFromSave = true;
            start(primaryStage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown in loadGame", e);
        }
    }

    /**
     * Advances to the next game level, resetting various game parameters.
     * Clears the blocks and bonuses for the new level.
     */
    private void nextLevel() {
        Platform.runLater(() -> {
            try {
                level++;
                ballXSpeed = 0.500;
                ballYSpeed = 0.500;

                engine.stop();
                resetCollideFlags();
                resetBall();
                goDownBall = true;

                isGoldStatus = false;
                Block.isExistHeartBlock = false;
                Block.indestructible_Blocks = 0;
                xPlayer = 190.0f;
                centerPlayerX = 205.0f;

                time = 0;
                goldTime = 0;

                engine.stop();
                Block.blocksArrayList.clear();
                bonuses.clear();
                Bullet.bulletsArrayList.clear();
                destroyedBlockCount = 0;
                initGame(primaryStage);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An exception was thrown in nextLevel", e);
            }
        });
    }

    /**
     * Restarts the game, resetting all parameters to their initial values.
     *
     * @param primaryStage The primary stage for the game.
     */
    public void restartGame(Stage primaryStage) {
        try {
            level = 0;
            playerLife = 3;
            score = 0;
            ballXSpeed = 0.500;
            ballYSpeed = 0.500;
            destroyedBlockCount = 0;
            resetCollideFlags();
            goDownBall = true;


            isGoldStatus = false;
            Block.isExistHeartBlock = false;
            Block.indestructible_Blocks = 0;
            time = 0;
            goldTime = 0;

            Block.blocksArrayList.clear();
            bonuses.clear();
            Bullet.bulletsArrayList.clear();

            start(primaryStage);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown in restartGame", e);
        }
    }

    /**
     * Handles updates in the game loop. Updates boss hit cooldown, displays score and heart labels,
     * checks block collisions, and updates positions for paddle, ball, and bonuses.
     */
    @Override
    public void onUpdate() {
        // Inside the server update loop
        bossHitCooldown = Math.max(0, bossHitCooldown - 1);
        Platform.runLater(() -> {
            scoreLabel.setText("Score: " + score);
            heartLabel.setText("Heart : " + playerLife);
            checkBlockCollisions();
            updatePaddleAndBallPosition();
            updateBonusesPosition();
            Bullet.updateBulletPosition();
            updateGoldStatus();
        });


    }

    /**
     * Updates the positions of the player paddle and the ball in the game view.
     */
    private void updatePaddleAndBallPosition() {
            paddle.setX(xPlayer);
            paddle.setY(yPlayer);
            ball.setCenterX(xBall);
            ball.setCenterY(yBall);
    }

    /**
     * Checks collisions between the ball and the blocks in the game.
     * Handles block hits and updates the score.
     */
    private void checkBlockCollisions() {
        boolean isHit;
        for (Block block : Block.blocksArrayList) {
            //null check and check if block is destroyed
            if (block != null && Block.blocksArrayList.contains(block) && !block.isDestroyed) {
                isHit = checkCollision(ball, block.brick);
                if (isHit) {
                    handleBlockHit(block);
                }
            }
        }
    }

    /**
     * Handles the collision and hit events for a specific block.
     * Updates the score, marks the block as destroyed, and handles special block types.
     *
     * @param block The block involved in the collision.
     */
    private void handleBlockHit(Block block) {
        if (block.type == Block.BOSS && bossLife != 0) {
            decreaseBossLife();
            new DisplayScores().show(block.x, block.y, bossLife);
        } else if (block.type != Block.BLOCK_INDESTRUCTIBLE) {
            updateScoreAndHideBlock(block, 1);
            block.isDestroyed = true;
            destroyedBlockCount++;
            //todo assign to missile paddle collision
/*          Explode explosion = new Explode();
            explosion.setLayoutX(block.x);
            explosion.setLayoutY(block.y);
            Platform.runLater(() -> root.getChildren().add(explosion));*/
        }
        handleSpecialBlockTypes(block);
        getCollisionSide2(ball, block.brick);
    }

    /**
     * Handles special block types (e.g., alien, star, heart) and triggers corresponding actions.
     *
     * @param block The block with a special type.
     */
    private void handleSpecialBlockTypes(Block block) {
        if (block.type == Block.BLOCK_ALIEN) {
            createBonus(block);
        }

        if (block.type == Block.BLOCK_STAR) {
            activateGoldStatus();
        }

        if (block.type == Block.BLOCK_HEART) {
            playerLife++;
        }
    }

    /**
     * Creates a bonus based on the given block. The bonus type depends on the game level.
     * Adds the bonus to the view, and records its creation time.
     *
     * @param block The block associated with the bonus.
     */
    private void createBonus(Block block) {
        Bonus bonus = new Bonus(block.row, block.column, block,false);
        if (level <= 2) {
         bonus = new Bonus(block.row, block.column, block,true);
        }
        bonus.timeCreated = time;
        if (bonus.reward != null) {
            GameView gameView = new GameView(getRoot());
            gameView.addBonusToView(bonus);
            bonuses.add(bonus);
        } else {
            LOGGER.severe("reward.reward is null");
        }
    }

    /**
     * Activates the gold status, updating game parameters such as gold time, ball appearance, and root style.
     * Sets the isGoldStatus flag to true.
     */
    private void activateGoldStatus() {
        goldTime = time;
        ball.setFill(new ImagePattern(new Image("goldball.png")));
        getRoot().getStyleClass().add("goldRoot");
        isGoldStatus = true;
    }

    /**
     * Updates the game score based on the given reward value.
     * Displays the reward at the block's position and hides the block in the view.
     *
     * @param block  The block associated with the reward.
     * @param reward The reward value to be added to the score.
     */
    private void updateScoreAndHideBlock(Block block, int reward) {
        score += reward;
        new DisplayScores().show(block.x, block.y, reward);
        try {
            if (block.brick != null) {
               Platform.runLater(()-> block.brick.setVisible(false));
            } else {
                LOGGER.severe("block.brick is null");
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.severe("Caught IndexOutOfBoundsException by Yahia: " + e.getMessage());
        }
    }

    /**
     * Empty method to be overridden. Invoked during the initialization phase of the game.
     */
    @Override
    public void onInit() {
        // empty
    }

    /**
     * Determines the collision side between a circle and a rectangle.
     * Updates collision flags based on the collision side.
     *
     * @param c The circle involved in the collision.
     * @param r The rectangle involved in the collision.
     */
    public void getCollisionSide2(Circle c, Rectangle r) {
        double dx = c.getCenterX() - Math.max(r.getX(), Math.min(c.getCenterX(), r.getX() + r.getWidth()));
        double dy = c.getCenterY() - Math.max(r.getY(), Math.min(c.getCenterY(), r.getY() + r.getHeight()));

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) {
                collideToRightBlock = true;
                collideToLeftBlock = false;
            } else {
                collideToLeftBlock = true;
                collideToRightBlock = false;
            }
        } else {
            if (dy > 0) {
                collideToBottomBlock = true;
                collideToTopBlock = false;
            } else {
                collideToTopBlock = true;
                collideToBottomBlock = false;
            }
        }
    }

    /**
     * Checks for collision between a circle and a rectangle.
     *
     * @param c The circle involved in the collision.
     * @param r The rectangle involved in the collision.
     * @return True if there is a collision, false otherwise.
     */
    public boolean checkCollision(Circle c, Rectangle r) {
        double dx = c.getCenterX() - Math.max(r.getX(), Math.min(c.getCenterX(), r.getX() + r.getWidth()));
        double dy = c.getCenterY() - Math.max(r.getY(), Math.min(c.getCenterY(), r.getY() + r.getHeight()));
        return (dx * dx + dy * dy) <= (c.getRadius() * c.getRadius());
    }

    /**
     * Updates physics-related aspects of the game.
     * Invokes methods to update bonuses position, check destroyed count, check if the boss is eliminated, and set ball physics.
     */
    @Override
    public void onPhysicsUpdate() {
        Platform.runLater(this::updateBonusesPosition);
        checkDestroyedCount();
        bossEliminated();
        setPhysicsToBall();
    }

    /**
     * Updates the gold status, including reverting to the normal ball appearance and removing gold status styling after a certain duration.
     */
    private void updateGoldStatus() {
            if (time - goldTime > 5000) {
                ball.setFill(new ImagePattern(new Image("ball.png")));
                if (getRoot() != null) {
                    getRoot().getStyleClass().remove("goldRoot");
                }
                isGoldStatus = false;
            }
    }

    /**
     * Updates the position of bonuses in the game.
     * Checks for collisions with the player paddle, handles taken bonuses, and updates their positions.
     */
    private void updateBonusesPosition() {
            for (Bonus bonus : bonuses) {
                if (bonus.y > SCENE_HEIGHT || bonus.taken) {
                    continue;
                }
                if (bonus.y >= yPlayer && bonus.y <= yPlayer + PLAYER_HEIGHT && bonus.x >= xPlayer && bonus.x <= xPlayer + PLAYER_WIDTH) {
                    bonus.taken = true;
                    bonus.reward.setVisible(false);
                    score += 10;
                    new DisplayScores().show(bonus.x, bonus.y, 10);
                }
                // Update the reward's y property to move it downwards
                bonus.y += ((time - bonus.timeCreated) / 1000.000) + 1.000; // Adjust the speed as needed
                bonus.reward.setY(bonus.y);
            }
    }

    /**
     * Updates the game time. Used to synchronize time across the game components.
     *
     * @param time The current time value.
     */
    @Override
    public void onTime(long time) {
        GameApplication.time = time;
    }
}