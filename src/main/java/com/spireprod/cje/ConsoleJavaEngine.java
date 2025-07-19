package com.spireprod.cje;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

public abstract class ConsoleJavaEngine {

	protected String PIXEL_BLOCK = "\u2588";
	protected String PIXEL_SHADE = "\u2591";
	protected String PIXEL_SHADE_FULL = "\u2593";
	protected String PIXEL_SHADE_HALF = "\u2592";

	protected Terminal terminal;
	protected TextGraphics termGraphics;
	protected KeyStroke termKey;
	protected SwingTerminalFrame frame;
	protected Screen screen;
	protected boolean isRunning = false;

	private int termWidth, termHeight;

	private final int targetFPS = 60;
	private final long optimalTime = (long) (1E9f / targetFPS);

	public static final String CJE_VERSION = "0.1.10-Talos";

	public ConsoleJavaEngine(String title, int width, int height) {
		DefaultTerminalFactory defaultTermFactory = new DefaultTerminalFactory();
		defaultTermFactory.setTerminalEmulatorTitle(title).setInitialTerminalSize(new TerminalSize(width, height))
				.setForceAWTOverSwing(false).setPreferTerminalEmulator(true);

		try {
			terminal = defaultTermFactory.createSwingTerminal();
			frame = (SwingTerminalFrame) terminal;
			frame.setVisible(true);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			screen = new TerminalScreen(terminal);

			this.termWidth = frame.getTerminalSize().getColumns();
			this.termHeight = frame.getTerminalSize().getRows();

			this.termGraphics = screen.newTextGraphics();

			frame.addResizeListener(new TerminalResizeListener() {

				@Override
				public void onResized(Terminal terminal, TerminalSize newSize) {
					termWidth = newSize.getColumns();
					termHeight = newSize.getRows();
				}

			});

			frame.addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e) {
				}

				@Override
				public void windowClosing(WindowEvent e) {

					isRunning = false;
					try {
						screen.close();
						terminal.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				@Override
				public void windowClosed(WindowEvent e) {
				}

				@Override
				public void windowIconified(WindowEvent e) {
				}

				@Override
				public void windowDeiconified(WindowEvent e) {
				}

				@Override
				public void windowActivated(WindowEvent e) {
				}

				@Override
				public void windowDeactivated(WindowEvent e) {
				}
			});

			onGameCreate();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected abstract void onGameCreate();

	protected abstract void onGameUpdate(float deltaTime);

	protected abstract void onGameInput(float deltaTime, KeyStroke keyStroke);

	protected abstract void onGameRender(float deltaTime);

	public void run() {

		isRunning = true;

		Thread gameLoop = new Thread(() -> {
			try {
				loop();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});

		gameLoop.start();
	}

	private void loop() throws IOException, InterruptedException {
		long lastLoopTime = System.nanoTime();

		screen.startScreen();
		screen.setCursorPosition(null);

		while (isRunning) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;

			float delta = updateLength / ((float) optimalTime);

			TerminalSize newSize = screen.doResizeIfNecessary();
			if (newSize != null) {
				this.termWidth = newSize.getColumns();
				this.termHeight = newSize.getRows();
			}

			termKey = screen.pollInput();

			if (termKey != null && termKey.getKeyType().equals(KeyType.Escape))
				isRunning = false;

			if (termKey != null)
				onGameInput(delta, termKey);

			onGameUpdate(delta);

			onGameRender(delta);
			screen.refresh();

			long sleepNanos = (lastLoopTime - System.nanoTime() + optimalTime);
			if (sleepNanos > 0) {
				long millis = sleepNanos / 1_000_000;
				int nanos = (int) (sleepNanos % 1_000_000);
				Thread.sleep(millis, nanos);
			}
		}

		screen.stopScreen();
	}

//	private void loop() throws IOException {
//
//		screen.startScreen();
//		screen.setCursorPosition(null);
//
//		final float targetDelta = 1f / targetFPS;
//		float accumulator = 0f;
//		long lastTime = System.nanoTime();
//
//		while (isRunning) {
//			long now = System.nanoTime();
//			float deltaTime = (now - lastTime) / 1E9f;
//			lastTime = now;
//

//
//			accumulator += deltaTime;
//
//			while (accumulator >= targetDelta) {

//
//				accumulator -= targetDelta;
//			}
//

//		}
//
//		screen.stopScreen();
//	}

	// -----------------------------------
	// Getters and Setters

	public int getTerminalWidth() {
		return termWidth;
	}

	public int getTerminalHeight() {
		return termHeight;
	}

	// -----------------------------------
	// Drawing Methods

	public void writeString(char str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		termGraphics.setBackgroundColor(backgroundColor);
		termGraphics.setForegroundColor(textColor);
		termGraphics.setCharacter(x, y, str);
	}

	public void writeString(char str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(char str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		termGraphics.setBackgroundColor(backgroundColor);
		termGraphics.setForegroundColor(textColor);
		termGraphics.putString(x, y, str);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(String str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE);
	}

}
