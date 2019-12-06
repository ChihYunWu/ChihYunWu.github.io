import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;
import java.awt.event.*;
import java.awt.*;

public class HitBrick2 {

    public static void main(String args[]) {

        InitFrame myGame = new InitFrame();
        myGame.setSize(500, 650);
        myGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myGame.setResizable(false);
        myGame.setVisible(true);
    }

}

class InitFrame extends JFrame {

    private double degree = 30.0;
    private JLabel ball;
    private int step = 5;

    public InitFrame() {
        super("HitBrick");
        setLayout(new BorderLayout());

        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        JPanel gamePanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        Icon ballpic = new ImageIcon("ball.png");
        ball = new JLabel(ballpic);
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.setPreferredSize(new Dimension(495, 40));
        gamePanel.setPreferredSize(new Dimension(495, 580));
        gamePanel.setBackground(Color.BLACK);
        start.setFocusable(false);
        stop.setFocusable(false);
        gamePanel.setFocusable(true);

        gamePanel.setLayout(new BorderLayout());
        Plank p = new Plank();
        Brick b = new Brick();
        gamePanel.add(ball, BorderLayout.CENTER);
        gamePanel.add(p, BorderLayout.SOUTH);
        gamePanel.add(b, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.SOUTH);
        gamePanel.addKeyListener(p);
        startBall();
    }

    enum BallState {
        WALK, MAYHIT
    }

    // enum HitState {
    //     UPandDOWN_HIT, RandL_HIT, NONE_HIT
    // }

    private void walkBall(double degree) {
        int x = ball.getX(), y = ball.getY();
        double dx, dy, radians;
        radians = Math.toRadians(-degree);
        dx = step * Math.cos(radians);
        dy = step * Math.sin(radians);

        ball.setLocation((int) (x + dx), (int) (y + dy));

    }

    private void UDrotateDegree() {
        // degree = degree + 180.0;
        degree = 360 - degree;
        
    }

    private void RLrotateDegree() {
        // degree = 360.0 - degree;
        degree = 180.0 - degree;
    }

    // private HitState hitState = HitState.NONE_HIT;

    private void decideHitState() {
        System.out.printf("%d,%d %n",ball.getX(),ball.getY());
        // System.out.println(ball.getY());
        if (ball.getY() <= -200 || ball.getY() >= 350) {
            // hitState = HitState.UPandDOWN_HIT;
            UDrotateDegree();
        } else if (ball.getX() <= -250 || ball.getX() >= 250) {
            // hitState = HitState.RandL_HIT;
            RLrotateDegree();
        } else {
            // hitState = HitState.NONE_HIT;
        }
    }

    private void startBall() {
        BallState ballState = BallState.WALK;

        new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                walkBall(degree);
                decideHitState();
                // switch (ballState) {

                // case WALK:
                // walkBall(degree);
                // decideHitState();
                // break;
                // case MAYHIT:
                // // checkHitBall(degree);
                // break;
                // }
            }
        }).start();
        ;
    }

}

class Plank extends JPanel implements KeyListener {

    private int x = 195;
    private int y = 0;
    private int width = 100;
    private int height = 15;

    public Plank() {
        setPreferredSize(new Dimension(495, 15));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            if (!(x <= 0)) // limit bound
                x -= 10;
        }
        if (key == KeyEvent.VK_RIGHT) {
            if (!(x >= 390))// limit bound
                x += 10;
        }
        repaint();
    }

    public void keyReleased(KeyEvent event) {
    }

    public void keyTyped(KeyEvent event) {
    }

}

class Brick extends JPanel {
    private int brickWidth = 48;
    private int brickHeight = 30;
    private int row = 10;
    private int column = 5;
    private boolean[][] brickCheck = new boolean[row][column];
    private Color[] randomColor = { Color.RED, Color.white, Color.BLUE, Color.YELLOW, Color.orange };
    Random k = new Random();

    public Brick() {
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (k.nextInt(2) == 0) {
                    brickCheck[i][j] = true;
                } else {
                    brickCheck[i][j] = false;
                }
            }
        }
        setPreferredSize(new Dimension(500, 152));
        setBackground(Color.black);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (brickCheck[i][j]) {
                    g.setColor(randomColor[k.nextInt(5)]);
                    g.drawRect(2 + i * 49, 1 + j * 30, brickWidth, brickHeight);
                }
            }
        }

    }

}
