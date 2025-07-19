package com.spireprod.cje.core.scenes;

import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.core.ConsoleRenderer;

public interface Scene {

	void onSceneUpdate(float delta);

	void onSceneRender(ConsoleRenderer renderer);

	void onSceneInput(float delta, KeyStroke key);

}
