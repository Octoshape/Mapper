package myPackage;
import java.awt.image.BufferedImage;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jnativehook.NativeHookException;

import myPackage.CARD.STATUS;
import myPackage.Sleep.THE;
import myPackage.Utils.GEM;

public class Main {

	public static void main(String[] args) throws IOException, AWTException, InterruptedException, NativeHookException {
//		BufferedImage bI = ImageIO.read(new File("input\\goldKeys.png"));
//		color(bI, 1656, 34, 1728, 102);
		
//		PVPBotGuardian specialBoard = new PVPBotGuardian("BprBsspr\nsrsygBbp\nBrgrpByy\npbysBssB\nBsgrypsp\nBbpBBbgy\nrryybBbb\nprypBbsy");
//		specialBoard.cards = new CARD[] {new CARD("MERCY", 0), new CARD("ALCHI", 1), new CARD("CAT", 2), new CARD("GARD", 3)};
//		specialBoard.cards[2].set_status(STATUS.ACTIVE);
//		specialBoard.updateBoardState();
//		Move next = specialBoard.calculateNextMove(0, null);

		Utils.initGlobalKeyListener();
        Utils.initTrayIcon();
        
		int notMyTurnCounter = 0;
		List<String> argsList= Arrays.asList(args);
		if (argsList.contains("--depth")) {
			Utils.DEPTH = Integer.parseInt(argsList.get(argsList.indexOf("--depth") + 1));
		}
		if (argsList.contains("--mode")) {
			Utils.MODE = argsList.get(argsList.indexOf("--mode") + 1);
		}
		
		Utils.showInfo();
		BufferedImage previousImage, image;

		while(true) {
			if (Utils.PAUSED) {
				Thread.sleep(500);
				continue;
			}
			image = Utils.takeScreenshot();
			AbstractBoard board = null;
			Move bestMove = null;
			try {
				do {
					previousImage = image;
					Thread.sleep(2 * Utils.DELAY);
					image = Utils.takeScreenshot();
					if (Utils.isGameOver(image)) {
						Utils.skipScore();
						if (Utils.MODE.equals("MF")) {
							Utils.MODE = "M";
						}
						Utils.startNewGame();
					} else if (Utils.isServicePopupShowing(image)) {
						Utils.skipServicePopup();
						Utils.startNewGame();
					} else if (Utils.MODE.equals("M") && Utils.noMoreMaps(image)) {
						Utils.exitNoMoreMaps();
						Utils.MODE = "MF";
						Utils.startNewGame();
					}
				} while (Utils.hasBoardMoved(previousImage, image));
				long[][] vals = Utils.extractRGB(image);

				if (Utils.MODE.equals("M")) {
					board = new TreasureBoard(vals);
					if (Utils.SKIP) {
						Utils.SKIP = false;
						continue;
					}
					for (int depth = 0; depth <= Utils.DEPTH; depth++) {
						bestMove = board.calculateNextMove(depth, (BoardMove)bestMove);
						if (depth == 0 && ((BoardMove)bestMove).extraTurns == 1) {
							break; // Always take first 5.
						}
					}
				} else if (Utils.MODE.equals("MF")) {
					if (Utils.isMyTurn(image)) {
						Sleep.until(THE.CARDS_ARE_STEADY);
						board = new MapFarmGameBoard(vals);
						if (Utils.SKIP) {
							Utils.SKIP = false;
							System.out.println("Skipped \"frame\", found bad RGB values.");
							continue;
						}
						((MapFarmGameBoard)board).initBoard();
						((MapFarmGameBoard)board).checkForCardUpdates();
						bestMove = board.calculateNextMove(0, null);
					} else {
						continue;
					}
				} else if (Utils.MODE.equals("P")) {
					if (Utils.isMyTurn(image)) {
						notMyTurnCounter = 0;
						Sleep.until(THE.CARDS_ARE_STEADY);
						board = new PVPBot(vals);
						if (Utils.SKIP) {
							Utils.SKIP = false;
							System.out.println("Skipped \"frame\", found bad RGB values.");
							continue;
						}
						((PVPBot)board).initBoard();
						((PVPBot)board).checkForCardUpdates();
						bestMove = board.calculateNextMove(0, null);
					} else {
						notMyTurnCounter++;
						if (notMyTurnCounter > 20) {
							notMyTurnCounter = 0;
							Utils.skipScore();
							Utils.startNewGame();
						}
						continue;
					}
				} else if (Utils.MODE.equals("G")) {
					if (Utils.isMyTurn(image)) {
						notMyTurnCounter = 0;
						Sleep.until(THE.CARDS_ARE_STEADY);
						PVPBotGuardian bot = new PVPBotGuardian(vals);
						if (Utils.SKIP) {
							Utils.SKIP = false;
							System.out.println("Skipped \"frame\", found bad RGB values.");
							continue;
						}
						bot.initBoard();
						bot.checkForCardUpdates();
						bot.updateBoardState();
						bestMove = bot.calculateNextMove(0, null);
					} else {
						notMyTurnCounter++;
						if (notMyTurnCounter > 30) {
							notMyTurnCounter = 0;
							Utils.skipScore();
							Utils.startNewGame();
						}
						continue;
					}
				}
				Utils.makeMove(bestMove);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unused")
	private static void color(BufferedImage image, int xLow, int yLow, int xHigh, int yHigh) throws InterruptedException, AWTException {
		Thread.sleep(500);
		long readValue = 0;
		for (int x = xLow; x < xHigh; x++) {
			for (int y = yLow; y < yHigh; y++) {
				readValue += image.getRGB(x, y);
				image.setRGB(x, y, 99999);
			}
		}
		System.out.println(readValue);
		try {
			ImageIO.write(image, "png", new File("out.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}