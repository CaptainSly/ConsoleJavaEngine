package com.spireprod.cje.core.scenes;

import com.spireprod.cje.ConsoleJavaEngine;

public abstract class AbstractScene implements Scene {

	protected ConsoleJavaEngine cje;

	public AbstractScene(ConsoleJavaEngine cje) {
		this.cje = cje;
	}

}
