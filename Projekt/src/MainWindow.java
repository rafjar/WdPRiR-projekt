import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainWindow extends JFrame {
    private final int frameRate, nBalls;
    private Ball[] balls;
    private DrawPanel drawPanel;
    private final Dimension windowSize;

    MainWindow(Dimension windowSize, int frameRate, int nBalls) {
        super("Mega wyczesane kule 2D");
        this.nBalls = nBalls;
        this.frameRate = frameRate;
        this.windowSize = windowSize;

        initWindow();
    }

    public void initWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initBalls();

        drawPanel = new DrawPanel(windowSize, balls);
        add(drawPanel, BorderLayout.CENTER);

        scheduleWork();

        pack();
        setResizable(false);
        setVisible(true);
    }

    private void initBalls() {
        balls = new Ball[nBalls];
        for(int i=0; i<nBalls; ++i)
            balls[i] = new Ball(windowSize);
    }

    private void scheduleWork() {
        ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for(var ball : balls)
            scheduledService.scheduleAtFixedRate(ball, 0, 1000/frameRate, TimeUnit.MILLISECONDS);

        scheduledService.scheduleAtFixedRate(drawPanel, 0, 1000/frameRate, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow(new Dimension(500, 500), 60, 500));
    }
}
