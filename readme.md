# Console Java Engine - Version 0.1.20-Jyggalag
[![](https://jitpack.io/v/CaptainSly/ConsoleJavaEngine.svg)](https://jitpack.io/#CaptainSly/ConsoleJavaEngine)

The Console Java Engine is based off of JavidX9's PixelGameEngine.

Uses Lanterna to emulate a terminal, also comes with a Pair class that is a basic copy of C++s pair class.

Requires Java 17+

## What does it provide? 

If you decide to use Console Java Engine to make your game with, first off thank you for using my project to make yours, secondly you might want to know what it provides. 

As of version 0.1.20-Jyggalag the following is provided:

A Scene system

A Pair class that emulates the C++ Standard Library's. (I thought it'd be nice to have when I first wrote this, this might be removed later)

A Colors class that holds RGB colors. These colors were sourced from the [website](html-color.codes) 

A very basic Camera class. It's really basic camera.

An Immediate Mode UI - I emulated how DearImGui works and currently adding features to the it as I go. 

An Input class - It handles keyboard input. I've been trying to implement a `justPressed` feature, but Lanterna only let's you know a key was pressed and not when it was released, held or pressed. So I might have to dig deeper and develop a separate input system from Lanterna. 

## How to use

Under the hood, Console Java Engine uses Lanterna to create a Swing based Terminal for drawing to. 

To utilize the Console Java Engine in your project, create a class that extends the `ConsoleJavaEngine` class. The Console Java Engine uses scenes so you will also need to create a class that extends `AbstractScene` class. The `AbstractScene` class provides methods for you to override so you can update, render to the screen, render ui, and process input. You can draw to the screen using the provided `ConsoleRenderer` object, and draw UI using the `UIContext` object pass through. `UIContext` uses an Immediate Mode gui system so you're able to create UI kinda like DearIMGui does. 

A Skeleton is provided below for both a class that extends the `ConsoleJavaEngine` and a class that extends `AbstractScene`

The default font included is ascii-sector-16x16-tileset. This means that each cell will also be 16x16 "pixels", if you will, so when you specify you're width and height for creating your game, it'll be multiplied by the font's character width and height. 80, 48 is used here which would translate to a window size of 1280 by 768. This font was chosen because of it's ability to use Code Page 437. By utilizing '\u' then a number, you can use symbols from the code page. 

```Java

import com.spireprod.cje.ConsoleJavaEngine;

public class Adventure extends ConsoleJavaEngine {

	public Adventure() {
		super("Adventure", 80, 48);
	}

	@Override
	protected void onGameCreate() {
		setScene(new GameScene(this));
	}

	public static void main(String[] args) {
		Adventure venture = new Adventure();
		venture.run();
	}

}

import com.spireprod.cje.ConsoleJavaEngine;
import com.spireprod.cje.core.ConsoleRenderer;
import com.spireprod.cje.core.input.Input;
import com.spireprod.cje.core.scenes.AbstractScene;
import com.spireprod.cje.core.ui.UIContext;

public class GameScene extends AbstractScene {

	public GameScene(ConsoleJavaEngine cje) {
		super(cje);
	}

	@Override
	public void onSceneUpdate(float delta) {
	}

	@Override
	public void onSceneRender(ConsoleRenderer renderer) {
	}

	@Override
	public void onSceneUIRender(UIContext ctx, ConsoleRenderer renderer, Input input) {
	}

	@Override
	public void onSceneInput(float delta, Input input) {
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