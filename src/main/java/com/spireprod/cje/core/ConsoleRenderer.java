package com.spireprod.cje.core;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

public class ConsoleRenderer {

	private TextGraphics graphics;
	
	public ConsoleRenderer(TextGraphics graphics) {
		this.graphics = graphics;
	}

	public void clearScreen() {
		graphics.setBackgroundColor(TextColor.ANSI.BLACK);
		graphics.fill(' ');
	}

	public void writeString(char str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		graphics.setBackgroundColor(backgroundColor);
		graphics.setForegroundColor(textColor);
		graphics.setCharacter(x, y, str);
	}

	public void writeString(char str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(char str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor, TextColor textColor) {
		graphics.setBackgroundColor(backgroundColor);
		graphics.setForegroundColor(textColor);
		graphics.putString(x, y, str);
	}

	public void writeString(String str, int x, int y, TextColor backgroundColor) {
		writeString(str, x, y, backgroundColor, TextColor.ANSI.WHITE);
	}

	public void writeString(String str, int x, int y) {
		writeString(str, x, y, TextColor.ANSI.BLACK, TextColor.ANSI.WHITE);
	}

}
