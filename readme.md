# Console Java Engine - Version 0.1.10-Talos

The Console Java Engine is based off of JavidX9's PixelGameEngine.

Uses Lanterna to emulate a terminal, also comes with a Pair class that is a basic copy of C++s pair class.


## How to use

To use CJE, include it in your project, then create a class and extend the `ConsoleJavaEngine`. It provides a Skeleton for your project and also provides protected methods to write to the screen. 

```Java

import com.googlecode.lanterna.input.KeyStroke;
import com.spireprod.cje.ConsoleJavaEngine;

public class Adventure extends ConsoleJavaEngine {

	public Adventure() {
		super("Adventure", 80, 24);
	}

	@Override
	protected void onGameCreate() {

	}

	@Override
	protected void onGameUpdate(float deltaTime) {

	}

	@Override
	protected void onGameInput(float deltaTime, KeyStroke keyStroke) {

	}

	@Override
	protected void onGameRender(float alpha) {

	}

	public static void main(String[] args) {
		Adventure venture = new Adventure();
		venture.run();
	}

}

```


### Jitpack
[![](https://jitpack.io/v/CaptainSly/ConsoleJavaEngine.svg)](https://jitpack.io/#CaptainSly/ConsoleJavaEngine)

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