package com.spireprod.cje.core.scenes;

import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;

public interface Scene {

	void onSceneUpdate(float delta);

	void onSceneRender(ConsoleRenderer renderer);

	void onSceneInput(float delta, Input input);

}
