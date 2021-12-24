import java.awt.*;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Ball implements Runnable {
    private static final double MAX_VEL = 1;

    int diameter;
    double xpos, ypos, xvel, yvel, mass, radius;
    Dimension windowSize;
    Color color;
    private final ReentrantLock lock = new ReentrantLock();

    Ball(Dimension windowSize) {
        this.windowSize = windowSize;

        Random r = new Random();
        color = new Color(r.nextFloat(0, (float) 0.6), r.nextFloat(0, (float) 0.6), r.nextFloat(0, (float) 0.6));

        diameter = r.nextInt(20, 60);
        radius = diameter / 2.;

        mass = Math.PI * diameter * diameter / 4;

        xvel = r.nextDouble(-MAX_VEL, MAX_VEL);
        yvel = r.nextDouble(-MAX_VEL, MAX_VEL);

        xpos = r.nextDouble(0, windowSize.width-diameter);
        ypos = r.nextDouble(0, windowSize.height-diameter);
    }

    public void move() {
        lock.lock();
        try {
            if (xpos + xvel < 0 || xpos + xvel >= windowSize.width - diameter)
                xvel *= -1;
            if (ypos + yvel < 0 || ypos + yvel >= windowSize.height - diameter)
                yvel *= -1;

            xpos += xvel;
            ypos += yvel;
        } finally {
            lock.unlock();
        }
    }

    public static boolean checkIfCollide(Ball a, Ball b) {
        double aXpos = a.xpos + a.radius, aYpos = a.ypos + a.radius;
        double bXpos = b.xpos + b.radius, bYpos = b.ypos + b.radius;

        double x = Math.abs(aXpos - bXpos);
        double y = Math.abs(aYpos - bYpos);

        return Math.sqrt(x*x + y*y) <= a.radius + b.radius;
    }

    public static void handleCollision(Ball a, Ball b) {
        a.lock.lock();
        b.lock.lock();
        try {
            double distanceBetweenBalls = a.radius + b.radius;
            double x = a.xpos-b.xpos;
            double y = a.ypos-b.ypos;
            double scale = (a.radius+b.radius) / Math.sqrt(x*x + y*y);

            double[] aVelDiff = {a.xvel-b.xvel, a.yvel-b.yvel};
            double[] aPosDiff = {(x)*scale, (y)*scale};
            double aNewXvel = a.xvel - 2*b.mass / (a.mass + b.mass) * dotProduct(aVelDiff, aPosDiff) / (distanceBetweenBalls*distanceBetweenBalls) * aPosDiff[0];
            double aNewYvel = a.yvel - 2*b.mass / (a.mass + b.mass) * dotProduct(aVelDiff, aPosDiff) / (distanceBetweenBalls*distanceBetweenBalls) * aPosDiff[1];

            double[] bVelDiff = {b.xvel-a.xvel, b.yvel-a.yvel};
            double[] bPosDiff = {(-x)*scale, (-y)*scale};
            double bNewXvel = b.xvel - 2*a.mass / (a.mass + b.mass) * dotProduct(bVelDiff, bPosDiff) / (distanceBetweenBalls*distanceBetweenBalls) * bPosDiff[0];
            double bNewYvel = b.yvel - 2*a.mass / (a.mass + b.mass) * dotProduct(bVelDiff, bPosDiff) / (distanceBetweenBalls*distanceBetweenBalls) * bPosDiff[1];

            final double collideScale = 0.01;
            a.xvel = collideScale*aNewXvel;
            a.yvel = collideScale*aNewYvel;
            b.xvel = collideScale*bNewXvel;
            b.yvel = collideScale*bNewYvel;

            while(checkIfCollide(a, b)) {
                a.move();
                b.move();
            }
            a.xvel = aNewXvel;
            a.yvel = aNewYvel;
            b.xvel = bNewXvel;
            b.yvel = bNewYvel;
        } finally {
            b.lock.unlock();
            a.lock.unlock();
        }
    }

    private static double dotProduct(double[] vecA, double[] vecB) {
        double sum = 0;
        for(int i=0; i<vecA.length; ++i)
            sum += vecA[i] * vecB[i];

        return sum;
    }

    public void paint(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int) xpos, (int) ypos, diameter, diameter);
    }

    @Override
    public void run() {
        move();
    }
}
