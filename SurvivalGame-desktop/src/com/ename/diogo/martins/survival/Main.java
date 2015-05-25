package com.ename.diogo.martins.survival;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SurvivalGame";
		//cfg.useGL20 = false;
		cfg.width = 320;
		cfg.height = 480;
		cfg.resizable=true;
		new LwjglApplication(new AppController(), cfg);
		Game_State.setLanguage("pt");
	}
}
