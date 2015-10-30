package cn.edu.ccec.imis;
import java.awt.Image;
/*小格子*/
public class Rect {
	private int row;
	private int col;
	private Image image;//格子的贴图	
	public Rect() {}
	public Rect(int row, int col, Image image) {
		super();
		this.row = row;
		this.col = col;
		this.image = image;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}		
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public void moveRight(){
		col++; 
	}	
	public void moveLeft(){
		col--;
	}	
	public void moveDown(){
		row++;
	}	
	@Override
	public String toString() {
		return "["+row+","+col+"]";
	}
}
