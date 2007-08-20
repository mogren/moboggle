package pk.games.test1;

import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import java.util.Timer;
import javax.microedition.lcdui.Font;

import pk.games.menu.Test1GUI;

public class Test1Canvas extends GameCanvas implements Runnable, CommandListener {
	private Test1GUI midlet;			// Hold the Main Midlet
	private Command backCommand = new Command("Quit", Command.EXIT,1);
	private Thread gameThread = null;	// Game thread for the actual game 

	private int width;				// Screen width
	private int height;				// Screen height
	
	private boolean finished;		// Run game loop if not finished
	private int sleepTime = 200;	// Sleep after each game loop (ms)
	private int boardSize = 5;		// nxn board
	private int gameTime = 20;		// For how long shall the game run (s)
	private GameBoard gameBoard;	// All letter tiles
	private CustomFont font;		// Bitmap font
	private Clock clock;			// Count down clock
	private FoundWords foundWords;	// List with submitted words
		
	public Test1Canvas(Test1GUI midlet) {
		super(true);  
		this.midlet = midlet;
		addCommand(backCommand);
		setCommandListener(this);

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
		
		// Init clock
		clock = new Clock(gameTime);
		new Timer().schedule(clock, 0, 1000);

	}
	
	// Runs the game loop and if finished moves to game over screen
	public void run() {
		Thread currentThread = Thread.currentThread();
		
		while(( (currentThread == gameThread) && !finished)) { 
			try {
				verifyGameState();				// Verify game state
				moveCursor();					// User input
				updateScreen(getGraphics());	// Update screen
				Thread.sleep(sleepTime);  
			} catch (Exception e) { } 
		}	
		//midlet.mainMenuScreenShow(null);
		gameOver(getGraphics());
	} 
	
	// Start the game
	public void start() {	
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	// Stop the game
	public void stop() {
		gameThread = null;
	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == backCommand)
			midlet.mainMenuScreenShow(null);
	}
	
	// Draw a background
	private void createBackground(Graphics g) {
		g.setColor(0xdddddd);
		g.fillRect(0,0,width,height);
	}
	
	// User input, move cursor or mark/unmark letters
	private void moveCursor() {
		int keyState = getKeyStates(); 
		System.out.println(keyState);
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
		} else if ((keyState & GAME_C_PRESSED) != 0) {
			gameBoard.submitWord(foundWords);
		} else if ((keyState & GAME_D_PRESSED) != 0) {
			gameBoard.clear();
		}
	}

	// End game if out of time
	private void verifyGameState() {
		if(clock.getTimeLeft() == 0) {
			finished = true;
			return;
		}
	}
	
	// Draw all objects to the screen
	public void updateScreen(Graphics g) {				
		createBackground(g);										// Draw background
		clock.renderTime(g,font,5,5);								// Draw Timer
		gameBoard.renderBoard(g,font, 5, 25);						// Draw board
		foundWords.renderFoundWords(g, font, (18+boardSize*20), 5);	// Draw found words
		flushGraphics();	
	}
	
	// Very simple game over screen, anything more advanced should probably be in a separate class
	private void gameOver(Graphics g){
		createBackground(g);										// Draw background
		g.setColor(0x333388);
		g.fillRect(5,5,width-10,height-10);
		g.setColor(0x000000);
		g.drawRect(5,5,width-10,height-10);
		font.drawString(g,"GAME OVER!" ,60,20,0);
		int count = foundWords.getCount();
		font.drawString(g,"In "+gameTime +" seconds you found " + count + " words" ,10,40,0);
		flushGraphics();
	}
}