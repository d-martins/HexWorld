package UI;

import utils.Strings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ename.diogo.martins.survival.AppController;
import com.ename.diogo.martins.survival.Game_State;

public class Map_Interface extends User_Interface {
	
	private static String TAG="MAP_INTERFACE";
	
	ActionBar actions;
	TileInfo tileInfo;
	HUD hud;
	Game_State gameState;
	Dialog exitDialog;
	Dialog nextLevel;
	Dialog gameOver;
	ScoringTable scoreTable;
	Stage stage;
	Label finalPoints; 
	
	public Map_Interface(AppController appController) {
		super(appController);
		gameState= appCtrl.getGameState();
		scoreTable= new ScoringTable(skin);
		// TODO Auto-generated constructor stub
	}
	public void UpdateMapUI(){
		
		tileInfo.updateComponent(gameState);
		actions.updateComponent(gameState);
		hud.updateComponent(gameState);
	}
	
	public Stage getMapUserInterface(){
		stage=new Stage(new ExtendViewport(320,480));	
		stage.addActor(BuildActionBar());
		stage.addActor(BuildPlayerInfoDisplay());
		buildDialogs();
//		stage.addListener(new ClickListener(){
//			@Override
//			public boolean keyDown(InputEvent event, int keycode) {				
//				if(keycode==Keys.BACK){
//					if(exitDialog.isVisible()){
//						exitDialog.hide();
//						exitDialog.setVisible(false);
//					}
//					else{
//						exitDialog.setVisible(true);
//						exitDialog.show(stage);						
//					}
//				}
//				return super.keyDown(event, keycode);
//			}
//		});
		
		return stage;
	}
	
	public void showNextLevelDialog(){
		stage.clear();
		scoreTable.updateComponent(gameState);
		nextLevel.show(stage);
	}
	
	public void showGameOverDialog(){
		stage.clear();
		finalPoints.setText(""+gameState.getPoints());
		gameOver.show(stage);
	}
	
	private Table BuildPlayerInfoDisplay(){		
		Table r=new Table();
		r.setFillParent(true);
		
		hud= new HUD(skin);
		hud.setPortraitListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.getMapScreen().centerOnPlayer();				
			}
		});
		hud.setCharacterPortrait(gameState.getPlayer());
		hud.setPauseListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				exitDialog.setVisible(true);
				exitDialog.show(stage);
			}
		});
		
		hud.setHelpListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.openHelpScreen();
			}
		});
		
		
		
		r.add(hud.getTable().top().left().padTop(5)).expandX().fillX();
		return r.bottom().left();
	}
	
	private Table BuildActionBar(){
		Table r=new Table();
		r.setFillParent(true);
		
		tileInfo=new TileInfo(skin);
		r.add(tileInfo.getTable().bottom().left()).expandX().fillX();		
		r.row();
		actions= new ActionBar(skin);
		r.add(actions.getTable().bottom().left()).expandX().fillX();			
		
		actions.setMoveListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.Move(false);
			}			
		});
		actions.setCancelListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.deselectTile();
			}			
		});
		actions.setExploreListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.Explore(false);
			}			
		});
		actions.setRestListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.Rest(false);
			}			
		});
		actions.setCraftListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.openItemScreen();
			}			
		});
		
		tileInfo.setEnemyNameListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				tileInfo.updateComponent(gameState);				
			}
		});
		tileInfo.setTileNameListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				tileInfo.updateComponent(gameState);				
			}
		});
		//r.debug();
		return r.bottom().left();
	}
	
	private void buildDialogs(){
		exitDialog = new Dialog("", skin);		
		Label l= new Label(Strings.getString("back_to_menu_dialog"), skin, "fnt12","white");
		l.setWrap(true);
		l.setAlignment(Align.center);
		exitDialog.getContentTable().add(l).expand().fill();
		exitDialog.setVisible(false);
		Dialog.fadeDuration=0;
		TextButton confirmButton=new TextButton(Strings.getString("confirm_exit_menu"),skin,"orange");
		confirmButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				TODO:Back to Menu...
				appCtrl.endGame();			
			}
			
		});
		TextButton denyButton=new TextButton(Strings.getString("deny_exit_menu"),skin,"orange");
		denyButton.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				exitDialog.hide();
				exitDialog.setVisible(false);
			}
		});
		exitDialog.getButtonTable().add(denyButton).uniform().expand().fill();
		exitDialog.getButtonTable().add(confirmButton).uniform().expand().fill();
		
		
		nextLevel=new Dialog(Strings.getString("level_clear"), skin);
		TextButton next=new TextButton(Strings.getString("next_level"), skin, "orange");
		nextLevel.add(scoreTable.getTable().bottom().left());
		nextLevel.row();
		
		nextLevel.add(next).colspan(2);
		next.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.startNewLevel();
			}
		});
		
		gameOver=new Dialog(Strings.getString("game_Over"), skin);
		Button b= new Button(skin,"empty");
		b.add(new Label(Strings.getString("points"),skin,"short")).expandX().left();
		finalPoints=new Label(""+gameState.getPoints(),skin,"short");
		b.add(finalPoints).expandX().right();
		TextButton over=new TextButton(Strings.getString("continue"),skin,"orange");
//		gameOver.debug();
//		gameOver.button(new TextButton("a",skin,"orange"));
		gameOver.clear();
		gameOver.add(b).expandX().fillX().width(150).pad(20,5,15,5);
		gameOver.row();
		gameOver.add(over);
		
		over.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.endGame();
			}
		});
		
	}
	
	
}
