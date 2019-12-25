
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.Socket;
import java.awt.*;

enum BallState {
    WALK, MAYHIT, MAYPLANK
}

public class HitBrick2 extends JFrame implements KeyListener {
    public int brickWidth = 48;
    public int brickHeight = 30;
    public int brickrow = 10;
    public int brickcolumn = 5;
    public boolean[][] brickCheck = new boolean[brickrow][brickcolumn];
    public Color[][] brickColor = new Color[brickrow][brickcolumn];
    public Color[] randomColor = { Color.RED, Color.white, Color.BLUE, Color.YELLOW, Color.orange };
    Random k = new Random();

    public int plankX = 195;
    public int plankY = 530;
    public int plankWidth = 100;
    public int plankHeight = 15;

    Timer ballGo;

    private double degree = 30.0;
    private JLabel ball;
    private int step = 5;
    private int score = 0;
    private JPanel buttonPanel;
    private JPanel gamePanel;

    private Image offScreenImage;
    private Graphics gImage;

    public HitBrick2() {
        super("HitBrick");
        setLayout(new BorderLayout());
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        startBall();
        addKeyListener(this);

        for (int i = 0; i < brickrow; i++) {
            for (int j = 0; j < brickcolumn; j++) {
                if (k.nextInt(2) == 0) {
                    brickCheck[i][j] = true;
                    brickColor[i][j] = randomColor[k.nextInt(5)];
                } else {
                    brickCheck[i][j] = false;
                }
            }
        }

        gamePanel = new JPanel();
        buttonPanel = new JPanel();
        // gamePanel.setLayout(new BorderLayout());

        JButton start = new JButton("Start");

        start.addActionListener(e -> {
            ballGo.start();
        });

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> {
            ballGo.stop();
        });

        JButton rank = new JButton("Rank");
        rank.addActionListener(e -> {
            try {
                getRank(this);
            } catch (Exception x) {
                x.printStackTrace();
            }
        });
        buttonPanel.setPreferredSize(new Dimension(495, 40));
        gamePanel.setPreferredSize(new Dimension(495, 580));
        gamePanel.setBackground(Color.BLACK);
        start.setFocusable(false);
        stop.setFocusable(false);
        rank.setFocusable(false);
        gamePanel.setFocusable(true);

        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(rank);

        // Icon ballpic = new ImageIcon("ball.png");
        // ball = new JLabel(ballpic);
        // gamePanel.add(ball, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.SOUTH);

    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(500, 650);
            gImage = offScreenImage.getGraphics();
        }
        gImage.setColor(Color.BLACK);
        gImage.fillRect(0, 0, 500, 570);
        add(buttonPanel, BorderLayout.NORTH);
        drawBrick();
        drawPlank();
        g.drawImage(offScreenImage, 0, 80, null);

    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            if (!(plankX <= 0)) // limit bound
                plankX -= 10;
        }
        if (key == KeyEvent.VK_RIGHT) {
            if (!(plankX >= 390))// limit bound
                plankX += 10;
        }
        repaint();
    }

    public void keyReleased(KeyEvent event) {
    }

    public void keyTyped(KeyEvent event) {
    }

    private void saveScore(JFrame f) {
        Socket socket;
        DataOutputStream output;
        DataInputStream input;
        String message;
        String userName;
        try {

            userName = JOptionPane.showInputDialog("Enter your Name");
            socket = new Socket("127.0.0.1", 8777);
            if (socket.isConnected()) {
                System.out.println("connect");
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                output.writeUTF("save");
                output.writeUTF(userName);
                output.writeInt(score);
                message = (String) input.readUTF();

                JOptionPane.showMessageDialog(f, userName + " got " + score + "\n" + message, "GameOver",
                        JOptionPane.PLAIN_MESSAGE);

                output.close();
                socket.close();
                if (socket.isClosed()) {
                    System.out.println("connection close");
                }
            }
        } catch (Exception x) {
            JOptionPane.showMessageDialog(f, " got " + score, "GameOver", JOptionPane.PLAIN_MESSAGE);
            x.printStackTrace();

        }

    }

    private void getRank(JFrame f) {
        Socket socket;
        DataInputStream input;
        DataOutputStream output;
        String rank;

        try {
            socket = new Socket("127.0.0.1", 8777);
            if (socket.isConnected()) {
                System.out.println("connect");
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF("rank");
                rank = (String) input.readUTF();
                System.out.println(rank);

                JOptionPane.showMessageDialog(f, rank, "Rank", JOptionPane.PLAIN_MESSAGE);

                input.close();
                socket.close();
                if (socket.isClosed()) {
                    System.out.println("connection close");
                }
            }
        } catch (Exception x) {
            JOptionPane.showMessageDialog(f, "no message", "Rank", JOptionPane.PLAIN_MESSAGE);
            x.printStackTrace();

        }
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

        for (int i = 0; i < brickrow; i++) {
            for (int j = 0; j < brickcolumn; j++) {
                // if ballPoint in ij then decide hit ij
                if (brickCheck[i][j]) {
                    if (ball.getY() > (-200 + j * 31) && (ball.getY() < (-200 + (j + 1) * 30))
                            && ball.getX() > (-250 + i * 49) && ball.getX() < (-250 + (i + 1) * 50)) {
                        Point decidePosition = ball.getLocation();
                        brickCheck[i][j] = false;
                        score++;
                        // scoreLabel.setText("score: " + Integer.toString(score));

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
            int px = plankX - 250;

            if (ballx > px && ballx < px + 195) {
                UDrotateDegree();
            } else {
                ballGo.stop();
                saveScore(this);
            }
        }
    }

    private void gameWin() {
        boolean isWin = true;
        for (int i = 0; i < brickrow; i++) {
            for (int j = 0; j < brickcolumn; j++) {
                if (brickCheck[i][j]) {
                    isWin = false;
                }
            }
        }
        if (isWin) {
            saveScore(this);
            ballGo.stop();
        }
    }

    public void startBall() {

        ballGo = new Timer(10, new ActionListener() {
            BallState ballState = BallState.WALK;

            public void actionPerformed(ActionEvent e) {

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
                gameWin();

            }
        });
    }

    private void drawPlank() {
        gImage.setColor(Color.RED);
        gImage.fillRect(plankX, plankY, plankWidth, plankHeight);
    }

    private void drawBrick() {
        for (int i = 0; i < brickrow; i++) {
            for (int j = 0; j < brickcolumn; j++) {
                if (brickCheck[i][j]) {
                    gImage.setColor(brickColor[i][j]);
                    gImage.drawRect(2 + i * 49, 1 + j * 30, brickWidth, brickHeight);
                }
            }
        }
    }

    public static void main(String args[]) {
        new HitBrick2();

    }
    // class Plank extends JPanel implements KeyListener {

    // public Plank() {
    // setPreferredSize(new Dimension(495, 15));
    // setBackground(Color.BLACK);
    // }

    // public int plankX() {
    // return x;
    // }

    // @Override
    // protected void paintComponent(Graphics g) {
    // super.paintComponent(g);
    // g.setColor(Color.RED);
    // g.fillRect(x, y, width, height);
    // }

    // public void keyPressed(KeyEvent e) {
    // int key = e.getKeyCode();
    // if (key == KeyEvent.VK_LEFT) {
    // if (!(x <= 0)) // limit bound
    // x -= 10;
    // }
    // if (key == KeyEvent.VK_RIGHT) {
    // if (!(x >= 390))// limit bound
    // x += 10;
    // }
    // repaint();
    // }

    // public void keyReleased(KeyEvent event) {
    // }

    // public void keyTyped(KeyEvent event) {
    // }

    // }

    // class Brick extends JPanel {

    // public Brick() {
    // for (int i = 0; i < row; i++) {
    // for (int j = 0; j < column; j++) {
    // if (k.nextInt(2) == 0) {
    // brickCheck[i][j] = true;
    // } else {
    // brickCheck[i][j] = false;
    // }
    // }
    // }
    // setPreferredSize(new Dimension(500, 152));
    // setBackground(Color.black);
    // }

    // @Override
    // public void paintComponent(Graphics g) {

    // super.paintComponent(g);
    // for (int i = 0; i < row; i++) {
    // for (int j = 0; j < column; j++) {
    // if (brickCheck[i][j]) {
    // g.setColor(randomColor[k.nextInt(5)]);
    // g.drawRect(2 + i * 49, 1 + j * 30, brickWidth, brickHeight);
    // }
    // }
    // }

    // }

    // }
}