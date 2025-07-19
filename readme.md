# Console Java Engine - Version 0.1.15-Jyggalag
[![](https://jitpack.io/v/CaptainSly/ConsoleJavaEngine.svg)](https://jitpack.io/#CaptainSly/ConsoleJavaEngine)

The Console Java Engine is based off of JavidX9's PixelGameEngine.

Uses Lanterna to emulate a terminal, also comes with a Pair class that is a basic copy of C++s pair class.

Requires Java 17+

## How to use

To use CJE, include it in your project, then create a class and extend the `ConsoleJavaEngine`. It provides a Skeleton for your project. After that all you need to do is create different `Scene` classes for you game. The `ConsoleRenderer` class holds methods to write to the screen. 

```Java

import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.ConsoleJavaEngine;

public class Adventure extends ConsoleJavaEngine {

	public Adventure() {
		super("Adventure", 80, 24);
	}

	@Override
	protected void onGameCreate() {
		setScene(new GameScreen());
	}

}

import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.scenes.Scene;

public class GameScene implements Scene {

	@Override
	public void onSceneUpdate(float delta) {

	}

	@Override
	public void onSceneRender(ConsoleRenderer renderer) {

	}

	@Override
	public void onSceneInput(float delta, KeyStroke key) {

	}

}


```


### Jitpack

ConsoleJavaEngine is available through [Jitpack](https://jitpack.io). Here's what you want to use:

```gradle

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
	
	dependencies {
	        implementation("com.github.CaptainSly:ConsoleJavaEngine:{VERSION}")
	}
```

## Known Bugs

Currently has a flickering bug.