package com.spireprod.cje.core.scenes;

import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;

public interface Scene {

	void onSceneUpdate(float delta);

	void onSceneRender(ConsoleRenderer renderer);

	void onSceneUIRender(ConsoleRenderer renderer, Input input);
	
	void onSceneInput(float delta, Input input);

}
