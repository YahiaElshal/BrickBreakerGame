package brickgame;

/**
 * The {@code GameEngine} class represents the game engine for a simple brick game.
 * It manages the game loop, updates, physics calculations, and time tracking.
 */
public class GameEngine {

    private OnAction onAction;
    private int fps;
    private Thread updateThread;
    private Thread physicsThread;
    private boolean isStopped = true;

    /**
     * Sets the callback interface for game actions.
     *
     * @param onAction The {@link OnAction} instance for handling game actions.
     */
    public void setOnAction(OnAction onAction) {
        this.onAction = onAction;
    }

    /**
     * Sets the frames per second (FPS) for the game engine.
     *
     * @param fps The desired frames per second.
     */
    public void setFps(int fps) {
        this.fps = 1000 / fps;
    }

    /**
     * Initializes the game engine.
     */
    private synchronized void initialize() {
        onAction.onInit();
    }

    /**
     * Starts the game engine, initiating the game loop, update thread, physics thread, and time tracking.
     */
    public void start() {
        time = 0;
        initialize();
        update();
        physicscalculation();
        timeStart();
        isStopped = false;
    }

    /**
     * Stops the game engine by interrupting the update thread, physics thread, and time thread.
     * Also sets the isStopped flag to true.
     */
    public void stop() {
        if (!isStopped) {
            isStopped = true;
            updateThread.interrupt();
            physicsThread.interrupt();
            timeThread.interrupt();
        }
    }

    private long time = 0;
    private Thread timeThread;
    private volatile boolean isRunning = true;

    /**
     * Initiates the time tracking thread, updating time every millisecond.
     */
    private void timeStart() {
        timeThread = new Thread(() -> {
            try {
                while (isRunning) {
                    time++;
                    onAction.onTime(time);
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timeThread.start();
    }

    /**
     * Updates the game by invoking the {@code onUpdate} method in a separate thread.
     * The update is performed at the specified frames per second.
     */
    private synchronized void update() {
        updateThread = new Thread(() -> {
            while (!updateThread.isInterrupted()) {
                try {
                    onAction.onUpdate();
                    Thread.sleep(fps);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updateThread.start();
    }

    /**
     * Initiates the physics calculation thread, updating physics every millisecond.
     */
    private synchronized void physicscalculation() {
        physicsThread = new Thread(() -> {
            while (!physicsThread.isInterrupted()) {
                try {
                    onAction.onPhysicsUpdate();
                    Thread.sleep(fps);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        physicsThread.start();
    }

    /**
     * Interface for handling game actions, including update, initialization, physics update, and time tracking.
     */
    public interface OnAction {
        /**
         * Invoked for game updates.
         */
        void onUpdate();

        /**
         * Invoked during game initialization.
         */
        void onInit();

        /**
         * Invoked for physics updates.
         */
        void onPhysicsUpdate();

        /**
         * Invoked to track game time.
         *
         * @param time The current game time.
         */
        void onTime(long time);
    }
}
