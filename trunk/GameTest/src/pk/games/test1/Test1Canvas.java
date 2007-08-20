package pk.games.test1;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Graphics;
import java.util.Timer;
import javax.microedition.lcdui.Font;

public class Test1Canvas extends GameCanvas implements Runnable {

	private int width;				// Screen width
	private int height;				// Screen height
	
	private int sleepTime = 200;	// Sleep after each game loop
	private int boardSize = 5;		// nxn board
	private GameBoard gameBoard;	// All letter tiles
	private CustomFont font;		// Bitmap font
	private Clock clock;			// Count down clock
	private FoundWords foundWords;	// List with submitted words
		
	public Test1Canvas() {
		super(false);
		width = getWidth();
		height = getHeight();
		
        // get font and metrics
        font = CustomFont.getFont( 
            "/mono.png",        // the font definition file
            Font.SIZE_SMALL,    // ignored
            Font.STYLE_PLAIN ); // no styling	
        
		// Setup board
		try {
			gameBoard = new GameBoard(boardSize);
		} catch (Exception e) { }
		
		// Setup found words
		foundWords = new FoundWords();
	}
	
	public void run() {
		
		// Init clock
		clock = new Clock(15);
		new Timer().schedule(clock, 0, 1000);
				
		while(true) { 
			updateScreen(getGraphics());
			try {
				Thread.sleep(sleepTime);  
			} catch (Exception e) { } 
		}	
	} 
	
	public void start() {
		
		Thread runner = new Thread(this);
		runner.start();
		}
	
	private void createBackground(Graphics g) {
		g.setColor(0xdddddd);
		g.fillRect(0,0,width,height);
		
		//Blue squares for objects
//		g.setColor(0x333388);
//		g.fillRect((18+boardSize*20),25,width-(18+boardSize*20)-5,height-30);	//Found words
//		g.setColor(0x000000);
//		g.drawRect((18+boardSize*20),25,width-(18+boardSize*20)-5,height-30);
	}
	
	private void moveCursor() {
		int keyState = getKeyStates(); 
		if ((keyState & UP_PRESSED) != 0) { 
			gameBoard.moveCursor(2); 
		} else if ((keyState & RIGHT_PRESSED) != 0) { 
			gameBoard.moveCursor(6); 
		} else if ((keyState & DOWN_PRESSED) != 0) { 
			gameBoard.moveCursor(8); 
		} else if ((keyState & LEFT_PRESSED) != 0) { 
			gameBoard.moveCursor(4); 
		} else if ((keyState & FIRE_PRESSED) != 0) {
			gameBoard.moveCursor(5);
		} else if ((keyState & GAME_A_PRESSED) != 0) {
			//TODO: Rätt knapp !!!
			gameBoard.submitWord(foundWords);
		}
	}
	
	public void updateScreen(Graphics g) {
		
		// Draw background
		createBackground(g);
		
		// Draw Timer
		clock.renderTime(g,font,5,5);
		
		// Draw board
		moveCursor();
		gameBoard.renderBoard(g,font, 5, 25);

		// Draw found words
		foundWords.renderFoundWords(g, font, (18+boardSize*20), 5);
		
		flushGraphics();
	
	}
}