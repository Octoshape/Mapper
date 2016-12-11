package myPackage;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Scanner;


public class Utils {
	
	public static final int MF_X_MY_TURN = 296;
	public static final int MF_X_HIS_TURN = 1583;
	public static final int MF_Y_TURN = 10;
	public static final int MF_X_TURN_SIZE = 40;
	public static final int MF_Y_TURN_SIZE = 50;
	
	public static int DELAY = 200;
	public static int DEPTH = 0;
	public static int X_START = 486;
	public static int X_END = 1436;
	public static int Y_START = 80;
	public static int Y_END = 1028;
	public static int FIELD_WIDTH = X_END - X_START;
	public static int FIELD_HEIGTH = Y_END - Y_START;
	public static int TILE_WIDTH = 118;
	public static int SEARCH_WIDTH = 10;
	public static int OFFSET = TILE_WIDTH / 2;
	public static boolean DEBUG = false;

	public static int X_CONTINUE = 1800;
	public static int Y_CONTINUE = 950;
	public static int CONTINUE_SIZE = 50;
	public static long M_CONTINUE_VAL = -16497979377l;
	public static long MF_MY_TURN_VAL = -15746790052l;
	public static long MF_HIS_TURN_VAL = -15888737492l;
	public static boolean SNAPSHOT = false;
	public static String MODE = "M"; // M & MF
	
	public static enum MAP_GEM implements IGem { EMPTY, COPPER, SILVER, GOLD, BAG, IRON, GREEN, RED, TREASURE };
	public static enum GEM implements IGem { EMPTY, RED, BLUE, GREEN, BROWN, YELLOW, PURPLE, SKULL };
	
	public static enum DIRECTION { UP, RIGHT, DOWN, LEFT }

	public static void startNewGame() throws AWTException, InterruptedException {
		if (MODE == "M") {
			click(570, 980); // Click the Minigame button.
			Thread.sleep(500);
			click(570, 780); // Click the Treasure Hunt button.
			Thread.sleep(500);
			click(970, 860); // Click the "Use a Map" button.
			Thread.sleep(7000);
		} else if (MODE == "MF") {
			click(960, 575); // Click Broken Spire
			Thread.sleep(500);
			click(1130, 350); // Click Challenges
			Thread.sleep(500);
			click(1700, 730); // Click Fight!!!!!!!
			Thread.sleep(500);
			click(1000, 500); // Click to start fight.
			Thread.sleep(7000); // TODO Parse screen and wait until game begins
		}
		
	}

	public static void skipScore() throws AWTException, InterruptedException {
		click(X_CONTINUE + CONTINUE_SIZE / 2, Y_CONTINUE + CONTINUE_SIZE / 2);
		Thread.sleep(5000);
		click(1000, 500); // Click to skip score screen.
		Thread.sleep(5000);
	}

	public static void click(int x, int y) throws AWTException {
		Robot bot = new Robot();
		bot.mouseMove(x, y);    
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
	}

	public static void makeMove(Move nextMove) throws AWTException {
		clickOnField(nextMove.row, nextMove.column);
		clickOnField(nextMove.row2, nextMove.column2);
	}

	public static void clickOnField(int y, int x) throws AWTException {
		int finalX = X_START + TILE_WIDTH * (x + 1) - TILE_WIDTH / 2;
		int finalY = Y_START + TILE_WIDTH * (y + 1) - TILE_WIDTH / 2;

		Robot bot = new Robot();
		bot.mouseMove(finalX, finalY);    
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(0, 0);
	}

	public static void promptEnterKey(){
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
	}
	
	public static boolean isMyTurn(BufferedImage image) {
		long myTurn = 0, hisTurn = 0;
		
		for (int x = Utils.MF_X_HIS_TURN; x < Utils.MF_X_HIS_TURN + Utils.MF_X_TURN_SIZE; x++ )
			for (int y = Utils.MF_Y_TURN; y < Utils.MF_Y_TURN + Utils.MF_Y_TURN_SIZE; y++ )
				hisTurn += image.getRGB(x, y);
		
		for (int x = Utils.MF_X_MY_TURN; x < Utils.MF_X_MY_TURN + Utils.MF_X_TURN_SIZE; x++ )
			for (int y = Utils.MF_Y_TURN; y < Utils.MF_Y_TURN + Utils.MF_Y_TURN_SIZE; y++ )
				myTurn += image.getRGB(x, y);
		
		return myTurn != Utils.MF_MY_TURN_VAL && hisTurn == Utils.MF_HIS_TURN_VAL;
	}
}
