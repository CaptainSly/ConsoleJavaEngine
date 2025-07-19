package com.spireprod.cje.core.input;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Input {

	private final Set<KeyStroke> currentKeys = new HashSet<>();
	private final Set<KeyStroke> previousKeys = new HashSet<>();

	public void update(KeyStroke stroke) {
		if (stroke != null) {
			currentKeys.add(normalize(stroke));
		}
	}

	public void endFrame() {
		previousKeys.clear();
		previousKeys.addAll(currentKeys);
		currentKeys.clear();
	}

	// Check if key is currently held down
	public boolean isKeyDown(KeyType type) {
		return previousKeys.stream().anyMatch(k -> k.getKeyType() == type);
	}

	public boolean isKeyDown(char c) {
		return previousKeys.stream().anyMatch(
				k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c);
	}

	// Check if key was just pressed this frame
	public boolean isKeyPressed(KeyType type) {
		return currentKeys.stream().anyMatch(k -> k.getKeyType() == type)
				&& previousKeys.stream().noneMatch(k -> k.getKeyType() == type);
	}

	public boolean isKeyPressed(char c) {
		return currentKeys.stream()
				.anyMatch(k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c)
				&& previousKeys.stream().noneMatch(
						k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c);
	}

	// Check if key was released this frame
	public boolean isKeyReleased(KeyType type) {
		return previousKeys.stream().anyMatch(k -> k.getKeyType() == type)
				&& currentKeys.stream().noneMatch(k -> k.getKeyType() == type);
	}

	public boolean isKeyReleased(char c) {
		return previousKeys.stream()
				.anyMatch(k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c)
				&& currentKeys.stream().noneMatch(
						k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c);
	}

	// Normalize to avoid issues with Shift+char, etc.
	private KeyStroke normalize(KeyStroke stroke) {
		// Only consider key type + char, ignore modifiers for now
		if (stroke.getKeyType() == KeyType.Character && stroke.getCharacter() != null) {
			return new KeyStroke(Character.toLowerCase(stroke.getCharacter()), false, false);
		}
		return new KeyStroke(stroke.getKeyType());
	}
}
