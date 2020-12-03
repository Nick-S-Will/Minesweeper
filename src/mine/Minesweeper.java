package mine;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

public class Minesweeper implements ActionListener, MouseListener {
	public static final int GRID = 32; //tile size in pixels
	public static Minesweeper m;
	static ImageIcon Plain, Flag, Mine, Empty, one, two, three, four, five, six, seven, eight; //tile images
	static Timer t;
	JFrame f = new JFrame(), setup = new JFrame();
	JLabel minesText = new JLabel(), timeText = new JLabel(); //number of mines unflagged, length of current game in seconds
	JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 20, 1)); //size of the board
	int ticks, startMines, mines, size, coveredTiles; //number of timer updates since start, ..., number of mines unflagged, ...
	Tile[][] tiles;

	public Minesweeper() {
		try {f.setIconImage(new ImageIcon("images/icon.png").getImage());} catch (Exception e) {}
		try {setup.setIconImage(new ImageIcon("images/icon.png").getImage());} catch (Exception e) {}
		setup.setTitle("Setup");
		setup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup.setSize(10 * GRID + 14, 2 * GRID + 37);
		setup.setLocationRelativeTo(null);
		setup.setResizable(false);
		setup.getContentPane().setLayout(new GridLayout(1, 4, 0, 0));
		
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

		t = new Timer(1000, this);
		setup.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			Plain = new ImageIcon("images/plain.png");
			Flag  = new ImageIcon("images/flag.png");
			Mine  = new ImageIcon("images/mine.png");
			Empty = new ImageIcon("images/empty.png");
			one   = new ImageIcon("images/one.png");
			two   = new ImageIcon("images/two.png");
			three = new ImageIcon("images/three.png");
			four  = new ImageIcon("images/four.png");
			five  = new ImageIcon("images/five.png");
			six   = new ImageIcon("images/six.png");
			seven = new ImageIcon("images/seven.png");
			eight = new ImageIcon("images/eight.png");
		} catch (Exception e) {}

		m = new Minesweeper();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		timeText.setText("Time: " + ++ticks + "  ");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			Tile a = (Tile) e.getComponent();
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (!a.isFlagged()) {
					if (a.isMine()) {
						for (Tile[] i : tiles) for (Tile j : i) if (j.isMine()) j.setIcon(Mine);
						JOptionPane.showMessageDialog(null, "Game Over");
						System.exit(0);
					}
					else checkTile(a);
				}
			}
			else if (e.getButton() == MouseEvent.BUTTON3) {
				if (!a.isMined()) {
					a.setFlagged(!a.isFlagged());
					a.setIcon(a.isFlagged() ? Flag : Plain);
					if (a.isFlagged()) mines--;
					else mines++;
					
					minesText.setText("  Mines: " + mines);
				}
			}
		}
		catch (Exception E) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				size = (int) sizeSpinner.getValue();
				startMines = size * size / 8;
				mines = startMines;
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

				ArrayList<Integer> toAdd = new ArrayList<Integer>();
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
		}
	}

	private void checkTile(Tile a) {
		if (!a.isMined()) {
			a.setMined(true);
			coveredTiles--;
			int mineCount = 0;

			for (int x = -1;x <= 1;x++) for (int y = -1;y <= 1;y++) {
				try {
					if (tiles[a.getX()/GRID + x][a.getY()/GRID + y].isMine()) mineCount++;
				} catch (Exception E) {}
			}
			a.setIcon(getNumber(mineCount));

			if (mineCount == 0) {
				for (int x = -1;x <= 1;x++) for (int y = -1;y <= 1;y++) if (!(x == 0 && y == 0)) {
					try {
						checkTile(tiles[a.getX()/GRID + x][a.getY()/GRID + y]);
					} catch (Exception E) {}
				}
			}

			if (coveredTiles == startMines) {
				t.stop();
				JOptionPane.showMessageDialog(null, "You beat size " + size + " in " + ticks + " seconds");
				System.exit(0);
			}
		}
	}

	public static ImageIcon getNumber(int mines) {
		switch(mines) {
		case 0: return Empty;
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

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}