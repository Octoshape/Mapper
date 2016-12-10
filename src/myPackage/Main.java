package myPackage;
import java.awt.image.BufferedImage;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import myPackage.GameBoard.Move;
import myPackage.Utils.GEM;

public class Main {

	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
		//		long[][] vals = extractRGB(ImageIO.read(new File("input\\test.png")));
		//		GameBoard g = new GameBoard(analyzeRGB(vals));
		//			
		//		System.out.println(g.calculateNextMove(1, g.calculateNextMove(0, null)));

		int i = 0;
		@SuppressWarnings("resource")
		PrintStream logger = new PrintStream(new File ("log.txt"));
		List<String> argsList= Arrays.asList(args);
		if (argsList.contains("--debug")) {
			Utils.DEBUG = true;
		}

		if (argsList.contains("--depth")) {
			Utils.DEPTH = Integer.parseInt(argsList.get(argsList.indexOf("--depth") + 1));
		}

		Thread.sleep(3000);
		BufferedImage previousImage, image = takeScreenshot(); //ImageIO.read(new File("images\\base.png")); 
		Utils.startNewGame();

		while(true) {
			do {
				previousImage = image;
				Thread.sleep(Utils.DELAY);
				image = takeScreenshot();
				if (isGameOver(image)) {
					Utils.skipScore();
					Utils.startNewGame();
				}
			} while (hasChanged(previousImage, image));

//			image = ImageIO.read(new File("input\\test.png"));
			long[][] values = extractRGB(image);
			GameBoard game = new GameBoard(analyzeRGB(values));
			Move bestMove = null;
			for (int depth = 0; depth <= Utils.DEPTH; depth++) {
				bestMove = game.calculateNextMove(depth, bestMove);
				if (depth == 0 && bestMove.extraTurns == 1) {
					break; // Always take first 5.
				}
			}

			if (Utils.DEBUG) {
				ImageIO.write(image, "png", new File("images\\log" + i++ + ".png"));
				logger.println("At board " + (i - 1) + ":");
				logger.println("Making move: " + bestMove);
				logger.println("With second move: " + bestMove.nextMove);
				logger.println();
			} else {
				Utils.makeMove(bestMove);
//				Move nextMove = bestMove;
//				do {
//					/* make america great again
//					* and also proceed with subsequent moves only if they're as GOOD as initially thought
//					* (things falling down from the top might have changed the predicted surrounding area)
//					* this optimization gets more important with higher depth
//					*/
//					if (game.makeMove(nextMove)) {
//						Utils.makeMove(nextMove);
//						nextMove = nextMove.nextMove;
//					} else {
//						if (Utils.DEBUG) {
//							logger.println("Collapsing messed up our move: " + nextMove + " completely.");
//						}
//						break;
//					}
//
//					do {
//						previousImage = image;
//						Thread.sleep(Utils.DELAY);
//						image = takeScreenshot();
//						if (isGameOver(image)) {
//							Utils.skipScore();
//							Utils.startNewGame();
//							nextMove = null;
//						}
//					} while (hasChanged(previousImage, image));
//
//					if (nextMove != null && Utils.DEBUG) {
//						ImageIO.write(image, "png", new File("images\\log" + i++ + ".png"));
//						logger.println("At board " + (i - 1) + ":");
//						logger.println("Trying to make move: " + nextMove);
//					}
//
//					values = extractRGB(image);
//					game = new GameBoard(analyzeRGB(values));
//				} while (nextMove != null);
			}
		}
	}

	private static boolean isGameOver(BufferedImage image) {
		long gameOver = 0;
		for (int x = Utils.X_CONTINUE; x < Utils.X_CONTINUE + Utils.CONTINUE_SIZE; x++ )
			for (int y = Utils.Y_CONTINUE; y < Utils.Y_CONTINUE + Utils.CONTINUE_SIZE; y++ )
				gameOver += image.getRGB(x, y);

		return gameOver == Utils.CONTINUE_VAL;
	}

	private static boolean hasChanged(BufferedImage previousImage, BufferedImage image) {
		int[] rgbPrevious = new int[Utils.FIELD_WIDTH * Utils.FIELD_HEIGTH], rgb = new int[Utils.FIELD_WIDTH * Utils.FIELD_HEIGTH];
		previousImage.getRGB(Utils.X_START + Utils.OFFSET, Utils.Y_START + Utils.OFFSET, Utils.FIELD_WIDTH - Utils.OFFSET, Utils.FIELD_HEIGTH - Utils.OFFSET, rgbPrevious, 0, Utils.FIELD_WIDTH);
		image.getRGB(Utils.X_START + Utils.OFFSET, Utils.Y_START + Utils.OFFSET, Utils.FIELD_WIDTH - Utils.OFFSET, Utils.FIELD_HEIGTH - Utils.OFFSET, rgb, 0, Utils.FIELD_WIDTH);
		return !Arrays.equals(rgb, rgbPrevious);
	}

	private static BufferedImage takeScreenshot() throws AWTException {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage image = new Robot().createScreenCapture(screenRect);
		return image;
	}

	private static GEM[][] analyzeRGB(long[][] values) {
		GEM[][] result = new GEM[8][8];
		for (int x = 0; x < values.length; x++) {
			for (int y = 0; y < values.length; y++) {
				long value = values[y][x];
				if (value < -4300000000l)
					result[x][y] = GEM.valueOf("TREASURE");
				else if (value < -4070000000l)
					result[x][y] = GEM.valueOf("SILVER");
				else if (value < -3800000000l)
					result[x][y] = GEM.valueOf("RED");
				else if (value < -3400000000l)
					result[x][y] = GEM.valueOf("IRON");
				else if (value < -3100000000l)
					result[x][y] = GEM.valueOf("BAG");
				else if (value < -2800000000l)
					result[x][y] = GEM.valueOf("COPPER");
				else if (value < -2500000000l)
					result[x][y] = GEM.valueOf("GREEN");
				else if (value < -1300000000l)
					result[x][y] = GEM.valueOf("GOLD");
				else
					System.out.println("Found RGB Value outside of range.");
			}
		}

		return result;
	}

	private static long[][] extractRGB(BufferedImage image) {
		long[][] values = new long[8][8];
		long currentValue = 0;

		for (int x = Utils.X_START + 1 + Utils.TILE_WIDTH / 2; x < Utils.X_END; x+= (Utils.TILE_WIDTH + 1)) {
			for (int y = Utils.Y_START + 1 + Utils.TILE_WIDTH / 2; y < Utils.Y_END; y+= (Utils.TILE_WIDTH + 1)) {
				for (int a = x - Utils.SEARCH_WIDTH; a <= x + Utils.SEARCH_WIDTH; a++)
					for (int b = y - Utils.SEARCH_WIDTH; b <= y + Utils.SEARCH_WIDTH; b++) {
						currentValue += image.getRGB(a, b);
					}
				values[(x - Utils.X_START) / Utils.TILE_WIDTH][(y - Utils.Y_START) / Utils.TILE_WIDTH] = currentValue;
				currentValue = 0;
			}
		}
		return values;
	}
}	
