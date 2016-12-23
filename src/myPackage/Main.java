package myPackage;
import java.awt.image.BufferedImage;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
//		BufferedImage bI = ImageIO.read(new File("input\\nomaps.png"));
//		color(bI);
//		GameBoard gb = new GameBoard(Utils.extractRGB(bI));
//		System.out.println(gb);
		int i = 0;
		PrintStream logger = new PrintStream(new File ("log.txt"));
		List<String> argsList= Arrays.asList(args);
		if (argsList.contains("--debug")) {
			Utils.DEBUG = true;	
		}
		if (argsList.contains("--depth")) {
			Utils.DEPTH = Integer.parseInt(argsList.get(argsList.indexOf("--depth") + 1));
		}
		if (argsList.contains("--mode")) {
			Utils.MODE = argsList.get(argsList.indexOf("--mode") + 1);
		}
		
		Thread.sleep(3000);
		BufferedImage previousImage, image = Utils.takeScreenshot();
		Utils.startNewGame();

		while(true) {
			AbstractGameBoard board = null;
			Move bestMove = null;
			try {
				do {
					previousImage = image;
					Thread.sleep(Utils.DELAY);
					image = Utils.takeScreenshot();
					if (Utils.isGameOver(image)) {
						Utils.skipScore();
						if (Utils.MODE.equals("MF")) {
							Utils.MODE = "M";
						}
						Utils.startNewGame();
					} else if (Utils.noMoreMaps(image)) {
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
						board = new GameBoard(vals);
						if (Utils.SKIP) {
							Utils.SKIP = false;
							System.out.println("Skipped \"frame\", found bad RGB values.");
							continue;
						}
						((GameBoard)board).updateCardsAndStatus(image);
						bestMove = board.calculateNextMove(0, null);
					} else {
						continue;
					}
				}

				if (Utils.DEBUG) {
					ImageIO.write(image, "png", new File("images\\log" + i++ + ".png"));
					logger.println("At board " + (i - 1) + ":");
					logger.println("Making move: " + bestMove);
					logger.println();
				}
				Utils.makeMove(bestMove);
			} catch (Exception e) {
				logger.println("Exception " + e.getMessage() + " thrown in Main with:\n");
				logger.println("Board state: " + board + "\n");
				logger.println("Calculated move: " + bestMove + "\n");
				logger.println("Restarting bot.\n\n");
			}
		}
	}

		private static void color(BufferedImage bI) throws IOException {
			long gameOver = 0;
			for (int x = Utils.M_X_NO_MORE_MAPS; x < Utils.M_X_NO_MORE_MAPS + Utils.M_NO_MORE_MAPS_SIZE; x++ )
				for (int y = Utils.M_Y_NO_MORE_MAPS; y < Utils.M_Y_NO_MORE_MAPS + Utils.M_NO_MORE_MAPS_SIZE; y++ )
					gameOver += bI.getRGB(x, y);
	
			System.out.println(gameOver);
		}
}	
