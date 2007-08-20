package pk.games.test1;

import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class GameBoard {	
	private static final int TILE_WIDTH = 20;	// Width of the tiles on the board
	private static final int TILE_HEIGHT = 20;	// Height of the tiles on the board
	private static final char[] LETTERS = {'A','B','C','D','E','F','G','H','I','J'};	// Static alphabeth

	private int[][] board;		// Number matrix repsresenting the board
	private int boardSize;		// How many tiles on each row and column
	private Image boardImage;	// The whole board cnstructed from the letter images
	private int cursorPosX;		// Current x-position of cursor
	private int cursorPosY;		// Current y-position of cursor
	private Image cursor;		// Image of the cursor
	private Image cursorMarked;	// Image of tiles marked by cursor
	private int[][] marked;		// Matrix to keep track in which order the tiles are marked
	private int lastMarkedX;	// X-position of last marked tile
	private int lastMarkedY;	// Y-position of last marked tile
	private int wordLength;		// Length of all the current marked tiles
	private char[] currentWord;	// Char array with the marked tiles
	
	// Constructor, creates a new matrix with numbers and corresponding board image
	// Initializes the cursor.
	public GameBoard(int boardSize) throws Exception {
		this.boardSize = boardSize;
		wordLength = 0;
		currentWord= new char[boardSize*boardSize];

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
		cursorPosX = cursorPosY = 2;
		lastMarkedX = lastMarkedY = -1;
	}
			
	// Render board on canvas
	public void renderBoard(Graphics g, CustomFont f,int x, int y) {
		int boardPxl = boardSize*TILE_HEIGHT;
		// draw boxes for board and current word
		g.setColor(0x333388);
		g.fillRect(x,y,(8+boardPxl),(8+boardPxl));
		g.fillRect(x,y +13+boardPxl, (8+boardPxl), 15);
		g.setColor(0x000000);
		g.drawRect(x,y,(8+boardPxl),(8+boardPxl));
		g.drawRect(x,y+13+boardPxl, (8+boardPxl), 15);
		
		// draw letters
		g.drawImage(boardImage,x+4,y+4,0);
		
		// draw marked and cursor
		for (int row=0; row<boardSize; row++) 
			for (int col=0; col<boardSize; col++) 
				if (marked[row][col] != 0)
					g.drawImage(cursorMarked, x+4+col*TILE_WIDTH, y+4+row*TILE_HEIGHT, 0);
		g.drawImage(cursor, x+4+cursorPosX*TILE_WIDTH, y+4+cursorPosY*TILE_HEIGHT, 0);
		
		// draw current word
		int length = f.charsWidth(currentWord, 0, wordLength);
		if (length > boardPxl) {
			f.drawString(g, "...",x+4,y+17+boardPxl,0);
			length=(length-boardPxl)/f.charWidth('a');
			f.drawChars(g, currentWord, length+4, wordLength-(length+4), x+4+(3*f.charWidth('.')), y +17+boardPxl, 0);
		}
		else {
			f.drawChars(g, currentWord, 0, wordLength, x+4, y +17+boardPxl, 0);
		}
	}
		
	public void moveCursor(int dir) {
		switch (dir){
		case 2: // 2 - Move up
			if (cursorPosY>0 && isNeighbour(cursorPosX,cursorPosY-1) && isUnmarkedOrLast(cursorPosX,cursorPosY-1))
				cursorPosY--;
			break;
		case 4: // 4 - Move Left
			if (cursorPosX>0 && isNeighbour(cursorPosX-1,cursorPosY) && isUnmarkedOrLast(cursorPosX-1,cursorPosY))
				cursorPosX--;
			break;
		case 5: // 5 - Mark/UnMark
			if ((lastMarkedX == cursorPosX) &&(lastMarkedY == cursorPosY)) {	// Unmark tile
				wordLength--;
				
				if (wordLength == 0){
					lastMarkedX = -1;
					lastMarkedY = -1;
					marked[cursorPosY][cursorPosX] = 0;
				}
				else {
					for (int row=0; row<boardSize; row++) {
						for (int col=0; col<boardSize; col++) {
							if (marked[row][col] >wordLength)
								marked[cursorPosY][cursorPosX] = 0;
							if (marked[row][col] == wordLength) {
								lastMarkedX=col;
								lastMarkedY=row;
							}
						}
					}
				}
			}
			else {																// Mark tile
				currentWord[wordLength] = LETTERS[board[cursorPosY][cursorPosX]];
				wordLength++;
				marked[cursorPosY][cursorPosX] = wordLength;
				lastMarkedX = cursorPosX;
				lastMarkedY = cursorPosY;
			}
			break;
		case 6: // 6 - Move Right
			if (cursorPosX<boardSize-1 && isNeighbour(cursorPosX+1,cursorPosY) && isUnmarkedOrLast(cursorPosX+1,cursorPosY)) 
				cursorPosX++;
			break;
		case 8: // 8 - Move Down
			if (cursorPosY<boardSize-1 && isNeighbour(cursorPosX,cursorPosY+1) && isUnmarkedOrLast(cursorPosX,cursorPosY+1))
				cursorPosY++;
			break;
		}
	}
	
	// Submit current word to found word list and clear board
	public void submitWord(FoundWords fw) {
		if (wordLength > 2)
			fw.submitWord(currentWord, wordLength);
		wordLength=0;
		lastMarkedX = -1;
		lastMarkedY = -1;
		for (int row=0; row<boardSize; row++) 
			for (int col=0; col<boardSize; col++) 
				marked[row][col] = 0;					
	}
	
	// Check if tile is neighbour to tile(x,y)
	private boolean isNeighbour(int x,int y){
		return lastMarkedX == -1 || (Math.abs(x-lastMarkedX)<2) && (Math.abs(y-lastMarkedY)<2);
	}
	
	// Only allowed to move onto unmarked tiles, unless it's the last one marked.
	private boolean isUnmarkedOrLast(int x,int y){
		return marked[y][x] == 0 || marked[y][x] == wordLength; 
	}
}