package com.ename.diogo.martins.survival.Screens;

import UI.MainMenu_Interface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ename.diogo.martins.survival.AppController;

public class MainMenu_Screen implements Screen {

	Stage stage;
	InputMultiplexer inputMultiplexer;	
	MainMenu_Interface ui;
	AppController appCtrl;
	
	public MainMenu_Screen(AppController appController, InputMultiplexer multiplexer) {
		appCtrl=appController;
		inputMultiplexer=multiplexer;
		ui= new MainMenu_Interface(appCtrl);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();		
		stage.draw();
		Table.drawDebug(stage);

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

	}

	@Override
	public void show() {
		//	Gdx.graphics.setContinuousRendering(true);
		Gdx.input.setCatchBackKey(false);
		if(stage!=null)
			stage.dispose();
		//TODO: Don't just dispose of the stage....
		stage=ui.getMainMenuInterface();
		inputMultiplexer.addProcessor(stage);

	}

	@Override
	public void hide() {
		inputMultiplexer.removeProcessor(stage);

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		if(stage!=null)
			stage.dispose();
	}

}
