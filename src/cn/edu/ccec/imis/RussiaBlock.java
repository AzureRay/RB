package cn.edu.ccec.imis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class RussiaBlock extends JPanel {
	private static Image background;// ����ͼƬ
	public static Image I;
	public static Image J;
	public static Image L;
	public static Image S;
	public static Image Z;
	public static Image O;
	public static Image T;
	/* ���� */
	public static final int ROWS = 20;
	/* ���� */
	public static final int COLS = 10;
	/* ǽ */
	private Rect[][] wall = new Rect[ROWS][COLS];
	public static final int RECT_SIZE = 26;
	/** ���������� */
	private int lines;
	/* ���� */
	private int score;
	private int level;
	/* �������䷽�� */
	private Block currentone = new Block();
	/* ��һ�����䷽�� */
	private Block nextOne = new Block();
	/* ��̬��ʼ�� */
	static {
		try {
			background = ImageIO.read(RussiaBlock.class.getResource("RB.png"));
			T = ImageIO.read(RussiaBlock.class.getResource("T.png"));
			I = ImageIO.read(RussiaBlock.class.getResource("I.png"));
			S = ImageIO.read(RussiaBlock.class.getResource("S.png"));
			Z = ImageIO.read(RussiaBlock.class.getResource("Z.png"));
			L = ImageIO.read(RussiaBlock.class.getResource("L.png"));
			J = ImageIO.read(RussiaBlock.class.getResource("J.png"));
			O = ImageIO.read(RussiaBlock.class.getResource("O.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ������

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		RussiaBlock rb = new RussiaBlock();
		frame.add(rb);
		frame.setSize(525, 550);
		frame.setUndecorated(true);// ȥ�����ڿ�
		// ʹframe���ڵĹرտ���ʹ��
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);// ʹ��ǰ���ھ���
		frame.setVisible(true);
		rb.action();
		AePlayWave p = new AePlayWave("D:\\mymusic\\sky.mid");
		p.start();
	}

	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);// ʹ��this ��Ϊ�۲���
		g.translate(15, 15);// ƽ�ƻ�ͼ����ϵ
		paintCurrentBlock(g);// ������������ķ���
		paintWall(g);// ��ǽ
		paintNextOne(g);// ��ʾ��һ��Ҫ����ķ���
		paintScore(g);// ��ʾ����
		paintNotes(g);// ��ʾ��Ϸ������
	}

	// ����������ķ���
	private void paintCurrentBlock(Graphics g) {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect c = rects[i];
			int x = c.getCol() * RECT_SIZE - 1;
			int y = c.getRow() * RECT_SIZE - 1;
			g.drawImage(c.getImage(), x, y, null);
		}
	}

	// ��ʾ��һ��Ҫ����ķ���
	private void paintNextOne(Graphics g) {
		Rect[] rects = nextOne.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect c = rects[i];
			int x = (c.getCol() + 10) * RECT_SIZE - 1;
			int y = (c.getRow() + 1) * RECT_SIZE - 1;
			g.drawImage(c.getImage(), x, y, null);
		}
	}

	// ��ʾ����
	public static final int FONT_COLOR = 0x667799;
	public static final int FONT_SIZE = 0x20;

	private void paintScore(Graphics g) {
		Font f = getFont();// ��ȡ��ǰ�� ���Ĭ������
		Font font = new Font(f.getName(), Font.BOLD, FONT_SIZE);
		int x = 290;
		int y = 162;
		g.setColor(new Color(FONT_COLOR));
		g.setFont(font);
		String str = "SCORE:" + this.score;
		g.drawString(str, x, y);
		y += 56;
		str = "LINES:" + this.lines;
		g.drawString(str, x, y);
		y += 56;
		str = "[P]Pause";
		if (pause) {
			str = "[C]Continue";
		}
		if (gameOver) {
			str = "[S]Start!";
		}
		g.drawString(str, x, y);
		y += 120;
		str = "LEVEL:" + this.level; // ���ȼ�
		g.drawString(str, x, y);
		y += 56;
		str = "TIME:" + this.t; // for test����Ϸ�ٶ�
		g.drawString(str, x, y);
	}

	private void paintNotes(Graphics g) { // ��Ϸ����ʱ����ʾ�����
		Color oldColor = g.getColor();
		g.setFont(new Font("Arial", Font.ITALIC, 48));
		int x = 5;
		int y = 218;
		g.setColor(Color.RED);
		String str = "Game Over!";
		if (gameOver)
			g.drawString(str, x, y);
	}

	// ��������ʽ��ʾǽ��
	private void paintWall(Graphics g) {
		for (int row = 0; row < wall.length; row++) {
			Rect[] line = wall[row];
			for (int col = 0; col < line.length; col++) {
				Rect rect = line[col];
				int x = col * RECT_SIZE;
				int y = row * RECT_SIZE;
				if (rect == null) {
					g.setColor(new Color(0));
					// ������
					g.drawRect(x, y, RECT_SIZE, RECT_SIZE);
				} else {
					g.drawImage(rect.getImage(), x - 1, y - 1, null);
				}
			}
		}
	}

	private int t = 700;
	private boolean pause;
	private boolean gameOver;
	private Timer timer;

	/* ����������Ϸ */
	public void startAction() {
		clearWall();
		currentone = Block.randomBlock();
		nextOne = Block.randomBlock();
		lines = 0;
		score = 0;
		pause = false;
		gameOver = false;
		level = 1;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
				// setLevel();
			}
		}, t, t); // ��ʱ�����óɱ���
	}

	/* ������Ϸ�Ѷȼ��� */
	public void setLevel() {
		if (this.score >= 0 && this.score < 1) {

			this.level = 1;
			this.t = 700;
		}
		if (this.score >= 1 && this.score < 30) {
			this.level = 2;
			this.t = 600;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					softDropAction();
					repaint();
				}
			}, t, t);
		}
		if (this.score >= 30 && this.score < 200) {
			this.level = 3;
			this.t = 500;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					softDropAction();
					repaint();
				}
			}, t, t);
		}
		if (this.score > 200) {
			this.level = 4;
			this.t = 400;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					softDropAction();
					repaint();
				}
			}, t, t);
		}

	}

	/*
	 * ��Ȼ���� ����ܹ���������䣬�������½��ǽ�ϣ� ���µķ�����ֲ���ʼ���¡�
	 */
	public void softDropAction() {
		if (BlockCanDrop()) {
			currentone.softDrop();
		} else {
			BlockLandToWall();
			destroyLines();// �ƻ�������
			checkGameOver();
			currentone = nextOne;
			nextOne = Block.randomBlock();
		}
	}

	/** ��鵱ǰ�ķ����ܷ�������� */
	public boolean BlockCanDrop() {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect cell = rects[i];
			int row = cell.getRow();
			int col = cell.getCol();
			if (row == ROWS - 1) {
				return false;
			}// ��ǽ�׾Ͳ����½���
		}
		for (int i = 0; i < rects.length; i++) {
			Rect rect = rects[i];
			int row = rect.getRow();
			int col = rect.getCol();
			if (wall[row + 1][col] != null) {
				return false;// �·�ǽ���з���Ͳ����½���
			}
		}
		return true;
	}

	/* ������½��ǽ�� */
	public void BlockLandToWall() {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect rect = rects[i];
			int row = rect.getRow();
			int col = rect.getCol();
			wall[row][col] = rect;
		}
	}

	// �����Ϸ�Ƿ����
	public void checkGameOver() {
		if (wall[0][4] == null) {
			return;
		}
		gameOver = true;
		timer.cancel();
		repaint();
	}

	/* �����Ѿ������У����ҼƷ� */
	public void destroyLines() {
		int lines = 0;
		for (int row = 0; row < wall.length; row++) {
			if (fullRects(row)) {
				deleteRow(row);
				lines++;
			}
		}
		this.lines += lines;
		this.score += SCORE_TABLE[lines];
		setLevel();
	}

	private static final int[] SCORE_TABLE = { 0, 1, 10, 30, 200 };

	public boolean fullRects(int row) {
		Rect[] line = wall[row];
		for (int i = 0; i < line.length; i++) {
			if (line[i] == null) {// ����пո�ʽ�Ͳ�������
				return false;
			}
		}
		return true;
	}

	public void deleteRow(int row) {
		for (int i = row; i >= 1; i--) {
			// ���� [i-1] -> [i]
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0], null);
	}

	private void clearWall() {
		// ��ǽ��ÿһ�е�ÿ����������Ϊnull
		for (int row = 0; row < ROWS; row++) {
			Arrays.fill(wall[row], null);
		}
	}

	public void action() {
		startAction();
		repaint();
		KeyAdapter l = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_Q) {
					System.exit(0);// �˳���ǰ��Java����
				}
				if (gameOver) {
					if (key == KeyEvent.VK_S) {
						startAction();
					}
					return;
				}
				// �����ͣ���Ұ�����[C]�ͼ�������
				if (pause) {// pause = false
					if (key == KeyEvent.VK_C) {
						continueAction();
					}
					return;
				}
				// ��������������
				switch (key) {
				case KeyEvent.VK_RIGHT:
					moveRightAction();
					break;
				case KeyEvent.VK_LEFT:
					moveLeftAction();
					break;
				case KeyEvent.VK_DOWN:
					softDropAction();
					break;
				case KeyEvent.VK_UP:
					rotateRightAction();
					break;
				case KeyEvent.VK_Z:
					rotateLeftAction();
					break;
				case KeyEvent.VK_SPACE:
					hardDropAction();
					break;

				case KeyEvent.VK_P:
					pauseAction();
					break;
				}
				repaint();
			}
		};
		this.requestFocus();
		this.addKeyListener(l);
	}

	public void pauseAction() {
		timer.cancel(); // ֹͣ��ʱ��
		pause = true;
		repaint();
	}

	public void continueAction() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				softDropAction();
				repaint();
			}
		}, t, t);
		pause = false;
		repaint();
	}

	public void hardDropAction() {
		while (BlockCanDrop()) {
			currentone.softDrop();
		}
		BlockLandToWall();
		destroyLines();
		checkGameOver();
		currentone = nextOne;
		nextOne = Block.randomBlock();
	}

	public void moveLeftAction() {
		currentone.moveLeft();
		if (outOfBound() || coincide()) {
			currentone.moveRight();
		}
	}

	public void moveRightAction() {
		currentone.moveRight();
		if (outOfBound() || coincide()) {
			currentone.moveLeft();
		}
	}

	private boolean outOfBound() {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect rect = rects[i];
			int col = rect.getCol();
			if (col < 0 || col >= COLS) {
				return true;// ������
			}
		}
		return false;
	}

	private boolean coincide() {
		Rect[] rects = currentone.getRects();
		for (Rect rect : rects) {
			int row = rect.getRow();
			int col = rect.getCol();
			if (row < 0 || row >= ROWS || col < 0 || col >= COLS
					|| wall[row][col] != null) {
				return true; // ǽ���и��Ӷ��󣬷����غ�
			}
		}
		return false;
	}

	/* ������ת */
	public void rotateRightAction() {
		currentone.rotateRight();
		if (outOfBound() || coincide()) {
			currentone.rotateLeft();
		}
	}

	/* ������ת */
	public void rotateLeftAction() {
		currentone.rotateLeft();
		if (outOfBound() || coincide()) {
			currentone.rotateRight();
		}
	}

}
