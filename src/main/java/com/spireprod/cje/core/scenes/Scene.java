package com.spireprod.cje.core.scenes;

import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.ui.TextUI;

public interface Scene {

	void onSceneUpdate(float delta);

	void onSceneRender(ConsoleRenderer renderer);

	void onSceneUIRender(TextUI ctx, ConsoleRenderer renderer, Input input);
	
	void onSceneInput(float delta, Input input);

}
