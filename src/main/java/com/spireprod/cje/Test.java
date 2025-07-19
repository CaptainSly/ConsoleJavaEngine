package com.spireprod.cje;

import com.googlecode.lanterna.input.KeyStroke;

public class Test extends ConsoleJavaEngine {

	public Test() {
		super("Test", 80, 24);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onGameCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onGameUpdate(float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onGameInput(float deltaTime, KeyStroke keyStroke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onGameRender(float alpha) {
		writeString("Quack bitch", 5, 10);
	}

	public static void main(String[] args) {
		Test t = new Test();
		t.run();
	}
	
}
