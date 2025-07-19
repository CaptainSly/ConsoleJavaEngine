package com.spireprod.cje.core.scenes;

import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.core.ConsoleRenderer;

public class SceneManager {

	private Scene currentScene;

	public SceneManager() {
	}

	public void sceneUpdate(float deltaTime) {
		if (currentScene != null)
			currentScene.onSceneUpdate(deltaTime);
	}

	public void sceneInput(float deltaTime, KeyStroke key) {
		if (currentScene != null)
			currentScene.onSceneInput(deltaTime, key);
	}

	public void sceneRender(ConsoleRenderer renderer) {
		if (currentScene != null)
			currentScene.onSceneRender(renderer);
	}
	
	public void setScene(Scene scene) {
		currentScene = scene;
	}

}
