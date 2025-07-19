package com.spireprod.cje.core.ui;

import com.googlecode.lanterna.input.KeyType;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;

public class TextUI {
	public int selectedIndex = 0;
	public int totalItems = 0;

	public void reset() {
		totalItems = 0;
	}

	public boolean selectable(String label, ConsoleRenderer renderer, Input input, int x, int y) {
		boolean selected = (selectedIndex == totalItems);
		String display = selected ? "> " + label : "  " + label;

		renderer.writeString(display, x, y);

		totalItems++;
		return selected && input.isKeyPressed(KeyType.Enter);
	}

	public void handleInput(Input input) {
		if (input.isKeyPressed(KeyType.ArrowUp))
			selectedIndex--;
		if (input.isKeyPressed(KeyType.ArrowDown))
			selectedIndex++;

		selectedIndex = Math.floorMod(selectedIndex, totalItems);
	}
}
