package cn.edu.ccec.imis;
import java.util.Arrays;
import java.util.Random;
    /*4格方块 */
public class Block {
	protected Rect[] rects = new Rect[4];
	/*保存旋转的相对于轴位置状态 */
	protected State[] states;	
	/* 随机生成 4格方块, 使用简单工厂方法模式! 
	   randomBlock 随机生成一个四格方块 
	        这个方面的返回值是多态的! */
	public static Block randomBlock(){
		Random r = new Random();
		int type = r.nextInt(7);
		switch(type){
		case 0: return new T();
		case 1: return new I();
		case 2: return new J();
		case 3: return new L();
		case 4: return new O();
		case 5: return new S();
		case 6: return new Z();
		}
		return null;
	}	
	public Rect[] getRects() {
		return rects;
	}
	/* 自然下落 */
	public void softDrop(){
		for(int i=0; i<rects.length; i++){
			rects[i].moveDown();
		}
	}
     /*向右移动*/
	public void moveRight(){
		for(int i=0; i<rects.length; i++){
			this.rects[i].moveRight();
		}
	} 
	/*向左移动*/
	public void moveLeft(){
		for(int i=0; i<rects.length; i++){
			rects[i].moveLeft();
		}
	}
	private int index = 100000;
	/*向右旋转 */
	public void rotateRight() {
		index++;
		State s = states[index%states.length];
		Rect o = rects[0];
		rects[1].setRow(o.getRow()+s.row1);
		rects[1].setCol(o.getCol()+s.col1);
		rects[2].setRow(o.getRow()+s.row2);
		rects[2].setCol(o.getCol()+s.col2);
		rects[3].setRow(o.getRow()+s.row3);
		rects[3].setCol(o.getCol()+s.col3);
	}
	/*向左旋转 */
	public void rotateLeft() {
		index--;
		State s = states[index%states.length];
		Rect o = rects[0];
		rects[1].setRow(o.getRow()+s.row1);
		rects[1].setCol(o.getCol()+s.col1);
		rects[2].setRow(o.getRow()+s.row2);
		rects[2].setCol(o.getCol()+s.col2);
		rects[3].setRow(o.getRow()+s.row3);
		rects[3].setCol(o.getCol()+s.col3);
	}	
	@Override
	public String toString() {
		return Arrays.toString(rects); 
	}
	/*Block类中添加的内部类用于记录旋转状态 */
	protected class State{
		int row0,col0,row1,col1,row2,col2,row3,col3;
		public State(int row0, int col0, int row1, int col1,
			int row2, int col2,
			int row3, int col3) {
			this.row0 = row0;
			this.col0 = col0;
			this.row1 = row1;
			this.col1 = col1;
			this.row2 = row2;
			this.col2 = col2;
			this.row3 = row3;
			this.col3 = col3;
		}	  
	}	
}//Block 类的结束
class T extends Block{
	public T() {
		rects[0] = new Rect(0, 4, RussiaBlock.T);
		rects[1] = new Rect(0, 3, RussiaBlock.T);
		rects[2] = new Rect(0, 5, RussiaBlock.T);
		rects[3] = new Rect(1, 4, RussiaBlock.T);
		states = new State[]{
				 new State(0,0, 0,-1, 0,1, 1, 0),
				 new State(0,0, -1,0, 1,0, 0,-1),
				 new State(0,0, 0,1,  0,-1, -1,0),
				 new State(0,0, 1,0, -1,0, 0,1)};
	}
}
class I extends Block{
	public I() {
		rects[0] = new Rect(0, 4, RussiaBlock.I);
		rects[1] = new Rect(0, 3, RussiaBlock.I);
		rects[2] = new Rect(0, 5, RussiaBlock.I);
		rects[3] = new Rect(0, 6, RussiaBlock.I);
		states = new State[]{
				new State(0,0, 0,1, 0,-1, 0,-2),
				new State(0,0, -1,0, 1,0,2,0)};
	}
}
class L extends Block {
	public L() {
		rects[0] = new Rect(0, 4, RussiaBlock.L);
		rects[1] = new Rect(0, 3, RussiaBlock.L);
		rects[2] = new Rect(0, 5, RussiaBlock.L);
		rects[3] = new Rect(1, 3, RussiaBlock.L);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 1,-1 ),
				new State(0,0, -1,0, 1,0, -1,-1),
				new State(0,0, 0,1, 0,-1, -1,1),
				new State(0,0, 1,0, -1,0, 1,1)};	
	}
}
class J extends Block {
	public J() {
		rects[0] = new Rect(0, 4, RussiaBlock.J);
		rects[1] = new Rect(0, 3, RussiaBlock.J);
		rects[2] = new Rect(0, 5, RussiaBlock.J);
		rects[3] = new Rect(1, 5, RussiaBlock.J);
		states = new State[]{
				new State(0,0, 0,-1, 0,1, 1,1),
				new State(0,0, -1,0, 1,0, 1,-1),
				new State(0,0, 0,1, 0,-1, -1,-1),
				new State(0,0, 1,0, -1,0, -1,1 )};
	}
}
class S extends Block {
	public S() {
		rects[0] = new Rect(0, 4, RussiaBlock.S);
		rects[1] = new Rect(0, 5, RussiaBlock.S);
		rects[2] = new Rect(1, 3, RussiaBlock.S);
		rects[3] = new Rect(1, 4, RussiaBlock.S);
		states = new State[]{
			new State(0,0, 0,1, 1,-1, 1,0 ),
			new State(0,0, -1,0, 1,1, 0,1 )};
	}
}
class Z extends Block {
	public Z() {
		rects[0] = new Rect(1, 4, RussiaBlock.Z);
		rects[1] = new Rect(0, 3, RussiaBlock.Z);
		rects[2] = new Rect(0, 4, RussiaBlock.Z);
		rects[3] = new Rect(1, 5, RussiaBlock.Z);
		states = new State[]{
				new State(0,0, -1,-1, -1,0, 0,1 ),
				new State(0,0, -1,1, 0,1, 1,0 )};
	}
}
class O extends Block {
	public O() {
		rects[0] = new Rect(0, 4, RussiaBlock.O);
		rects[1] = new Rect(0, 5, RussiaBlock.O);
		rects[2] = new Rect(1, 4, RussiaBlock.O);
		rects[3] = new Rect(1, 5, RussiaBlock.O);
		states = new State[]{
				new State(0,0, 0,1, 1,0, 1,1 ),
				new State(0,0, 0,1, 1,0, 1,1 )};
	}
}
