import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel implements Runnable {

    Ball[] balls;

    DrawPanel(Dimension windowSize, Ball[] balls) {
        setPreferredSize(windowSize);
        this.balls = balls;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(var ball : balls)
            ball.paint((Graphics2D) g);
    }

    @Override
    public void run() {
        repaint();
    }
}
