package com.spireprod.cje;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.scenes.AbstractScene;
import com.spireprod.cje.core.scenes.SceneManager;
import com.spireprod.cje.core.ui.UIContext;

public abstract class ConsoleJavaEngine {

	public static final String PIXEL_BLOCK = "\u2588";
	public static final String PIXEL_SHADE = "\u2591";
	public static final String PIXEL_SHADE_FULL = "\u2593";
	public static final String PIXEL_SHADE_HALF = "\u2592";
	public static int termWidth, termHeight;

	protected Terminal terminal;
	protected SceneManager sceneManager;
	protected UIContext ctx;
	protected ConsoleRenderer renderer;
	protected Input input;
	protected SwingTerminalFrame frame;
	protected Screen screen;
	protected boolean isRunning = false;

	private final int targetFPS = 60;
	private final long optimalTime = (long) (1E9f / targetFPS);

	public static final String CJE_VERSION = "0.1.20-Jyggalag";

	public static String DATA_FOLDER;
	public static final String[] DATA_FLDRS = { "Scripts/", "Saves/", "Maps/", "Config/" };

	public ConsoleJavaEngine(String title, int width, int height) {
		DATA_FOLDER = title.replace(" ", "_").trim() + File.separator;

		// Make Sure Data Folder Exists, if not, create it.
		File dataFolder = new File(DATA_FOLDER);
		if (!dataFolder.exists()) {
			dataFolder.mkdir();

			for (String folder : DATA_FLDRS)
				new File(DATA_FOLDER + folder).mkdirs();

		}

		DefaultTerminalFactory defaultTermFactory = new DefaultTerminalFactory();

		// Setup Default Font 'Ubuntu Mono'
		Font font = null;
		try {
			InputStream is = ConsoleJavaEngine.class.getResourceAsStream("/ascii-sector-16x16-tileset.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, is);
			is.close();
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}

		if (font == null)
			font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);

		defaultTermFactory.setTerminalEmulatorTitle(title + " -" + CJE_VERSION)
				.setTerminalEmulatorFontConfiguration(
						SwingTerminalFontConfiguration.newInstance(font.deriveFont(Font.PLAIN, 16)))
				.setInitialTerminalSize(new TerminalSize(width, height)).setForceAWTOverSwing(false)
				.setPreferTerminalEmulator(true);

		try {
			terminal = defaultTermFactory.createSwingTerminal();
			frame = (SwingTerminalFrame) terminal;
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);

			ctx = new UIContext();
			screen = new TerminalScreen(terminal);
			termWidth = frame.getTerminalSize().getColumns();
			termHeight = frame.getTerminalSize().getRows();

			input = new Input();
			sceneManager = new SceneManager();
			renderer = new ConsoleRenderer(screen.newTextGraphics());

			ctx.setConsoleRenderer(renderer);
			ctx.setInput(input);

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

	public void stop() {
		isRunning = false;
	}

	private void onGameUpdate(float deltaTime) {
		sceneManager.sceneUpdate(deltaTime);
	}

	private void onGameInput(float deltaTime, Input input) {
		sceneManager.sceneInput(deltaTime, input);
	}

	private void onGameUIRender(UIContext ctx, ConsoleRenderer renderer, Input input) {
		sceneManager.sceneUIRender(ctx, renderer, input);
	}

	private void onGameRender(ConsoleRenderer renderer) {
		sceneManager.sceneRender(renderer);
	}

	private void loop() throws IOException, InterruptedException {
		long lastLoopTime = System.nanoTime();

		screen.startScreen();
		screen.setCursorPosition(null);

		while (isRunning) {
			input.beginFrame();
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;

			float delta = updateLength / ((float) optimalTime);

			TerminalSize newSize = screen.doResizeIfNecessary();
			if (newSize != null) {
				termWidth = newSize.getColumns();
				termHeight = newSize.getRows();
			}

			KeyStroke termKey;
			while ((termKey = screen.pollInput()) != null)
				input.update(termKey);

			onGameInput(delta, input);

			onGameUpdate(delta);

			renderer.setTextGraphics(screen.newTextGraphics());
			renderer.clearScreen();

			onGameRender(renderer);

			// CJE-IMGUI Stuff
			ctx.reset();
			onGameUIRender(ctx, renderer, input);
			ctx.handleInput(input);

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

	public void setScene(AbstractScene scene) {
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
