package com.spireprod.cje.core.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;

public class TextUI {
	public int selectedIndex = 0;
	public int totalItems = 0;

	public void reset() {
		totalItems = 0;
	}

	public boolean button(String label, ConsoleRenderer renderer, Input input, int x, int y) {
		boolean selected = (selectedIndex == totalItems);
		String display = selected ? "> " + label : label;

		TextColor.ANSI color = selected ? TextColor.ANSI.GREEN : TextColor.ANSI.WHITE;

		renderer.writeString(display, x, y, TextColor.ANSI.BLACK, color);

		totalItems++;
		return selected && input.isKeyDown(KeyType.Enter);
	}

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

}
