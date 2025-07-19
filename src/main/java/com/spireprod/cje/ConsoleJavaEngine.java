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
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

public abstract class ConsoleJavaEngine {

	protected String PIXEL_BLOCK = "\u2588";
	protected String PIXEL_SHADE_FULL = "\u2593";
	protected String PIXEL_SHADE_HALF = "\u2592";
	protected String PIXEL_SHADE = "\u2591";

	protected Terminal terminal;
	protected TextGraphics termGraphics;
	protected KeyStroke termKey;
	protected Screen screen;
	protected boolean isRunning = false;

	private int termWidth, termHeight;

	private int targetFPS = 60;

	public static final String CJE_VERSION = "0.1.5-Talos";

	public ConsoleJavaEngine(String title, int width, int height) {
		DefaultTerminalFactory defaultTermFactory = new DefaultTerminalFactory();
		defaultTermFactory.setTerminalEmulatorTitle(title);
		defaultTermFactory.setInitialTerminalSize(new TerminalSize(width, height));

		try {
			terminal = defaultTermFactory.createTerminal();
			screen = new TerminalScreen(terminal);

			this.termWidth = terminal.getTerminalSize().getColumns();
			this.termHeight = terminal.getTerminalSize().getRows();

			this.termGraphics = screen.newTextGraphics();

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

	protected abstract void onGameRender(float alpha);

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

		screen.startScreen();
		screen.setCursorPosition(null);

		final float targetDelta = 1f / targetFPS;
		float accumulator = 0f;
		long lastTime = System.nanoTime();

		while (isRunning) {
			long now = System.nanoTime();
			float deltaTime = (now - lastTime) / 1E9f;
			lastTime = now;

			TerminalSize newSize = screen.doResizeIfNecessary();
			if (newSize != null) {
				this.termWidth = newSize.getColumns();
				this.termHeight = newSize.getRows();
			}

			accumulator += deltaTime;

			while (accumulator >= targetDelta) {
				termKey = screen.pollInput();

				if (termKey != null && termKey.getKeyType().equals(KeyType.Escape))
					isRunning = false;

				if (termKey != null)
					onGameInput(targetDelta, termKey);

				onGameUpdate(targetDelta);

				accumulator -= targetDelta;
			}

			onGameRender(accumulator / targetDelta);
			screen.refresh();
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
