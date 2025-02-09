package com.spireprod.cje;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.spireprod.cje.util.Timer;

public abstract class ConsoleJavaEngine {

	protected String PIXEL_BLOCK = "\u2588";
	protected String PIXEL_SHADE_FULL = "\u2593";
	protected String PIXEL_SHADE_HALF = "\u2592";
	protected String PIXEL_SHADE = "\u2591";

	protected Terminal terminal;
	protected Screen screen;
	protected Timer timer;
	protected boolean isRunning = false;

	private int termWidth, termHeight;

	private int targetFPS = 60;

	public static final String CJE_VERSION = "0.1.5-Talos";

	public ConsoleJavaEngine(String title, int width, int height) {
		DefaultTerminalFactory defaultTermFactory = new DefaultTerminalFactory();
		defaultTermFactory.setTerminalEmulatorTitle(title);
		defaultTermFactory.setInitialTerminalSize(new TerminalSize(width, height));

		timer = new Timer();

		try {
			terminal = defaultTermFactory.createTerminal();
			screen = new TerminalScreen(terminal);

			this.termWidth = terminal.getTerminalSize().getColumns();
			this.termHeight = terminal.getTerminalSize().getRows();

			terminal.addResizeListener(new TerminalResizeListener() {

				@Override
				public void onResized(Terminal terminal, TerminalSize newSize) {
					termWidth = newSize.getColumns();
					termHeight = newSize.getRows();
				}

			});

			((SwingTerminalFrame) terminal).addWindowListener(new WindowListener() {

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
		try {
			loop();
			screen.close();
			terminal.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loop() throws IOException {

		final long optimalTime = (long) (1e9 / targetFPS);

		screen.startScreen();
		screen.setCursorPosition(null);

		while (isRunning) {
			long startTime = System.nanoTime();

			float deltaTime = timer.getDelta();
			timer.update();

			screen.refresh();
			KeyStroke keyStroke = screen.pollInput();

			if (keyStroke != null && keyStroke.getKeyType().equals(KeyType.Escape))
				isRunning = false;

			if (keyStroke != null)
				onGameInput(deltaTime, keyStroke);

			onGameUpdate(deltaTime);
			timer.updateUPS();

			TerminalSize newSize = screen.doResizeIfNecessary();
			if (newSize != null) {
				this.termWidth = newSize.getColumns();
				this.termHeight = newSize.getRows();
			}

			onGameRender(deltaTime);
			timer.updateFPS();

			long elapsedTime = System.nanoTime() - startTime;
			long sleepTime = optimalTime - elapsedTime;

			if (sleepTime > 0) {
				try {
					Thread.sleep((long) (sleepTime / 1e6));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		screen.stopScreen();
	}

	// -----------------------------------
	// Getters and Setters

	public int getTerminalWidth() {
		return termWidth;
	}

	public int getTerminalHeight() {
		return termHeight;
	}

	public void setTargetFps(int fps) {
		this.targetFPS = fps;
	}
	
	// -----------------------------------
	// Drawing Methods

	public void writeString(char str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		TextGraphics textGraphics = screen.newTextGraphics();
		textGraphics.setBackgroundColor(backgroundColor);
		textGraphics.setForegroundColor(textColor);
		textGraphics.setCharacter(x, y, str);
	}

	public void writeString(char str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(char str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		TextGraphics textGraphics = screen.newTextGraphics();
		textGraphics.setBackgroundColor(backgroundColor);
		textGraphics.setForegroundColor(textColor);
		textGraphics.putString(x, y, str);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(String str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE);
	}

}
