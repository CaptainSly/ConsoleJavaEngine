package com.spireprod.cje.core.scenes;

import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.ui.TextUI;

public class SceneManager {

	private Scene currentScene;

	public SceneManager() {
	}

	public void sceneUpdate(float deltaTime) {
		if (currentScene != null)
			currentScene.onSceneUpdate(deltaTime);
	}

	public void sceneInput(float deltaTime, Input input) {
		if (currentScene != null)
			currentScene.onSceneInput(deltaTime, input);
	}

	public void sceneUIRender(TextUI ctx, ConsoleRenderer renderer, Input input) {
		if (currentScene != null)
			currentScene.onSceneUIRender(ctx, renderer, input);
	}

	public void sceneRender(ConsoleRenderer renderer) {
		if (currentScene != null)
			currentScene.onSceneRender(renderer);
	}

	public void setScene(Scene scene) {
		currentScene = scene;
	}

}
