package com.spireprod.cje.core;

public class Camera {
	private float x, y; // top-left world coordinate of the view
	private final int viewWidth, viewHeight;

	// Panning
	private float targetX, targetY;
	private float panSpeed = 0;
	private boolean isPanning = false;

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
	}

	public int getViewX() {
		return (int) x;
	}

	public int getViewY() {
		return (int) y;
	}

	public int worldToScreenX(int worldX) {
		return worldX - getViewX();
	}

	public int worldToScreenY(int worldY) {
		return worldY - getViewY();
	}

	private float lerp(float a, float b, float t) {
		return a + (b - a) * clamp(t, 0f, 1f);
	}

	private float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
