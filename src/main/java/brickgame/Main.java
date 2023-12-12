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
import static java.lang.System.out;

public class Main extends Application implements EventHandler<KeyEvent>, GameEngine.OnAction {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
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
    private double centerPlayerX = 255.0f;
    private static double yPlayer = 640.0f;
    private double xBall;
    private double yBall;
    private double ballXSpeed = 0.500;
    private double ballYSpeed = 0.500;
    private int playerLife = 3;
    private int bossLife = 30; // Set the initial Boss life count

    public static void setScore(int add) {
        score = score+add;
    }

    private static int  score    = 0;

    public static double getxPlayer() {
        return xPlayer;
    }
    public static double getyPlayer() {
        return yPlayer;
    }
    public static long getTime() {
        return time;
    }
    static long time     = 0;
    private long goldTime = 0;
    Label scoreLabel;
    Label heartLabel;
    private int destroyedBlockCount = 0;
    private boolean isGoldStatus = false;

    private boolean canMove = false;
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
    private GameEngine engine;
    private Stage  primaryStage;
    private final ImageView[] bossLifeHearts = new ImageView[bossLife];
    private final ArrayList<Bonus> bonuses = new ArrayList<>();
    private static final Random random = new Random();

    private static Pane root;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public static Pane getRoot() {
        return root;
    }

    public static void setRoot(Pane root) {
        Main.root = root;
    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        setRoot(new Pane());
        scene = new Scene(getRoot(), SCENE_WIDTH, SCENE_HEIGHT);
        Block.initBricks();
        initMenu(primaryStage);
    }

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
        title.setTranslateY(200);
        title.setFont(Font.loadFont(Objects.requireNonNull(getClass().getResource(FONT)).toExternalForm(), 32));
        title.setTextFill(Color.web("#0076a3")); // Use a color that matches the space theme
        menuPane.getChildren().add(title);

        // Add a TextArea for the rules and instructions
        TextArea instructions = new TextArea("Here are the rules and instructions...");
        instructions.setTranslateX(100);
        instructions.setTranslateY(250);
        instructions.setPrefWidth(300);
        instructions.setPrefHeight(200);
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

    private void initializeNewGame() {
        if (level > 0) {
            new Score().showMessage("Level Up :)");
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
            LOGGER.severe("One or more nodes are null in start method of Main.java");
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

    private void initializeBossLifeBar(GameView gameView) {
        gameView.addLifeBarBackground();
        for (int i = 0; i < bossLife; i++) {
            ImageView heart = new ImageView(new Image("boss_heart.png"));
            heart.setFitWidth(5);
            heart.setFitHeight(5);
            heart.setTranslateX(((double) i * 7) + 150);
            heart.setTranslateY(15);
            bossLifeHearts[i] = heart;
            LOGGER.info("before add boss life bar");
            gameView.addBossLifeBarToView(heart);
            LOGGER.info("Boss life bar initialised");
        }
    }

    private void setupScene(Stage primaryStage) {
        scene.setRoot(getRoot());
        scene.getStylesheets().add("style.css");
        scene.setOnKeyPressed(this);

        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startGameEngine() {
        engine = new GameEngine();
        engine.setOnAction(this);
        engine.setFps(120);
        engine.start();
    }

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


    private void initBall() {
        xBall = centerPlayerX;
        yBall = yPlayer + 5;
        ball = new Circle();
        ball.setRadius(BALL_RADIUS);
        ball.setFill(new ImagePattern(new Image("ball.png")));
    }

    private void initPaddle() {
        paddle = new Rectangle();
        paddle.setWidth(PLAYER_WIDTH);
        paddle.setHeight(PLAYER_HEIGHT);
        paddle.setX(xPlayer);
        paddle.setY(yPlayer);
        ImagePattern pattern = new ImagePattern(new Image("paddle.png"));
        paddle.setFill(pattern);
    }
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

    private void resetBall() {
        canMove = false;
        xBall = centerPlayerX;
        yBall = yPlayer + 3;
        xPlayer = 190.0f;
        centerPlayerX = 255.0f;
        yPlayer = 640.0f;
    }

    private void resetBallPosition() {
        xBall = centerPlayerX;
        yBall = yPlayer + 3;
    }

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
    private void balltoPaddleCollision() {
        if (yBall >= yPlayer - BALL_RADIUS && (xBall >= xPlayer && xBall <= xPlayer + PLAYER_WIDTH)) {
            resetCollideFlags();
            collideToPaddle = true;
            goDownBall = false;
             double relation = (xBall - centerPlayerX) / ((double) PLAYER_WIDTH / 2);

             if (Math.abs(relation) <= 0.3) {
                 ballXSpeed = Math.abs(relation);
                 //ballYSpeed = Math.abs(relation);
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

    private void loseheart() {
        resetBall();
        playerLife--;
        new Score().show((double) SCENE_WIDTH / 2, (double) SCENE_HEIGHT / 2, -1);

        if (playerLife == 0) {
            new Score().showGameOver(this);
            engine.stop();
        }
    }

    private void checkDestroyedCount() {
/*        out.println("Size of blocksArrayList: " + Block.blocksArrayList.size());
        out.println("Value of destroyedBlockCount: " + destroyedBlockCount);*/
        // Check if all blocks are destroyed or if the blocksArrayList is empty
        //todo fix for the hard levels
        if (bossHitCooldown == 0) {
            if (destroyedBlockCount + Block.indestructible_Blocks >= Block.blocksArrayList.size()) {
                // If all blocks are destroyed, advance to the next level
                nextLevel();
            bossHitCooldown = BOSS_HIT_COOLDOWN_DURATION;
            }
            out.println("destroyed blocks are: " + destroyedBlockCount + " and indestructible blocks are: " + Block.indestructible_Blocks + " and blocksArrayList size is: " + Block.blocksArrayList.size());
        }
    }

    private void decreaseBossLife() {
        if (bossHitCooldown == 0) {
            bossLife--;

            out.println("Boss life: " + bossLife);
            GameView gameView = new GameView(getRoot());
            gameView.removeBossLifeBar(bossLifeHearts[bossLife]);
            bossHitCooldown = BOSS_HIT_COOLDOWN_DURATION;
            // Update the visual representation of the boss's life bar
        }
    }
    private void bossEliminated() {
        if (!Block.blocksArrayList.isEmpty() && Block.blocksArrayList.get(0) != null && (level == BOSS_LEVEL && Block.blocksArrayList.get(0).isDestroyed)) {
            out.println("You Win");
            Image backgroundImage = new Image("winbg.png");
            root.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true))));
                new Score().showWin();
                engine.stop();
        }
    }
    private void saveGame() {
        new Thread(() -> {
            try {
                new File(SAVE_PATH_DIR).mkdirs();
                try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(SAVE_PATH))) {
                    writeGameData(outputStream);
                    new Score().showMessage("Game Saved");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "An IO exception was thrown in saveGame", e);
            }
        }).start();
    }

    private void loadGame() {
        LoadSave loadSave = new LoadSave();
        try {
            loadSave.read();
            updateGameState(loadSave);
            new Score().showMessage("Game Loaded");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception was thrown in loadGame", e);
        }
    }

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

    public void restartGame() {
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

    private void updatePaddleAndBallPosition() {
            paddle.setX(xPlayer);
            paddle.setY(yPlayer);
            ball.setCenterX(xBall);
            ball.setCenterY(yBall);
    }

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
    private void handleBlockHit(Block block) {
        if (block.type == Block.BOSS && bossLife != 0) {
            decreaseBossLife();
            new Score().show(block.x, block.y, bossLife);
        } else if (block.type != Block.BLOCK_INDESTRUCTIBLE) {
            updateScoreAndHideBlock(block, 1);
            block.isDestroyed = true;
            destroyedBlockCount++;
/*          Explode explosion = new Explode();
            explosion.setLayoutX(block.x);
            explosion.setLayoutY(block.y);
            Platform.runLater(() -> root.getChildren().add(explosion));*/
        }
        handleSpecialBlockTypes(block);
        getCollisionSide2(ball, block.brick);
    }

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

    private void createBonus(Block block) {
        Bonus bonus = new Bonus(block.row, block.column, block,false);
        if (level <= 2) {
         bonus = new Bonus(block.row, block.column, block,true);
        }
        bonus.timeCreated = time;
        if (bonus.reward != null) {
            GameView gameView = new GameView(getRoot());
            gameView.addBonusToView(bonus);
            //Platform.runLater(() -> root.getChildren().add(reward.reward));
            bonuses.add(bonus);
        } else {
            LOGGER.severe("reward.reward is null");
        }
    }


    private void activateGoldStatus() {
        goldTime = time;
        ball.setFill(new ImagePattern(new Image("goldball.png")));
        getRoot().getStyleClass().add("goldRoot");
        isGoldStatus = true;
    }
    private void updateScoreAndHideBlock(Block block, int reward) {
        score += reward;
        new Score().show(block.x, block.y, reward);
        try {
            if (block.brick != null) {
                LOGGER.info("I am about to set block " + block.type + " to invisible in updateScoreandHideBlock");
               Platform.runLater(()-> block.brick.setVisible(false));
            } else {
                out.println("block.brick is null");
            }
        } catch (IndexOutOfBoundsException e) {
            out.println("Caught IndexOutOfBoundsException by Yahia: " + e.getMessage());
        }
    }
    @Override
    public void onInit() {
        // empty
    }
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
    public boolean checkCollision(Circle c, Rectangle r) {
        double dx = c.getCenterX() - Math.max(r.getX(), Math.min(c.getCenterX(), r.getX() + r.getWidth()));
        double dy = c.getCenterY() - Math.max(r.getY(), Math.min(c.getCenterY(), r.getY() + r.getHeight()));
        return (dx * dx + dy * dy) <= (c.getRadius() * c.getRadius());
    }
    @Override
    public void onPhysicsUpdate() {
        Platform.runLater(this::updateBonusesPosition);
        checkDestroyedCount();
        bossEliminated();
        setPhysicsToBall();
    }
    private void updateGoldStatus() {
            if (time - goldTime > 5000) {
                ball.setFill(new ImagePattern(new Image("ball.png")));
                if (getRoot() != null) {
                    getRoot().getStyleClass().remove("goldRoot");
                }
                isGoldStatus = false;
            }
    }
    private void updateBonusesPosition() {
            for (Bonus bonus : bonuses) {
                if (bonus.y > SCENE_HEIGHT || bonus.taken) {
                    continue;
                }
                if (bonus.y >= yPlayer && bonus.y <= yPlayer + PLAYER_HEIGHT && bonus.x >= xPlayer && bonus.x <= xPlayer + PLAYER_WIDTH) {
                    out.println("You Got it and +3 score for you");
                    bonus.taken = true;
                    bonus.reward.setVisible(false);
                    score += 10;
                    new Score().show(bonus.x, bonus.y, 10);
                }
                // Update the reward's y property to move it downwards
                bonus.y += ((time - bonus.timeCreated) / 1000.000) + 1.000; // Adjust the speed as needed
                bonus.reward.setY(bonus.y);
            }
    }
    @Override
    public void onTime(long time) {
        Main.time = time;
    }
}