package com.spireprod.cje.core;

import java.util.Random;

public class Camera {
	private float x, y; // top-left world coordinate of the view
	private final int viewWidth, viewHeight;

	// Panning
	private float targetX, targetY;
	private float panSpeed = 0;
	private boolean isPanning = false;

	// Shake
	private float shakeTimer = 0;
	private float shakeMagnitude = 0;
	private final Random rng = new Random();

	public Camera(int viewWidth, int viewHeight) {
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
	}

	// Center immediately on a world coordinate
	public void centerOn(float worldX, float worldY) {
		x = worldX - viewWidth / 2f;
		y = worldY - viewHeight / 2f;
		isPanning = false;
	}

	// Smoothly pan to a world position (centered)
	public void panTo(float worldX, float worldY, float speed) {
		targetX = worldX - viewWidth / 2f;
		targetY = worldY - viewHeight / 2f;
		panSpeed = speed;
		isPanning = true;
	}

	// Shake the camera for a short time
	public void shake(float duration, float magnitude) {
		shakeTimer = duration;
		shakeMagnitude = magnitude;
	}

	public void update(float deltaSeconds) {
		if (Float.isNaN(x) || Float.isNaN(y)) {
			System.err.println("Warning: NaN in camera coordinates. Resetting.");
			x = 0;
			y = 0;
		}

		// Pan logic
		if (isPanning) {
			float t = clamp(deltaSeconds * panSpeed, 0f, 1f);
			x = lerp(x, targetX, t);
			y = lerp(y, targetY, t);

			if (Math.abs(x - targetX) < 0.1f && Math.abs(y - targetY) < 0.1f) {
				x = targetX;
				y = targetY;
				isPanning = false;
			}
		}

		// Shake logic
		if (shakeTimer > 0) {
			shakeTimer -= deltaSeconds;
		}
	}

	public int getViewX() {
		return (int) x + getShakeOffset();
	}

	public int getViewY() {
		return (int) y + getShakeOffset();
	}

	public int worldToScreenX(int worldX) {
		return worldX - getViewX();
	}

	public int worldToScreenY(int worldY) {
		return worldY - getViewY();
	}

	private int getShakeOffset() {
		return (shakeTimer > 0) ? rng.nextInt((int) (shakeMagnitude * 2 + 1)) - (int) shakeMagnitude : 0;
	}

	private float lerp(float a, float b, float t) {
		return a + (b - a) * clamp(t, 0f, 1f);
	}

	private float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
