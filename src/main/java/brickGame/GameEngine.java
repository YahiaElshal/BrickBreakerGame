package brickGame;


public class GameEngine {

    private OnAction onAction;
    private int fps = 55;
    private Thread updateThread;
    private Thread physicsThread;
    public boolean isStopped = true;

    public void setOnAction(OnAction onAction) {
        this.onAction = onAction;
    }

    /**
     * @param fps set fps and we convert it to millisecond
     */
    public void setFps(int fps) {
        this.fps = (int) 1000 / fps;
    }

    private synchronized void Update() {
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

    private void Initialize() {
        onAction.onInit();
    }

    private synchronized void PhysicsCalculation() {
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

    public void start() {
        time = 0;
        Initialize();
        Update();
        PhysicsCalculation();
        TimeStart();
        isStopped = false;
    }

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

    private void TimeStart() {
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

    public void stopTime() {
        isRunning = false;
    }


    public interface OnAction {
        void onUpdate();

        void onInit();

        void onPhysicsUpdate();

        void onTime(long time);
    }

}
