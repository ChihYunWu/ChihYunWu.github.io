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
    private int step = 4;
    private Brick b;
    private Plank p;

    public InitFrame() {
        super("HitBrick");
        setLayout(new BorderLayout());

        JButton start = new JButton("Start");

        start.addActionListener(e -> {
            ballGo.start();
        });

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> {
            ballGo.stop();
        });

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
        p = new Plank();
        b = new Brick();
        gamePanel.add(ball, BorderLayout.CENTER);
        gamePanel.add(p, BorderLayout.SOUTH);
        gamePanel.add(b, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.SOUTH);
        gamePanel.addKeyListener(p);
        startBall();
    }

    enum BallState {
        WALK, MAYHIT, MAYPLANK
    }

    private void walkBall(double degree) {
        int x = ball.getX(), y = ball.getY();
        double dx, dy, radians;
        radians = Math.toRadians(-degree);
        dx = step * Math.cos(radians);
        dy = step * Math.sin(radians);

        ball.setLocation((int) (x + dx), (int) (y + dy));

    }

    private void UDrotateDegree() {
        degree = 360 - degree;

    }

    private void RLrotateDegree() {
        degree = 180.0 - degree;
    }

    private void HitWall() {
        if (ball.getY() <= -200 || ball.getY() >= 350) {

            UDrotateDegree();
        } else if (ball.getX() <= -250 || ball.getX() >= 250) {

            RLrotateDegree();
        } else {

        }
    }

    // -49~-200
    private void HitBircks() {

        for (int i = 0; i < b.row; i++) {
            for (int j = 0; j < b.column; j++) {
                // if ballPoint in ij then decide hit ij
                if (b.brickCheck[i][j]) {
                    if (ball.getY() > (-200 + j * 31) && (ball.getY() < (-200 + (j + 1) * 30))
                            && ball.getX() > (-250 + i * 49) && ball.getX() < (-250 + (i + 1) * 50)) {
                        Point decidePosition = ball.getLocation();
                        b.brickCheck[i][j] = false;
                        if (decidePosition.getY() > (-200 + j * 33) && (ball.getY() < (-200 + (j + 1) * 28))) {
                            RLrotateDegree();
                        } else {
                            UDrotateDegree();
                        }
                    }
                }
            }
        }
        if (ball.getY() <= -200) {
            UDrotateDegree();
        } else if (ball.getX() <= -250 || ball.getX() >= 250) {
            RLrotateDegree();
        } else {
        }
    }

    private void HitPlank() {
        if (ball.getX() <= -250 || ball.getX() >= 250) {
            RLrotateDegree();
        }
        if (ball.getY() > 350) {

            int ballx = ball.getX();
            int px = p.plankX() - 250;
            System.out.println(ballx);
            System.out.println(px);
            if (ballx > px && ballx < px + 195) {
                UDrotateDegree();
            } else {
                ballGo.stop();
            }
        }
    }

    Timer ballGo;

    private void startBall() {

        ballGo = new Timer(10, new ActionListener() {
            BallState ballState = BallState.WALK;

            public void actionPerformed(ActionEvent e) {
                // System.out.println(ballState);
                if (ball.getY() < -70) {
                    ballState = BallState.MAYHIT;
                } else if (ball.getY() > 300) {
                    ballState = BallState.MAYPLANK;
                } else {
                    ballState = BallState.WALK;

                }
                walkBall(degree);
                switch (ballState) {
                case WALK:
                    HitWall();
                    break;

                case MAYHIT:
                    HitBircks();
                    break;
                case MAYPLANK:
                    HitPlank();
                    break;

                }

            }
        });
    }

}

class Plank extends JPanel implements KeyListener {

    public int x = 195;
    public int y = 0;
    public int width = 100;
    public int height = 15;

    public Plank() {
        setPreferredSize(new Dimension(495, 15));
        setBackground(Color.BLACK);
    }

    public int plankX() {
        return x;
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
    public int brickWidth = 48;
    public int brickHeight = 30;
    public int row = 10;
    public int column = 5;
    public boolean[][] brickCheck = new boolean[row][column];
    public Color[] randomColor = { Color.RED, Color.white, Color.BLUE, Color.YELLOW, Color.orange };
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
