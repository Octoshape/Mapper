package myPackage;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import myPackage.CARD;
import myPackage.CARD.STATUS;


public class Utils {
	
	// GENERAL CONSTANTS
	public static final int DELAY = 200;
	public static final int CAST_DELAY = 1000;
	public static final int X_START = 486;
	public static final int X_END = 1436;
	public static final int Y_START = 80;
	public static final int Y_END = 1028;
	public static final int FIELD_WIDTH = X_END - X_START;
	public static final int FIELD_HEIGTH = Y_END - Y_START;
	public static final int TILE_WIDTH = 118;
	public static final int SEARCH_WIDTH = 10;
	public static final int OFFSET = TILE_WIDTH / 2;
	private static final int X_CAST = 950;
	private static final int Y_CAST = 920;
	public static final int X_CARD_POS = 320;
	public static final int Y_CARD1_POS = 190;
	public static final int Y_CARD2_POS = 440;
	public static final int Y_CARD3_POS = 690;
	public static final int Y_CARD4_POS = 940;
	public static boolean DEBUG = false;
	public static int DEPTH = 1;
	public static String MODE = "M"; // M & MF
	public static boolean SKIP;

	// MF CONSTANTS
	public static final int MF_X_MY_TURN = 296;
	public static final int MF_X_HIS_TURN = 1583;
	public static final int MF_Y_TURN = 10;
	public static final int MF_X_TURN_SIZE = 40;
	public static final int MF_Y_TURN_SIZE = 50;
	public static final int MF_X_DEFEAT = 740;
	public static final int MF_Y_DEFEAT = 170;
	public static final int MF_DEFEAT_WIDTH = 440;
	public static final int MF_DEFEAT_HEIGHT = 80;

	// M CONSTANTS
	public static int M_X_NO_MORE_MAPS = 1005;
	public static int M_Y_NO_MORE_MAPS = 335;
	public static int M_NO_MORE_MAPS_SIZE = 50;

	public static int M_X_CONTINUE = 1800;
	public static int M_Y_CONTINUE = 950;
	public static int M_CONTINUE_SIZE = 50;
	public static int M_X_SERVICE = 890;
	public static int M_Y_SERVICE = 330;
	public static int M_SERVICE_SIZE = 60;


	// Enums
	public static enum MAP_GEM implements IGem { EMPTY, COPPER, SILVER, GOLD, BAG, IRON, GREEN, RED, VAULT };
	public static enum GEM implements IGem { EMPTY, RED, BLUE, GREEN, BROWN, YELLOW, PURPLE, SKULL };
	public static enum DIRECTION { UP, RIGHT, DOWN, LEFT }

	public static void startNewGame() throws AWTException, InterruptedException {
		if (MODE.equals("M")) {
			click(570, 980); // Click the Minigame button.
			Thread.sleep(500);
			click(570, 780); // Click the Treasure Hunt button.
			Thread.sleep(500);
			click(970, 860); // Click the "Use a Map" button.
			Thread.sleep(7000);
		} else if (MODE.equals("MF")) {
			click(870, 320); // Click Broken Spire from Zul'Kari.
			Thread.sleep(1500);
			click(1130, 350); // Click Challenges.
			Thread.sleep(2000);
			click(1700, 730); // Click Fight.
			Thread.sleep(1000);
			click(1000, 500); // Click to start fight.
		}
	}
	
	public static void exitNoMoreMaps() throws InterruptedException, AWTException {
		click(1280, 350);
		Thread.sleep(500);
		click(1360, 200);
		Thread.sleep(2000);
	}

	public static void skipScore() throws AWTException, InterruptedException {
		Thread.sleep(2000);
		if (MODE == "M") {
			click(M_X_CONTINUE + M_CONTINUE_SIZE / 2, M_Y_CONTINUE + M_CONTINUE_SIZE / 2); // Skip stones
			Thread.sleep(5000);
		}
		click(1000, 500); // Click to skip score screen.
		Thread.sleep(5000);
	}
	

	private static void castOnBoard(Cast c) throws AWTException, InterruptedException {
		click(c.getCard().getX(), c.getCard().getY()); // Click the card.
		Thread.sleep(CAST_DELAY);
		click(X_CAST, Y_CAST); // Click the cast button.
		Thread.sleep(CAST_DELAY);
		clickOnBoard(c.getX(), c.getY()); // Click the cast target.
	}

	public static void castOnCard(Cast c) throws AWTException, InterruptedException {
		click(c.getCard().getX(), c.getCard().getY()); // Click the card.
		Thread.sleep(CAST_DELAY);
		click(X_CAST, Y_CAST); // Click the cast button.
		Thread.sleep(CAST_DELAY);
		click(c.getX(), c.getY()); // Click the cast target.
	}

	public static void cast(Cast c) throws AWTException, InterruptedException {
		click(c.getCard().getX(), c.getCard().getY()); // Click the card.
		Thread.sleep(CAST_DELAY);
		click(X_CAST, Y_CAST); // Click the cast button.
	}

	private static void makeBoardMove(BoardMove boardMove) throws AWTException {
		clickOnBoard(boardMove.row, boardMove.column);
		clickOnBoard(boardMove.row2, boardMove.column2);
	}

	public static void click(int x, int y) throws AWTException {
		Robot bot = new Robot();
		bot.mouseMove(x, y);    
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(1, 1);
	}

	public static void makeMove(Move nextMove) throws AWTException, InterruptedException {
		if (nextMove instanceof BoardMove) {
			BoardMove boardMove = (BoardMove)nextMove;
			makeBoardMove(boardMove);
		} else {
			Cast cast = (Cast)nextMove;
			switch (cast.getTarget()) {
			case ALLY:
				castOnCard(cast);
				break;
			case BOARD:
				castOnBoard(cast);
				break;
			case NONE:
				cast(cast);
				break;
			default:
				break;
			}
			cast.getCard().set_status(STATUS.INACTIVE);
		}
	}

	public static void clickOnBoard(int y, int x) throws AWTException {
		int finalX = X_START + TILE_WIDTH * (x + 1) - TILE_WIDTH / 2;
		int finalY = Y_START + TILE_WIDTH * (y + 1) - TILE_WIDTH / 2;

		Robot bot = new Robot();
		bot.mouseMove(finalX, finalY);    
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.mouseMove(1, 1);
	}

	public static boolean isMyTurn(BufferedImage image) {
		long myTurn = 0, hisTurn = 0;

		for (int x = Utils.MF_X_HIS_TURN; x < Utils.MF_X_HIS_TURN + Utils.MF_X_TURN_SIZE; x++ )
			for (int y = Utils.MF_Y_TURN; y < Utils.MF_Y_TURN + Utils.MF_Y_TURN_SIZE; y++ )
				hisTurn += image.getRGB(x, y);

		for (int x = Utils.MF_X_MY_TURN; x < Utils.MF_X_MY_TURN + Utils.MF_X_TURN_SIZE; x++ )
			for (int y = Utils.MF_Y_TURN; y < Utils.MF_Y_TURN + Utils.MF_Y_TURN_SIZE; y++ )
				myTurn += image.getRGB(x, y);

		return myTurn != Pixel.MF_MY_TURN_VAL && hisTurn == Pixel.MF_HIS_TURN_VAL;
	}

	public static long[][] extractRGB(BufferedImage image) {
		long[][] values = new long[8][8];
		long currentValue = 0;

		for (int x = Utils.X_START + 1 + Utils.TILE_WIDTH / 2; x < Utils.X_END; x+= (Utils.TILE_WIDTH + 1)) {
			for (int y = Utils.Y_START + 1 + Utils.TILE_WIDTH / 2; y < Utils.Y_END; y+= (Utils.TILE_WIDTH + 1)) {
				for (int a = x - Utils.SEARCH_WIDTH; a <= x + Utils.SEARCH_WIDTH; a++) {
					for (int b = y - Utils.SEARCH_WIDTH; b <= y + Utils.SEARCH_WIDTH; b++) {
						currentValue += image.getRGB(a, b);
					}
				}
				values[(x - Utils.X_START) / Utils.TILE_WIDTH][(y - Utils.Y_START) / Utils.TILE_WIDTH] = currentValue;
				currentValue = 0;
			}
		}
		return values;
	}

	public static boolean isGameOver(BufferedImage image) {
		if (MODE == "M") {
			long gameOver = 0;
			for (int x = Utils.M_X_CONTINUE; x < Utils.M_X_CONTINUE + Utils.M_CONTINUE_SIZE; x++ )
				for (int y = Utils.M_Y_CONTINUE; y < Utils.M_Y_CONTINUE + Utils.M_CONTINUE_SIZE; y++ )
					gameOver += image.getRGB(x, y);
			
			return gameOver == Pixel.M_CONTINUE_VAL;
		} else {
			long gameOver = 0;
			for (int x = Utils.MF_X_DEFEAT; x < Utils.MF_X_DEFEAT + Utils.MF_DEFEAT_WIDTH; x++ )
				for (int y = Utils.MF_Y_DEFEAT; y < Utils.MF_Y_DEFEAT + Utils.MF_DEFEAT_HEIGHT; y++ )
					gameOver += image.getRGB(x, y);
			
			return gameOver == Pixel.MF_DEFEAT_VAL;
		}
	}
	
	public static boolean noMoreMaps(BufferedImage image) {
		long noMoreMaps = 0;
		for (int x = Utils.M_X_NO_MORE_MAPS; x < Utils.M_X_NO_MORE_MAPS + Utils.M_NO_MORE_MAPS_SIZE; x++ )
			for (int y = Utils.M_Y_NO_MORE_MAPS; y < Utils.M_Y_NO_MORE_MAPS + Utils.M_NO_MORE_MAPS_SIZE; y++ )
				noMoreMaps += image.getRGB(x, y);

		return noMoreMaps == Pixel.M_NO_MORE_MAPS_VAL;
	}

	public static BufferedImage takeScreenshot() throws AWTException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage image = new Robot().createScreenCapture(screenRect);
		return image;
	}

	public static boolean hasBoardMoved(BufferedImage previousImage, BufferedImage image) {
		int[] rgbPrevious = new int[Utils.FIELD_WIDTH * Utils.FIELD_HEIGTH], rgb = new int[Utils.FIELD_WIDTH * Utils.FIELD_HEIGTH];
		previousImage.getRGB(Utils.X_START + Utils.OFFSET, Utils.Y_START + Utils.OFFSET, Utils.FIELD_WIDTH - Utils.OFFSET, Utils.FIELD_HEIGTH - Utils.OFFSET, rgbPrevious, 0, Utils.FIELD_WIDTH);
		image.getRGB(Utils.X_START + Utils.OFFSET, Utils.Y_START + Utils.OFFSET, Utils.FIELD_WIDTH - Utils.OFFSET, Utils.FIELD_HEIGTH - Utils.OFFSET, rgb, 0, Utils.FIELD_WIDTH);
		return !Arrays.equals(rgb, rgbPrevious);
	}

	public static long isCardReady(CARD c, BufferedImage image) {
		long value = 0;
		for (int a = c.getX() - Utils.SEARCH_WIDTH; a <= c.getX() + Utils.SEARCH_WIDTH; a++) {
			for (int b = c.getY() - Utils.SEARCH_WIDTH; b <= c.getY() + Utils.SEARCH_WIDTH; b++) {
				value += image.getRGB(a, b);
			}
		}
		
		return value;
	}

	public static boolean isServicePopupShowing(BufferedImage image) {
		long popup = 0;
		for (int x = Utils.M_X_SERVICE; x < Utils.M_X_SERVICE + Utils.M_SERVICE_SIZE; x++ )
			for (int y = Utils.M_Y_SERVICE; y < Utils.M_Y_SERVICE + Utils.M_SERVICE_SIZE; y++ )
				popup += image.getRGB(x, y);

		return popup == Pixel.M_SERVICE_VAL;
	}

	public static void skipServicePopup() throws AWTException, InterruptedException {
		click(960, 700);
		Thread.sleep(5000);
	}
	
	public static void scrollOut() throws AWTException, InterruptedException {
		int j = 20;
		Robot r = new Robot();
		while (j-- > 0) {
			r.mouseWheel(1);
			Thread.sleep(10);
		}
	}
}
