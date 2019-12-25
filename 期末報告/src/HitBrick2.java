
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

public class HitBrick2 {
	public static boolean isRestart = true;

	public static void main(String args[]) {

		InitFrame myGame;
		while (true) {
			System.out.print("");
			if (isRestart) {
				myGame = new InitFrame();
				myGame.setSize(500, 650);
				myGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				myGame.setResizable(false);
				myGame.setVisible(true);
				myGame.startBall();
				isRestart = false;
			}
		}
	}

}

class InitFrame extends JFrame {

	private double degree = 30.0;
	private JLabel ball;
	private int step = 5;
	private Brick b;
	private Plank p;
	private int score = 0;
	private JPanel buttonPanel;

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

		JButton rank = new JButton("Rank");
		rank.addActionListener(e -> {
			try {
				getRank(this);
			} catch (Exception x) {
				x.printStackTrace();
			}
		});

		JButton reStart = new JButton("Restart");
		reStart.addActionListener(e -> {
			HitBrick2.isRestart = true;
			dispose();
		});

		JPanel gamePanel = new JPanel();
		buttonPanel = new JPanel();

		Icon ballpic = new ImageIcon("ball.png");
		ball = new JLabel(ballpic);

		buttonPanel.add(start);
		buttonPanel.add(stop);
		buttonPanel.add(reStart);
		buttonPanel.add(rank);

		buttonPanel.setPreferredSize(new Dimension(495, 40));
		gamePanel.setPreferredSize(new Dimension(495, 580));
		gamePanel.setBackground(Color.BLACK);
		start.setFocusable(false);
		stop.setFocusable(false);
		rank.setFocusable(false);
		reStart.setFocusable(false);
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
			int px = p.plankX() - 250;

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
		for (int i = 0; i < b.row; i++) {
			for (int j = 0; j < b.column; j++) {
				if (b.brickCheck[i][j]) {
					isWin = false;
				}
			}
		}
		if (isWin) {
			saveScore(this);
			ballGo.stop();
		}
	}

	Timer ballGo;

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
	public Color[][] brickColor = new Color[row][column];
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
				brickColor[i][j] = randomColor[k.nextInt(5)];
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
					g.setColor(brickColor[i][j]);
					g.drawRect(2 + i * 49, 1 + j * 30, brickWidth, brickHeight);
				}
			}
		}

	}

}
