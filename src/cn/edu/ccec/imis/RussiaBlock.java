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
	private static Image background;// 背景图片
	public static Image I;
	public static Image J;
	public static Image L;
	public static Image S;
	public static Image Z;
	public static Image O;
	public static Image T;
	/* 行数 */
	public static final int ROWS = 20;
	/* 列数 */
	public static final int COLS = 10;
	/* 墙 */
	private Rect[][] wall = new Rect[ROWS][COLS];
	public static final int RECT_SIZE = 26;
	/** 消掉的行数 */
	private int lines;
	/* 分数 */
	private int score;
	private int level;
	/* 正在下落方块 */
	private Block currentone = new Block();
	/* 下一个下落方块 */
	private Block nextOne = new Block();
	/* 静态初始化 */
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

	// 主程序

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		RussiaBlock rb = new RussiaBlock();
		frame.add(rb);
		frame.setSize(525, 550);
		frame.setUndecorated(true);// 去掉窗口框
		// 使frame窗口的关闭可以使用
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);// 使当前窗口居中
		frame.setVisible(true);
		rb.action();
		AePlayWave p = new AePlayWave("D:\\mymusic\\sky.mid");
		p.start();
	}

	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);// 使用this 作为观察者
		g.translate(15, 15);// 平移绘图坐标系
		paintCurrentBlock(g);// 绘制正在下落的方块
		paintWall(g);// 画墙
		paintNextOne(g);// 显示下一个要下落的方块
		paintScore(g);// 显示分数
		paintNotes(g);// 显示游戏结束语
	}

	// 画正在下落的方块
	private void paintCurrentBlock(Graphics g) {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect c = rects[i];
			int x = c.getCol() * RECT_SIZE - 1;
			int y = c.getRow() * RECT_SIZE - 1;
			g.drawImage(c.getImage(), x, y, null);
		}
	}

	// 显示下一个要下落的方块
	private void paintNextOne(Graphics g) {
		Rect[] rects = nextOne.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect c = rects[i];
			int x = (c.getCol() + 10) * RECT_SIZE - 1;
			int y = (c.getRow() + 1) * RECT_SIZE - 1;
			g.drawImage(c.getImage(), x, y, null);
		}
	}

	// 显示分数
	public static final int FONT_COLOR = 0x667799;
	public static final int FONT_SIZE = 0x20;

	private void paintScore(Graphics g) {
		Font f = getFont();// 获取当前的 面板默认字体
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
		str = "LEVEL:" + this.level; // 画等级
		g.drawString(str, x, y);
		y += 56;
		str = "TIME:" + this.t; // for test画游戏速度
		g.drawString(str, x, y);
	}

	private void paintNotes(Graphics g) { // 游戏结束时，显示结束语！
		Color oldColor = g.getColor();
		g.setFont(new Font("Arial", Font.ITALIC, 48));
		int x = 5;
		int y = 218;
		g.setColor(Color.RED);
		String str = "Game Over!";
		if (gameOver)
			g.drawString(str, x, y);
	}

	// 以网格形式显示墙体
	private void paintWall(Graphics g) {
		for (int row = 0; row < wall.length; row++) {
			Rect[] line = wall[row];
			for (int col = 0; col < line.length; col++) {
				Rect rect = line[col];
				int x = col * RECT_SIZE;
				int y = row * RECT_SIZE;
				if (rect == null) {
					g.setColor(new Color(0));
					// 画方形
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

	/* 用于启动游戏 */
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
		}, t, t); // 把时间设置成变量
	}

	/* 设置游戏难度级数 */
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
	 * 自然下落 如果能够下落就下落，否则就着陆到墙上， 而新的方块出现并开始落下。
	 */
	public void softDropAction() {
		if (BlockCanDrop()) {
			currentone.softDrop();
		} else {
			BlockLandToWall();
			destroyLines();// 破坏满的行
			checkGameOver();
			currentone = nextOne;
			nextOne = Block.randomBlock();
		}
	}

	/** 检查当前的方块能否继续下落 */
	public boolean BlockCanDrop() {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect cell = rects[i];
			int row = cell.getRow();
			int col = cell.getCol();
			if (row == ROWS - 1) {
				return false;
			}// 到墙底就不能下降了
		}
		for (int i = 0; i < rects.length; i++) {
			Rect rect = rects[i];
			int row = rect.getRow();
			int col = rect.getCol();
			if (wall[row + 1][col] != null) {
				return false;// 下方墙上有方块就不能下降了
			}
		}
		return true;
	}

	/* 方块着陆到墙上 */
	public void BlockLandToWall() {
		Rect[] rects = currentone.getRects();
		for (int i = 0; i < rects.length; i++) {
			Rect rect = rects[i];
			int row = rect.getRow();
			int col = rect.getCol();
			wall[row][col] = rect;
		}
	}

	// 检查游戏是否结束
	public void checkGameOver() {
		if (wall[0][4] == null) {
			return;
		}
		gameOver = true;
		timer.cancel();
		repaint();
	}

	/* 销毁已经满的行，并且计分 */
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
			if (line[i] == null) {// 如果有空格式就不是满行
				return false;
			}
		}
		return true;
	}

	public void deleteRow(int row) {
		for (int i = row; i >= 1; i--) {
			// 复制 [i-1] -> [i]
			System.arraycopy(wall[i - 1], 0, wall[i], 0, COLS);
		}
		Arrays.fill(wall[0], null);
	}

	private void clearWall() {
		// 将墙的每一行的每个格子清理为null
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
					System.exit(0);// 退出当前的Java进程
				}
				if (gameOver) {
					if (key == KeyEvent.VK_S) {
						startAction();
					}
					return;
				}
				// 如果暂停并且按键是[C]就继续动作
				if (pause) {// pause = false
					if (key == KeyEvent.VK_C) {
						continueAction();
					}
					return;
				}
				// 否则处理其它按键
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
		timer.cancel(); // 停止定时器
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
				return true;// 出界了
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
				return true; // 墙上有格子对象，发生重合
			}
		}
		return false;
	}

	/* 向右旋转 */
	public void rotateRightAction() {
		currentone.rotateRight();
		if (outOfBound() || coincide()) {
			currentone.rotateLeft();
		}
	}

	/* 向左旋转 */
	public void rotateLeftAction() {
		currentone.rotateLeft();
		if (outOfBound() || coincide()) {
			currentone.rotateRight();
		}
	}

}
