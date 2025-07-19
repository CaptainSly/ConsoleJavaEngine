package com.spireprod.cje;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.scenes.Scene;
import com.spireprod.cje.core.scenes.SceneManager;

public abstract class ConsoleJavaEngine {

	protected String PIXEL_BLOCK = "\u2588";
	protected String PIXEL_SHADE = "\u2591";
	protected String PIXEL_SHADE_FULL = "\u2593";
	protected String PIXEL_SHADE_HALF = "\u2592";

	protected Terminal terminal;
	protected SceneManager sceneManager;
	protected ConsoleRenderer renderer;
	protected Input input;
	protected SwingTerminalFrame frame;
	protected Screen screen;
	protected boolean isRunning = false;

	private int termWidth, termHeight;

	private final int targetFPS = 60;
	private final long optimalTime = (long) (1E9f / targetFPS);

	public static final String CJE_VERSION = "0.1.15-Jyggalag";

	public ConsoleJavaEngine(String title, int width, int height) {
		DefaultTerminalFactory defaultTermFactory = new DefaultTerminalFactory();
		defaultTermFactory.setTerminalEmulatorTitle(title + " -" + CJE_VERSION)
				.setInitialTerminalSize(new TerminalSize(width, height)).setForceAWTOverSwing(false)
				.setPreferTerminalEmulator(true);

		try {
			terminal = defaultTermFactory.createSwingTerminal();
			frame = (SwingTerminalFrame) terminal;
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);

			screen = new TerminalScreen(terminal);
			termWidth = frame.getTerminalSize().getColumns();
			termHeight = frame.getTerminalSize().getRows();

			input = new Input();
			sceneManager = new SceneManager();
			renderer = new ConsoleRenderer(screen.newTextGraphics());

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

	private void onGameUpdate(float deltaTime) {
		sceneManager.sceneUpdate(deltaTime);
	}

	private void onGameInput(float deltaTime, Input input) {
		sceneManager.sceneInput(deltaTime, input);
	}

	private void onGameUIRender(ConsoleRenderer renderer, Input input) {
		sceneManager.sceneUIRender(renderer, input);
	}

	private void onGameRender(ConsoleRenderer renderer) {
		sceneManager.sceneRender(renderer);
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

			KeyStroke termKey;
			while ((termKey = screen.pollInput()) != null)
				input.update(termKey);

			onGameInput(delta, input);

			onGameUpdate(delta);

			renderer.setTextGraphics(screen.newTextGraphics());
			renderer.clearScreen();

			onGameRender(renderer);
			onGameUIRender(renderer, input);

			screen.refresh();
			input.endFrame(); // Moved Here for UI Input

			long sleepNanos = (lastLoopTime - System.nanoTime() + optimalTime);
			if (sleepNanos > 0) {
				long millis = sleepNanos / 1_000_000;
				int nanos = (int) (sleepNanos % 1_000_000);
				Thread.sleep(millis, nanos);
			}
		}

		screen.stopScreen();
	}

	protected void setScene(Scene scene) {
		sceneManager.setScene(scene);
	}

	// -----------------------------------
	// Getters and Setters

	public int getTerminalWidth() {
		return termWidth;
	}

	public int getTerminalHeight() {
		return termHeight;
	}

}
