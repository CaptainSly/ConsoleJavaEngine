package com.spireprod.cje.util;

/**
 * The timer class is used for calculating delta time and also FPS and UPS
 * calculation.
 *
 * @author Heiko Brumme
 */
public class Timer {

	/**
	 * System time since last loop in nanoseconds.
	 */
	private long lastLoopTime;
	/**
	 * Used for FPS and UPS calculation.
	 */
	private float timeCount;
	/**
	 * Frames per second.
	 */
	private int fps;
	/**
	 * Counter for the FPS calculation.
	 */
	private int fpsCount;
	/**
	 * Updates per second.
	 */
	private int ups;
	/**
	 * Counter for the UPS calculation.
	 */
	private int upsCount;
	/**
	 * Nanoseconds to seconds conversion factor.
	 */
	private static final double NANO_TO_SECONDS = 1.0 / 1_000_000_000.0;

	/**
	 * Initializes the timer.
	 */
	public void init() {
		lastLoopTime = System.nanoTime();
	}

	/**
	 * Returns the current system time in seconds.
	 *
	 * @return System time in seconds
	 */
	public double getTime() {
		return System.nanoTime() * NANO_TO_SECONDS;
	}

	/**
	 * Returns the time that has passed since the last loop.
	 *
	 * @return Delta time in seconds
	 */
	public float getDelta() {
		long time = System.nanoTime();
		float delta = (float) ((time - lastLoopTime) * NANO_TO_SECONDS);
		lastLoopTime = time;
		timeCount += delta;
		return delta;
	}

	/**
	 * Updates the FPS counter.
	 */
	public void updateFPS() {
		fpsCount++;
	}

	/**
	 * Updates the UPS counter.
	 */
	public void updateUPS() {
		upsCount++;
	}

	/**
	 * Updates FPS and UPS if a whole second has passed.
	 */
	public void update() {
		if (timeCount > 1f) {
			fps = fpsCount;
			fpsCount = 0;

			ups = upsCount;
			upsCount = 0;

			timeCount -= 1f;
		}
	}

	/**
	 * Getter for the FPS.
	 *
	 * @return Frames per second
	 */
	public int getFPS() {
		return fps > 0 ? fps : fpsCount;
	}

	/**
	 * Getter for the UPS.
	 *
	 * @return Updates per second
	 */
	public int getUPS() {
		return ups > 0 ? ups : upsCount;
	}

	/**
	 * Getter for the last loop time in seconds.
	 *
	 * @return System time of the last loop
	 */
	public double getLastLoopTime() {
		return lastLoopTime * NANO_TO_SECONDS;
	}
}
