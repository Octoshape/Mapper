package myPackage;

import java.awt.AWTException;
import java.awt.image.BufferedImage;

public class Sleep {
	public enum THE { PVP_MENU, MAIN_MENU, BOARD_IS_READY, BATTLE_IS_READY, CARDS_ARE_STEADY, CHESTS_ARE_OPEN, OKEY_BUTTON_APPEARED, CHEST_MENU_OPEN }
	
	public static void until (THE v) {
		switch (v) {
		case CARDS_ARE_STEADY:
			cardsSteady(300);
			break;
		case BATTLE_IS_READY:
			sleep(300, 801, 676, 1079, 731, Pixel.SLEEP_BATTLE_READY);
			break;
		case BOARD_IS_READY:
			sleep(200, 1716, 6, 1766, 56, Pixel.SLEEP_BOARD_OPEN);
			boardReady(300);
			break;
		case MAIN_MENU:
			sleepWithClicks(300, 362, 948, 413, 995, Pixel.SLEEP_MAIN_MENU);
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			screenSteady(500);
			break;
		case PVP_MENU:
			sleepWithClicks(300, 341, 35, 453, 101, Pixel.SLEEP_PVP_MENU);
			screenSteady(500);
			break;
		case CHESTS_ARE_OPEN:
			sleep(100, Utils.M_X_CONTINUE, Utils.M_Y_CONTINUE, Utils.M_X_CONTINUE + Utils.M_CONTINUE_SIZE, Utils.M_Y_CONTINUE + Utils.M_CONTINUE_SIZE, Pixel.M_CONTINUE_VAL);
			break;
		case OKEY_BUTTON_APPEARED:
			sleep(100, 910, 879, 1005, 913, Pixel.OKEY_BUTTON);
			break;
		case CHEST_MENU_OPEN:
			sleep(100, 1656, 34, 1728, 102, Pixel.CHEST_MENU);
			break;
		default:
			break;
		
		}
	}
	
	private static void cardsSteady(int delay) {
		try {
			long previous, current;
			do {
				previous = Utils.getCardsValue();
				Thread.sleep(delay);
				current = Utils.getCardsValue();
			} while (previous != current);
		} catch (AWTException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void screenSteady(int delay) {
		try {
			BufferedImage previous, current = Utils.takeScreenshot();
			do {
				Thread.sleep(delay);
				previous = current;
				current = Utils.takeScreenshot();
			} while (Utils.hasBoardMoved(previous, current));
		} catch (AWTException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void boardReady(int delay) {
		try {
			boolean confirmed = false;
			long previous, current;
			while (!confirmed) {
				do {
					previous = readAllCards(delay, Utils.takeScreenshot());
					current = readAllCards(delay, Utils.takeScreenshot());
				} while (previous != current);
				// Check twice.
				previous = readAllCards(delay, Utils.takeScreenshot());
				if (previous == current) {
					confirmed = true;
				}
			}
		} catch (InterruptedException | AWTException e) { 
			e.printStackTrace();
		}
	}
	
	private static long readAllCards(int delay, BufferedImage image) throws InterruptedException, AWTException {
		Thread.sleep(delay);
		long readValue = 0;
		for (int i = 0; i < 4; i++) {
			for (int x = Utils.X_CARD_POS - Utils.SEARCH_WIDTH; x <= Utils.X_CARD_POS + Utils.SEARCH_WIDTH; x++)
				for (int y = Utils.getCardPosY(i) - Utils.SEARCH_WIDTH; y <= Utils.getCardPosY(i) + Utils.SEARCH_WIDTH; y++)
					readValue += image.getRGB(x, y);
		}
		for (int i = 0; i < 4; i++) {
			for (int x = Utils.X_CARD_ENEMY_POS - Utils.SEARCH_WIDTH; x <= Utils.X_CARD_ENEMY_POS + Utils.SEARCH_WIDTH; x++)
				for (int y = Utils.getCardPosY(i) - Utils.SEARCH_WIDTH; y <= Utils.getCardPosY(i) + Utils.SEARCH_WIDTH; y++)
					readValue += image.getRGB(x, y);
		}
		return readValue;
	}

	private static void sleep(int delay, int xLow, int yLow, int xHigh, int yHigh, long value) {
		try {
			boolean confirmed = false;
			while (!confirmed) {
				do;while (getValue(delay, xLow, yLow, xHigh, yHigh) != value);
				// Check twice.
				if (getValue(delay, xLow, yLow, xHigh, yHigh) == value) {
					confirmed = true;
				}
			}
		} catch (InterruptedException | AWTException e) { 
			e.printStackTrace();
		}
	}
	
	private static void sleepWithClicks(int delay, int xLow, int yLow, int xHigh, int yHigh, long value) {
		try {
			boolean confirmed = false;
			int clickCounter = 0;
			while (!confirmed) {
				do {
					if (clickCounter++ > 10) {
						clickCounter = 0;
						Utils.click(700, 500);
					}
				} while (getValue(delay, xLow, yLow, xHigh, yHigh) != value);
				// Check twice.
				if (getValue(delay, xLow, yLow, xHigh, yHigh) == value) {
					confirmed = true;
				}
			}
		} catch (InterruptedException | AWTException e) { 
			e.printStackTrace();
		}
	}

	private static long getValue(int delay, int xLow, int yLow, int xHigh, int yHigh) throws InterruptedException, AWTException {
		Thread.sleep(delay);
		BufferedImage image = Utils.takeScreenshot();
		long readValue = 0;
		for (int x = xLow; x < xHigh; x++) {
			for (int y = yLow; y < yHigh; y++) {
				readValue += image.getRGB(x, y);
			}
		}
		return readValue;
	}
}
