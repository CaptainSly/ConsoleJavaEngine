package com.spireprod.cje.core.input;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class Input {

	private final Set<KeyStroke> currentFrameKeys = new HashSet<>();
	private final Set<KeyStroke> lastFrameKeys = new HashSet<>();
	private boolean isNewFrame = false;

	public void update(KeyStroke stroke) {
		if (stroke != null) {
			currentFrameKeys.add(stroke);
		}
	}

	public void beginFrame() {
		lastFrameKeys.clear();
		lastFrameKeys.addAll(currentFrameKeys);
		currentFrameKeys.clear();
		isNewFrame = true;
	}

	public void endFrame() {
		isNewFrame = false;
	}

	public boolean isKeyDown(KeyType type) {
		return currentFrameKeys.stream().anyMatch(k -> k.getKeyType() == type);
	}

	public boolean isKeyDown(char c) {
		return currentFrameKeys.stream().anyMatch(
				k -> k.getKeyType() == KeyType.Character && k.getCharacter() != null && k.getCharacter() == c);
	}

	public boolean wasKeyDown(KeyType type) {
		if (!isNewFrame)
			throw new IllegalStateException("Are we between a beginFrame and endFrame call?");

		boolean isDownNow = currentFrameKeys.stream().anyMatch(k -> k.getKeyType() == type);
		boolean wasDownLast = lastFrameKeys.stream().anyMatch(k -> k.getKeyType() == type);

		return isDownNow && !wasDownLast;
	}

	public boolean wasKeyDown(char c) {
		if (!isNewFrame)
			throw new IllegalStateException("Are we between a beginFrame and endFrame call?");

		boolean isDownNow = currentFrameKeys.stream().anyMatch(k -> k.getCharacter() == c);
		boolean wasDownLast = lastFrameKeys.stream().anyMatch(k -> k.getCharacter() == c);

		return isDownNow && !wasDownLast;
	}

	public Set<KeyStroke> getCurrentKeys() {
		return currentFrameKeys;
	}

}
