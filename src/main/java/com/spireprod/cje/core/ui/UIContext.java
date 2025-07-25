package com.spireprod.cje.core.ui;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import com.spireprod.cje.ConsoleJavaEngine;
import com.spireprod.cje.core.Colors;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;

public class UIContext {
	public int selectedIndex = 0;
	public int totalItems = 0;

	// Panel Offset Stack
	private int offsetX = 0;
	private int offsetY = 0;
	private final Deque<int[]> offsetStack = new ArrayDeque<>(); // Panel OffsetStack

	private ConsoleRenderer renderer;
	private Input input;

	public void setConsoleRenderer(ConsoleRenderer renderer) {
		this.renderer = renderer;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public void reset() {
		totalItems = 0;
	}

	public boolean button(String label, int x, int y, TextColor backgroundColor, TextColor textColor) {
		boolean selected = (selectedIndex == totalItems);
		String display = selected ? "> " + label : label;

		TextColor color = selected ? TextColor.ANSI.GREEN : textColor;

		renderer.writeString(display, offsetX + x, offsetY + y, backgroundColor, color);

		totalItems++;
		return selected && input.isKeyDown(KeyType.Enter);
	}

	public void label(String label, int x, int y, int maxWidth, TextColor backgroundColor, TextColor textColor) {

		String[] words = label.split(" ");
		StringBuilder line = new StringBuilder();
		int lineY = y;

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			// +1 accounts for space between words
			if (line.length() + word.length() + (line.length() > 0 ? 1 : 0) > maxWidth) {
				renderer.writeString(line.toString(), offsetX + x, offsetY + lineY, backgroundColor, textColor);
				line = new StringBuilder(word);
				lineY++;
			} else {
				if (line.length() > 0)
					line.append(" ");
				line.append(word);
			}
		}
		// Render last line
		if (!line.isEmpty()) {
			renderer.writeString(line.toString(), offsetX + x, offsetY + lineY, backgroundColor, textColor);
		}
	}

	public void progressBar(String label, int x, int y, float progress, TextColor backgroundColor,
			TextColor progressColor) {
		progress = Math.max(0f, Math.min(1f, progress)); // Clamp between 0 and 1
		int barWidth = 20;
		int filled = Math.round(progress * barWidth);

		StringBuilder bar = new StringBuilder();
		bar.append(label).append(": [");

		for (int i = 0; i < barWidth; i++) {
			bar.append(i < filled ? ConsoleJavaEngine.PIXEL_BLOCK : ConsoleJavaEngine.PIXEL_SHADE);
		}
		bar.append("]");

		renderer.writeString(bar.toString(), offsetX + x, offsetY + y, backgroundColor, progressColor);
	}

	public boolean textInput(String label, int x, int y, StringBuilder buffer, TextColor backgroundColor,
			TextColor textColor) {
		boolean selected = (selectedIndex == totalItems);

		renderer.writeString(label + ": " + buffer.toString(), offsetX + x, offsetY + y, backgroundColor,
				selected ? TextColor.ANSI.GREEN : textColor);

		totalItems++;

		if (selected) {
			for (var key : input.getCurrentKeys()) {
				if (key.getKeyType() == KeyType.Character && key.getCharacter() != null) {
					buffer.append(key.getCharacter());
				} else if (key.getKeyType() == KeyType.Backspace && buffer.length() > 0) {
					buffer.deleteCharAt(buffer.length() - 1);
				} else if (key.getKeyType() == KeyType.Enter) {
					return true;
				}
			}
		}
		return false;
	}

	public <T> boolean choiceBox(int x, int y, T[] boxObjs, int[] choiceIdx, Function<T, String> labelFunc,
			TextColor backgroundColor, TextColor textColor) {
		boolean fired = false;

		for (int i = 0; i < boxObjs.length; i++) {
			boolean isSelected = (selectedIndex == totalItems);
			boolean choiceSelected = i == choiceIdx[0];

			TextColor color = isSelected ? TextColor.ANSI.GREEN : textColor;
			String prefix = isSelected ? "> " : "  ";

			if (choiceSelected)
				color = Colors.BLUE;

			renderer.writeString(prefix + labelFunc.apply(boxObjs[i]), offsetX + x, offsetY + y + i, backgroundColor,
					color);

			if (isSelected && input.isKeyDown(KeyType.Enter)) {
				fired = true;
				choiceIdx[0] = i;
			}

			totalItems++;
		}

		return fired;
	}

	// region: Panel

	public void beginPanel(int x, int y, int width, int height, TextColor backgroundColor) {
		offsetStack.push(new int[] { offsetX, offsetY });
		offsetX += x;
		offsetY += y;

		// Draw background
		for (int iy = 0; iy < height; iy++) {
			for (int ix = 0; ix < width; ix++)
				renderer.putChar(' ', offsetX + ix, offsetY + iy, null, backgroundColor);
		}

		// Top/Bottom border
		renderer.writeString("+" + buildChainLine(width - 2) + "+", offsetX, offsetY, backgroundColor,
				TextColor.ANSI.WHITE);
		renderer.writeString("+" + buildChainLine(width - 2) + "+", offsetX, offsetY + height - 1, backgroundColor,
				TextColor.ANSI.WHITE);

		// Left/Right border
		char[] sideChain = new char[] { '|', ':' };

		for (int iy = 1; iy < height - 1; iy++) {
			char sideGlyph = sideChain[iy % sideChain.length];
			renderer.putChar(sideGlyph, offsetX, offsetY + iy, TextColor.ANSI.WHITE, backgroundColor);
			renderer.putChar(sideGlyph, offsetX + width - 1, offsetY + iy, TextColor.ANSI.WHITE, backgroundColor);
		}

	}

	public void endPanel() {
		if (!offsetStack.isEmpty()) {
			int[] previous = offsetStack.pop();
			offsetX = previous[0];
			offsetY = previous[1];
		}
	}

	// endregion

	public void handleInput(Input input) {
		if (totalItems == 0) {
			selectedIndex = -1;
			return;
		}

		if (input.isKeyDown(KeyType.ArrowUp))
			selectedIndex--;
		if (input.isKeyDown(KeyType.ArrowDown))
			selectedIndex++;

		selectedIndex = Math.floorMod(selectedIndex, totalItems);
	}

	private String buildChainLine(int length) {
		String pattern = "-=";
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length) {
			sb.append(pattern);
		}
		return sb.substring(0, length);
	}

	public record Choice(Object choice, boolean fired) {
	}

}
