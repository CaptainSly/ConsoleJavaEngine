package test.spireprod.cje;

import java.util.ArrayList;

import com.googlecode.lanterna.TextColor.ANSI;
import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.ConsoleJavaEngine;
import com.spireprod.cje.util.Pair;

public class ConsoleFPS extends ConsoleJavaEngine {

	private int mapWidth = 16; // World Dimensions
	private int mapHeight = 16;
	private float playerX = 14.7f; // Player Start Position
	private float playerY = 5.09f;
	private float playerA = 0.0f; // Player Start Rotation
	private float FOV = 3.14159f / 4.0f; // Field of View
	private float depth = 16.0f; // Maximum rendering distance
	private float speed = 5.0f;

	String[] map = { 
			"################", 
			"#..............#", 
			"#.......##.#####", 
			"#.......#......#", 
			"#.......#......#",
			"#########......#",
			"#..............#", 
			"#..............#", 
			"#..............#", 
			"#......####..###",
			"#......#.......#", 
			"#####..#.......#", 
			"#......#.......#", 
			"#...#..#########", 
			"#...#..........#",
			"################" };

	public ConsoleFPS() {
		super("ConsoleFps", 120, 40);
		System.out.println(getTerminalWidth());
	}

	@Override
	public void onGameCreate() {
		setTargetFps(144);
	}

	@Override
	public void onGameUpdate(float deltaTime) {

	}

	@Override
	protected void onGameInput(float deltaTime, KeyStroke keyStroke) {
		if (keyStroke.getCharacter() == null)
			return;

		Character key = keyStroke.getCharacter();

		if (key.equals('a'))
			playerA -= (speed * 0.75f) * deltaTime;

		// Handle CW Rotation
		if (key.equals('d'))
			playerA += (speed * 0.75f) * deltaTime;

		// Handle Forwards movement & collision
		if (key.equals('w')) {
			playerX += Math.sin(playerA) * speed * deltaTime;
			playerY += Math.cos(playerA) * speed * deltaTime;
			if (map[(int) playerX].charAt((int) playerY) == '#') {
				playerX -= Math.sin(playerA) * speed * deltaTime;
				playerY -= Math.cos(playerA) * speed * deltaTime;
			}
		}

		// Handle backwards movement & collision
		if (key.equals('s')) {
			playerX -= Math.sin(playerA) * speed * deltaTime;
			playerY -= Math.cos(playerA) * speed * deltaTime;
			if (map[(int) playerX].charAt((int) playerY) == '#') {
				playerX += Math.sin(playerA) * speed * deltaTime;
				playerY += Math.cos(playerA) * speed * deltaTime;
			}
		}
	}

	@Override
	public void onGameRender(float deltaTime) {
		for (int x = 0; x < getTerminalWidth(); x++) {
			// For each column, calculate the projected ray angle into world space
			float rayAngle = (playerA - FOV / 2.0f) + ((float) x / (float) getTerminalWidth()) * FOV;

			// Find distance to wall
			float stepSize = 0.1f; // Increment size for ray casting, decrease to increase
			float distanceToWall = 0.0f; // resolution

			boolean hitWall = false; // Set when ray hits wall block
			boolean boundary = false; // Set when ray hits boundary between two wall blocks

			float eyeX = (float) Math.sin(rayAngle); // Unit vector for ray in player space
			float eyeY = (float) Math.cos(rayAngle);

			// Incrementally cast ray from player, along ray angle, testing for
			// intersection with a block
			while (!hitWall && distanceToWall < depth) {
				distanceToWall += stepSize;
				int testX = (int) (playerX + eyeX * distanceToWall);
				int testY = (int) (playerY + eyeY * distanceToWall);

				// Test if ray is out of bounds
				if (testX < 0 || testX >= mapWidth || testY < 0 || testY >= mapHeight) {
					hitWall = true; // Just set distance to maximum depth
					distanceToWall = depth;
				} else {
					// Ray is inbounds so test to see if the ray cell is a wall block
					// Ray is inbounds, so test to see if the ray cell is a wall block
					if (map[testX].charAt(testY) == '#') {
						// Ray has hit wall
						hitWall = true;

						// To highlight tile boundaries, cast a ray from each corner
						// of the tile, to the player. The more coincident this ray
						// is to the rendering ray, the closer we are to a tile
						// boundary, which we'll shade to add detail to the walls
						ArrayList<Pair<Float, Float>> p = new ArrayList<>();

						// Test each corner of the hit tile, storing the distance from
						// the player, and the calculated dot product of the two rays
						for (int tx = 0; tx < 2; tx++) {
							for (int ty = 0; ty < 2; ty++) {
								// Angle of corner to eye
								float vy = (float) testY + ty - playerY;
								float vx = (float) testX + tx - playerX;
								float d = (float) Math.sqrt(vx * vx + vy * vy);
								float dot = (eyeX * vx / d) + (eyeY * vy / d);
								p.add(new Pair<>(d, dot));
							}
						}

						// Sort Pairs from closest to farthest
						p.sort((left, right) -> Float.compare(left.first, right.first));

						// First two/three are closest (we will never see all four)
						float fBound = 0.01f;
						if (Math.acos(p.get(0).second) < fBound)
							boundary = true;
						if (Math.acos(p.get(1).second) < fBound)
							boundary = true;
						if (Math.acos(p.get(2).second) < fBound)
							boundary = true;
					}

				}
			}

			// Calculate distance to ceiling and floor
			int nCeiling = (int) ((float) (getTerminalHeight() / 2.0) - getTerminalHeight() / ((float) distanceToWall));
			int nFloor = getTerminalHeight() - nCeiling;

			// Shader walls based on distance
			String nShade = " ";
			if (distanceToWall <= depth / 4.0f)
				nShade = PIXEL_BLOCK; // Very close
			else if (distanceToWall < depth / 3.0f)
				nShade = PIXEL_SHADE_FULL;
			else if (distanceToWall < depth / 2.0f)
				nShade = PIXEL_SHADE_HALF;
			else if (distanceToWall < depth)
				nShade = PIXEL_SHADE;
			else
				nShade = " "; // Too far away

			if (boundary)
				nShade = " "; // Black it out

			for (int y = 0; y < getTerminalHeight(); y++) {
				// Each Row
				if (y <= nCeiling)
					writeString("~", x, y, ANSI.BLUE_BRIGHT, ANSI.BLUE);
				else if (y > nCeiling && y <= nFloor)
					writeString(nShade, x, y, ANSI.GREEN_BRIGHT, ANSI.GREEN);
				else // Floor
				{
					// Shade floor based on distance
					float b = 1.0f - (((float) y - getTerminalHeight() / 2.0f) / ((float) getTerminalHeight() / 2.0f));
					if (b < 0.25)
						nShade = "#";
					else if (b < 0.5)
						nShade = "x";
					else if (b < 0.75)
						nShade = ".";
					else if (b < 0.9)
						nShade = "-";
					else
						nShade = " ";
					writeString(nShade, x, y, ANSI.RED_BRIGHT, ANSI.RED);
				}

			}
		}

		writeString("[X: " + playerX + ", Y: " + playerY + ", A: " + playerA + "] - FPS: " + (1.0f / deltaTime), 40, 0,
				ANSI.WHITE, ANSI.BLACK);

		// Display Map
		for (int my = 0; my < mapHeight; my++) {
			for (int mx = 0; mx < mapWidth; mx++) {
				char tile = map[my].charAt(mx); // Access individual characters
				writeString(tile, mx + 1, my + 1, ANSI.BLUE, ANSI.WHITE);
			}
			System.out.println();
		}
		writeString("P", (int) playerY + 1, (int) playerX + 1, ANSI.BLUE, ANSI.WHITE);

	}

}
