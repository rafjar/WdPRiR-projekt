import java.awt.*;
import java.util.Random;

public class Ball implements Runnable {
    private static final double MAX_VEL = 2;

    int diameter;
    double xpos, ypos, xvel, yvel, mass;
    Dimension windowSize;
    Color color;

    Ball(Dimension windowSize) {
        this.windowSize = windowSize;

        Random r = new Random();
        color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());

        diameter = r.nextInt(20, 60);

        mass = Math.PI * diameter * diameter / 4;

        xvel = r.nextDouble(-MAX_VEL, MAX_VEL);
        yvel = r.nextDouble(-MAX_VEL, MAX_VEL);

        xpos = r.nextDouble(0, windowSize.width-diameter);
        ypos = r.nextDouble(0, windowSize.height-diameter);
    }

    public void move() {
        if(xpos + xvel < 0 || xpos + xvel >= windowSize.width - diameter)
            xvel *= -1;
        if(ypos + yvel < 0 || ypos + yvel >= windowSize.height - diameter)
            yvel *= -1;

        xpos += xvel;
        ypos += yvel;
    }

    public static boolean checkIfCollide(Ball a, Ball b) {
        double aRadius = a.diameter/2., aXpos = a.xpos + aRadius, aYpos = a.ypos + aRadius;
        double bRadius = b.diameter/2., bXpos = b.xpos + bRadius, bYpos = b.ypos + bRadius;

        double x = Math.abs(aXpos - bXpos);
        double y = Math.abs(aYpos - bYpos);

        return Math.sqrt(x*x + y*y) <= aRadius + bRadius;
    }

    public static void handleCollision(Ball a, Ball b) {
        double aRadius = a.diameter/2.;
        double bRadius = b.diameter/2.;

        double[] aVelDiff = {a.xvel-b.xvel, a.yvel-b.yvel};
        double[] aPosDiff = {a.xpos-b.xpos, a.ypos-b.ypos};
        double aNewXvel = a.xvel - 2*b.mass / (a.mass + b.mass) * dotProduct(aVelDiff, aPosDiff) / ((aRadius+bRadius)*(aRadius+bRadius)) * aPosDiff[0];
        double aNewYvel = a.yvel - 2*b.mass / (a.mass + b.mass) * dotProduct(aVelDiff, aPosDiff) / ((aRadius+bRadius)*(aRadius+bRadius)) * aPosDiff[1];

        double[] bVelDiff = {b.xvel-a.xvel, b.yvel-a.yvel};
        double[] bPosDiff = {b.xpos-a.xpos, b.ypos-a.ypos};
        double bNewXvel = b.xvel - 2*a.mass / (a.mass + b.mass) * dotProduct(bVelDiff, bPosDiff) / ((aRadius+bRadius)*(aRadius+bRadius)) * bPosDiff[0];
        double bNewYvel = b.yvel - 2*a.mass / (a.mass + b.mass) * dotProduct(bVelDiff, bPosDiff) / ((aRadius+bRadius)*(aRadius+bRadius)) * bPosDiff[1];

        a.xvel = aNewXvel;
        a.yvel = aNewYvel;
        b.xvel = bNewXvel;
        b.yvel = bNewYvel;

        while(checkIfCollide(a, b)) {
            a.move();
            b.move();
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
