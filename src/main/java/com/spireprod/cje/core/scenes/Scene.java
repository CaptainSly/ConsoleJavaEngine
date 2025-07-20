package com.spireprod.cje.core.scenes;

import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.ui.UIContext;

public interface Scene {

	void onSceneUpdate(float delta);

	void onSceneRender(ConsoleRenderer renderer);

	void onSceneUIRender(UIContext ctx, ConsoleRenderer renderer, Input input);
	
	void onSceneInput(float delta, Input input);

}
