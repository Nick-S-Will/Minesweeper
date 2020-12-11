package mine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper implements ActionListener, MouseListener {
	public static final int GRID = 32; // Tile Size in Pixels
	static ImageIcon plain, flag, mine, empty, one, two, three, four, five, six, seven, eight; // Tile Images
	static Timer t;
	JFrame f = new JFrame(), setup = new JFrame(); // Game JFrame and Size Select JFrame
	JLabel minesText = new JLabel(), timeText = new JLabel(); // Text for mines variable and ticks variable
	JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 20, 1)); // Board Size Selector
	Point mouseDownPos;
	// Seconds Counter in Game, ..., Flagged Mines Count, Unmined Tile Count
	int ticks, startingMines, mines, size, coveredTiles;
	Tile[][] tiles;

	public Minesweeper() { // Makes Setup Menu
		try {
			var icon = new ImageIcon("images/icon.png").getImage();
			f.setIconImage(icon);
			setup.setIconImage(icon);
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Icon not found :/");
		}

		// Making Setup Frame
		setup.setTitle("Setup");
		setup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup.setSize(10 * GRID + 14, 2 * GRID + 37);
		setup.setLocationRelativeTo(null);
		setup.setResizable(false);
		setup.getContentPane().setLayout(new GridLayout(1, 4, 0, 0));

		// Adding UI Elements
		JLabel sizeText = new JLabel("Size");
		JButton start = new JButton("Start");
		Font main = new Font("Impact", Font.PLAIN, 30);
		sizeSpinner.setFont(main);
		sizeText.setFont(main);
		start.setFont(main);
		sizeText.setHorizontalAlignment(JLabel.CENTER);
		start.addMouseListener(this);
		setup.add(sizeText);
		setup.add(sizeSpinner);
		setup.add(start);

		// Timer Setup
		t = new Timer(1000, this);
		setup.setVisible(true);
	}

	private void GameInit(){
		size = (int) sizeSpinner.getValue();
		startingMines = size * size / 8;
		mines = startingMines;
		tiles = new Tile[size][size];
		coveredTiles = size * size;

		f.setTitle("Minesweeper");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(size * GRID + 14, (size + 2) * GRID + 37);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.getContentPane().setLayout(new BorderLayout());

		JPanel stats = new JPanel();
		stats.setLayout(new GridLayout(1, 2));
		stats.setPreferredSize(new Dimension(10 * GRID, 2 * GRID));
		minesText.setFont(new Font("Impact", Font.PLAIN, 30));
		minesText.setText("  Mines: " + mines);
		stats.add(minesText);
		timeText.setFont(new Font("Impact", Font.PLAIN, 30));
		timeText.setHorizontalAlignment(JLabel.RIGHT);
		timeText.setText("Time: " + ticks + "  ");
		stats.add(timeText);
		f.getContentPane().add(stats, BorderLayout.NORTH);

		JPanel board = new JPanel();
		board.setLayout(new GridLayout(size, size));

		var toAdd = new ArrayList<Integer>();
		Random r = new Random();
		while (toAdd.size() < mines) {
			int ran = r.nextInt(coveredTiles);
			if (!toAdd.contains(ran)) {
				toAdd.add(ran);
			}
		}
		for (int x = 0;x < size;x++) for (int y = 0;y < size;y++) {
			boolean thisMine = false;
			for (Integer i : toAdd) {
				if (size * x + y == i) {
					thisMine = true;
					break;
				}
			}
			Tile a = new Tile(GRID * x, GRID * y, thisMine);
			a.addMouseListener(this);
			tiles[x][y] = a;
			board.add(a);
		}
		f.getContentPane().add(board, BorderLayout.CENTER);

		t.start();
		setup.setVisible(false);
		f.setVisible(true);
	}

	public static void main(String[] args) {
		// Getting Necessary Assets for Gameplay
		try {
			plain = new ImageIcon("images/plain.png");
			flag = new ImageIcon("images/flag.png");
			mine = new ImageIcon("images/mine.png");
			empty = new ImageIcon("images/empty.png");
			one   = new ImageIcon("images/one.png");
			two   = new ImageIcon("images/two.png");
			three = new ImageIcon("images/three.png");
			four  = new ImageIcon("images/four.png");
			five  = new ImageIcon("images/five.png");
			six   = new ImageIcon("images/six.png");
			seven = new ImageIcon("images/seven.png");
			eight = new ImageIcon("images/eight.png");
		} catch (Exception ignored) {
			JOptionPane.showMessageDialog(null, "Sprites not found :/");
		}

		new Minesweeper();
	}

	// Called Every Timer Tick
	public void actionPerformed(ActionEvent e) {
		timeText.setText("Time: " + ++ticks + "  ");
	}

	public void mousePressed(MouseEvent e) {
		// Saved Position on Mouse Down
		try {
			mouseDownPos = e.getLocationOnScreen();
		} catch (Exception ignored){}
	}

	// Called Every Click on the UI
	public void mouseReleased(MouseEvent e) {
		try {
			// Check If Clicked on Tile
			Tile a = (Tile) e.getComponent();

			// If Mouse Up on Same Tile
			if (PointDistance(mouseDownPos, e.getLocationOnScreen()) <= GRID / 2){
				if (e.getButton() == MouseEvent.BUTTON1) { // Left Click Try Dig
					if (!a.isFlagged()) {
						if (a.isMine()) { // Lose Game
							for (Tile[] i : tiles) for (Tile j : i) if (j.isMine()) j.setIcon(mine);
							JOptionPane.showMessageDialog(null, "Game Over");
							System.exit(0);
						}
						else CheckTile(a); // Dig
					}
				}
				else if (e.getButton() == MouseEvent.BUTTON3) { // Right Click Toggle Flag
					if (!a.isMined()) {
						a.setFlagged(!a.isFlagged());
						a.setIcon(a.isFlagged() ? flag : plain);
						if (a.isFlagged()) mines--;
						else mines++;

						minesText.setText("  Mines: " + mines);
					}
				}
			}
		}
		catch (Exception E) {
			if (e.getButton() == MouseEvent.BUTTON1) GameInit();
		}

		mouseDownPos = null;
	}

	private int PointDistance(Point a, Point b){ // Length Between 2 Points
		return (int) Math.sqrt(Math.pow(Math.abs(a.x - b.x), 2) + Math.pow(Math.abs(a.y - b.y), 2));
	}

	private void CheckTile(Tile a) { // Digs Tile a
		if (!a.isMined()) {
			a.setMined(true);
			coveredTiles--;
			int surroundingMines = 0;

			for (int x = -1;x <= 1;x++) for (int y = -1;y <= 1;y++) { // Count Surrounding Tiles
				try {
					if (tiles[a.getX()/GRID + x][a.getY()/GRID + y].isMine()) surroundingMines++;
				} catch (Exception ignored) {}
			}
			a.setIcon(GetMineCountImage(surroundingMines));

			if (surroundingMines == 0) {
				// Dig Surrounding Tiles
				for (int x = -1;x <= 1;x++) for (int y = -1;y <= 1;y++) if (!(x == 0 && y == 0)) {
					try {
						CheckTile(tiles[a.getX()/GRID + x][a.getY()/GRID + y]);
					} catch (Exception ignored) {}
				}
			}

			if (coveredTiles == startingMines) { // Check Win
				t.stop();
				JOptionPane.showMessageDialog(
						null, "You beat size " + size + " in " + ticks + " seconds");
				System.exit(0);
			}
		}
	}

	public static ImageIcon GetMineCountImage(int mines) { // Returns Image For Number
		switch(mines) {
			case 0: return empty;
			case 1: return one;
			case 2: return two;
			case 3: return three;
			case 4: return four;
			case 5: return five;
			case 6: return six;
			case 7: return seven;
			default: return eight;
		}
	}

	// Added Methods From MouseListener
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}