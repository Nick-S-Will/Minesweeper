package mine;

import javax.swing.JButton;

public class Tile extends JButton {
	private int x1, y1;
	private boolean mine, mined, flagged;
	private static final long serialVersionUID = 1L;
	
	public Tile(int x, int y, boolean mine) {
		this.setIcon(Minesweeper.Plain);
		x1 = x;
		y1 = y;
		this.mine = mine;
		this.mined = false;
		this.flagged = false;
	}
	
	public int getX() {
		return x1;
	}
	public int getY() {
		return y1;
	}
	public boolean isMine() {
		return mine;
	}
	public boolean isMined() {
		return mined;
	}
	public boolean isFlagged() {
		return flagged;
	}
	
	public void setMined(boolean mined) {
		this.mined = mined;
	}
	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}
}