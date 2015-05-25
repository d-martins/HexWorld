package com.ename.diogo.martins.survival.Screens;

import UI.Item_Interface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ename.diogo.martins.survival.AppController;
import com.ename.diogo.martins.survival.Game_State;

public class Item_Screen implements Screen{
	private static String TAG="ITEM_SCREEN" ;
	InputMultiplexer inputMultiplexer;	
	Stage stage;
	Item_Interface ui;
	Game_State gameState;
	AppController appCtrl;
	
	public Item_Screen(AppController appController, InputMultiplexer multiplexer){
		inputMultiplexer=multiplexer;
		appCtrl=appController;
		gameState=appCtrl.getGameState();
		ui=new Item_Interface(appController);
		
	}
	
	@Override
	public void render(float delta) {		
		ui.UpdateItemsUI();
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
		Gdx.app.log(TAG, "Item_Show");
	//	Gdx.graphics.setContinuousRendering(true);
		if(stage!=null)
			stage.dispose();
		//TODO: Don't just dispose of the stage....
		stage=ui.getItemUserInterface();
		inputMultiplexer.addProcessor(stage);
	}

	@Override
	public void hide() {
			inputMultiplexer.removeProcessor(stage);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		if(stage!=null)
			stage.dispose();
	}

}
