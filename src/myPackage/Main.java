package myPackage;
import java.awt.image.BufferedImage;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Arrays;

import myPackage.GameBoard.Move;
import myPackage.Utils.DIRECTION;
import myPackage.Utils.VALUE;

public class Main {
	
	public static void main(String[] args) throws IOException, AWTException, InterruptedException {
//		String[][] vals = new String[][] {
//			{ "COPPER", "SILVER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"},
//			{ "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER", "COPPER"}
//		};
//		GameBoard g = new GameBoard(vals);
//		g.makeMove(new Move(DIRECTION.D, 0, 1));
//		
//		System.out.println(g);
		Thread.sleep(3000);
		BufferedImage previousImage, image = takeScreenshot(); //ImageIO.read(new File("images\\base.png")); 
		Utils.startNewGame();
		
		while(true) {
			do {
				previousImage = image;
				Thread.sleep(100);
				image = takeScreenshot();
				if (isGameOver(image)) {
					Utils.skipScore();
					Utils.startNewGame();
				}
			} while (hasChanged(previousImage, image));
			
			long[][] values = extractRGB(image);
			GameBoard game = new GameBoard(analyzeRGB(values));
			Move nextMove = game.calculateNextMove();
			Utils.makeMove(nextMove);
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

	private static VALUE[][] analyzeRGB(long[][] values) {
		VALUE[][] result = new VALUE[8][8];
		for (int x = 0; x < values.length; x++) {
			for (int y = 0; y < values.length; y++) {
				long value = values[y][x];
				if (value < -4300000000l)
					result[x][y] = VALUE.valueOf("TREASURE");
				else if (value < -4070000000l)
					result[x][y] = VALUE.valueOf("SILVER");
				else if (value < -3800000000l)
					result[x][y] = VALUE.valueOf("RED");
				else if (value < -3400000000l)
					result[x][y] = VALUE.valueOf("IRON");
				else if (value < -3100000000l)
					result[x][y] = VALUE.valueOf("BAG");
				else if (value < -2800000000l)
					result[x][y] = VALUE.valueOf("COPPER");
				else if (value < -2500000000l)
					result[x][y] = VALUE.valueOf("GREEN");
				else if (value < -1300000000l)
					result[x][y] = VALUE.valueOf("GOLD");
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
