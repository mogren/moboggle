package pk.games.test1;

import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class GameBoard {	
	private static final int TILE_WIDTH = 20;
	private static final int TILE_HEIGHT = 20;
	private static final char[] LETTERS = {'A','B','C','D','E','F','G','H','I','J'};

	private int[][] board;
	private int boardSize;
	private Image boardImage;
	
	private int cursorPosX;
	private int cursorPosY;
	private Image cursor;
	private Image cursorMarked;
	private int[][] marked;
	private int lastMarkedX;
	private int lastMarkedY;
	private int wordLength;
	
	// Constructor, creates a new matrix with numbers and corresponding board image
	// Initializes the cursor.
	public GameBoard(int boardSizeIn) throws Exception {
		boardSize = boardSizeIn;
		wordLength = 0;

		// Create a new mutable boardImage
		Image tileImages = Image.createImage("/transplett.png");
		boardImage = Image.createImage(TILE_WIDTH*boardSize,TILE_HEIGHT*boardSize);
		Graphics bg = boardImage.getGraphics();
		bg.setColor(0x333388);
		bg.fillRect(0, 0, TILE_WIDTH*boardSize, TILE_HEIGHT*boardSize);
		
		// Random letters
		int chrNbr;
		Random rnd = new Random();
		board = new int[boardSize][boardSize];
		marked = new int[boardSize][boardSize];
		for (int row=0; row<boardSize; row++) {
			for (int col=0; col<boardSize; col++) {
				chrNbr = rnd.nextInt(10);
				board[row][col] = chrNbr;
				bg.drawRegion(tileImages, chrNbr*TILE_WIDTH, 0, TILE_WIDTH, TILE_HEIGHT, 0, col*TILE_HEIGHT, row*TILE_WIDTH, 0);
				marked[row][col]=0;
			}
		}		

		// Setup cursor
		Image cursors = Image.createImage("/cursor3.png");
		cursor = Image.createImage(cursors,0,0,TILE_WIDTH,TILE_HEIGHT,0);
		cursorMarked = Image.createImage(cursors,TILE_WIDTH,0,TILE_WIDTH,TILE_HEIGHT,0);
		cursorPosX = 2;
		cursorPosY = 2;
		lastMarkedX=-1;
		lastMarkedY=-1;
	}
			
	// Render board on canvas
	public void renderBoard(Graphics g, int x, int y) {
		// draw box
		g.setColor(0x333388);
		g.fillRect(x,y,(8+boardSize*20),(8+boardSize*20));
		g.setColor(0x000000);
		g.drawRect(x,y,(8+boardSize*20),(8+boardSize*20));
		
		// draw letters
		g.drawImage(boardImage,x+4,y+4,0);
		
		// draw marked and cursor
		for (int row=0; row<boardSize; row++) {
			for (int col=0; col<boardSize; col++) {
				if (marked[row][col] != 0){
					g.drawImage(cursorMarked, x+4+col*TILE_WIDTH, y+4+row*TILE_HEIGHT, 0);
				}
			}
		}
		g.drawImage(cursor, x+4+cursorPosX*TILE_WIDTH, y+4+cursorPosY*TILE_HEIGHT, 0);

	}
		
	public void moveCursor(int dir) {
		switch (dir){
		case 2: 
			if (cursorPosY>0 && isNeighbour(cursorPosX,cursorPosY-1) && marked[cursorPosY-1][cursorPosX] == 0) {
				cursorPosY--;
			}
			break;
		case 4: 
			if (cursorPosX>0 && isNeighbour(cursorPosX-1,cursorPosY) && marked[cursorPosY][cursorPosX-1] == 0) {
				cursorPosX--;
			}
			break;
		case 5: 
			if ((lastMarkedX == cursorPosX) &&(lastMarkedY == cursorPosY)) {
				marked[cursorPosY][cursorPosX] = 0;
				//ttt
			}
			else {
				wordLength++;
				marked[cursorPosY][cursorPosX] = wordLength;
				lastMarkedX = cursorPosX;
				lastMarkedY = cursorPosY;
			}
			break;
		case 6:
			if (cursorPosX<boardSize-1 && isNeighbour(cursorPosX+1,cursorPosY) && marked[cursorPosY][cursorPosX+1] == 0) {
				cursorPosX++;
			}
			break;
		case 8: 
			if (cursorPosY<boardSize-1 && isNeighbour(cursorPosX,cursorPosY+1) && marked[cursorPosY+1][cursorPosX] == 0) {
				cursorPosY++;
			}
			break;

		}
	}
	
	private boolean isNeighbour(int x,int y){
		return lastMarkedX == -1 || (Math.abs(x-lastMarkedX)<2) && (Math.abs(y-lastMarkedY)<2);
	}
	
//	private 
}