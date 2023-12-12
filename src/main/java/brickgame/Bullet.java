package brickgame;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.out;
import java.util.logging.Logger;

public class Bullet implements Serializable {
    public transient Rectangle bullet;

    public double x;
    public double y;
    public long timeCreated;
    public boolean hit = false;
    static Timer bulletTimer;
    static Logger LOGGER = Logger.getLogger(Bullet.class.getName());
    static final ArrayList<Bullet> bulletsArrayList = new ArrayList<>();

    public Bullet(double x, double y) {
        this.x = x;
        this.y = y;

        draw();
    }

    void draw() {
        bullet = new Rectangle();
        bullet.setWidth(25);
        bullet.setHeight(90);
        bullet.setX(x);
        bullet.setY(y);


        bullet.setVisible(true);
    }
    private static void createBullet(Block block) {

        double randomX1 = block.x + block.brick.getWidth() * Math.random();
        double randomX2 = block.x + block.brick.getWidth() * Math.random();
        double y = block.y + block.brick.getHeight(); // bottom side of the boss block
        final Bullet bullet1 = new Bullet(randomX1, y);
        final Bullet bullet2 = new Bullet(randomX2, y);

//        bullet1.x = randomX1;
//        bullet2.x = randomX2;
//        bullet1.x = 160;
//        bullet2.x = 320;
        bullet1.timeCreated = Main.time;
        bullet2.timeCreated = Main.time;
        String url = "bullet.png";
        bullet1.bullet.setFill(new ImagePattern(new Image(url)));
        bullet2.bullet.setFill(new ImagePattern(new Image(url)));
        if (bullet1.bullet != null && bullet2.bullet != null) {
/*            GameView gameView = new GameView(Main.getRoot());
            gameView.addBulletToView(bullet1);
            gameView.addBulletToView(bullet2);*/
            Platform.runLater(() -> Main.getRoot().getChildren().add(bullet1.bullet));
            Platform.runLater(() -> Main.getRoot().getChildren().add(bullet2.bullet));
            bulletsArrayList.add(bullet1);
            bulletsArrayList.add(bullet2);
            out.println("I have added two bullets");
        } else {
            LOGGER.severe("bullet.bullet is null");
        }
    }
    static void startBulletTimer(Block block) {
        bulletTimer = new Timer();
        bulletTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                createBullet(block);
            }
        }, 0, 4000); // Initial delay 0 ms, repeat every 4000 ms.
    }

    private void stopBulletTimer() {
        if (bulletTimer != null) {
            bulletTimer.cancel();
        }
    }
     static void updateBulletPosition() {
        double yPlayer = Main.getyPlayer();
        double xPlayer = Main.getxPlayer();
         // Create a copy of bulletsArrayList for iteration
         ArrayList<Bullet> bulletsCopy = new ArrayList<>(bulletsArrayList);
         for (Bullet bullet : bulletsCopy) {
            if (bullet.y > Main.SCENE_HEIGHT || bullet.hit) {
                continue;
            }
            if (bullet.y >= yPlayer && bullet.y <= yPlayer + Main.PLAYER_HEIGHT && bullet.x >= xPlayer && bullet.x <= xPlayer + Main.PLAYER_WIDTH) {
                out.println("You Got it and +3 score for you");
                bullet.hit = true;
                bullet.bullet.setVisible(false);
                Main.setScore(10);
                new Score().show(bullet.x, bullet.y, -10);
            }
            // Update the bullet's y property to move it downwards
            bullet.y += ((Main.getTime() - bullet.timeCreated) / 1000.000) + 0.500; // Adjust the speed as needed
            bullet.bullet.setLayoutY(bullet.y);
            out.println("bullet.y = " + bullet.y + " bullet.x = " + bullet.x + " bullet.timeCreated = " + bullet.timeCreated + " Main.time = " + Main.getTime());
            out.println(" visibility = "+ bullet.bullet.isVisible() + " The fill is =" + bullet.bullet.getFill());
            String imageUrl = ((ImagePattern) bullet.bullet.getFill()).getImage().getUrl();
            out.println("Image URL = " + imageUrl);
        }
    }
}