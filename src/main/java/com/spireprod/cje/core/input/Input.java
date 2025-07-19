package com.spireprod.cje.core.input;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Input {

	private final Set<KeyStroke> currentKeys = new HashSet<>();

	public void update(KeyStroke stroke) {
		if (stroke != null) {
			currentKeys.add(stroke);
		}
	}

	public void endFrame() {
		currentKeys.clear();
	}

	public boolean isKeyDown(KeyType type) {
		return currentKeys.stream().anyMatch(k -> k.getKeyType() == type);
	}

	public boolean isKeyDown(char c) {
		return currentKeys.stream().anyMatch(
				k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c);
	}

}
