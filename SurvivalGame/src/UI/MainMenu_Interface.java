package UI;

import utils.Strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ename.diogo.martins.survival.AppController;

public class MainMenu_Interface extends User_Interface {
	
	Stage stage;
	
	TextButton newGame;
	TextButton continueGame;
	TextButton options;
	TextButton help;
	
	public MainMenu_Interface(AppController appController) {
		super(appController);
		// TODO Auto-generated constructor stub
	}

	public Stage getMainMenuInterface(){
		stage=new Stage(new ExtendViewport(320,480));
		stage.addActor( buildBackground());
		stage.addActor(buildButtons());
		setListeners();
		return stage;
	}
	
	public Table buildBackground(){
		Table root=new Table();
		root.setFillParent(true);
		
		Image color = new Image(skin,"pane-white");
		Table b = new Table();
		
		root.add(b).expand().fill().uniform();
		root.add(color).expand().fill().uniform();		
		
		return root;
	}
	
	public Table buildButtons(){
		Table root=new Table(skin);
		root.setFillParent(true);

//		skin.get("red",TextButtonStyle.class).font=skin.getFont("fnt12");
		
		Image title= new Image(skin,"title-white");
		newGame=new TextButton(Strings.getString("main_newGame"), skin, "red");
		continueGame=new TextButton(Strings.getString("main_continueGame"), skin, "red");
		options=new TextButton(Strings.getString("main_options"), skin, "red");
		options.setDisabled(true);
		help=new TextButton(Strings.getString("main_help"), skin, "red");
		help.setDisabled(false);
		
		root.add(title).top().left().padTop(60).padLeft(10);
		root.row();
		if(!Gdx.files.local("saveGame").exists())
			continueGame.setDisabled(true);
		root.defaults().width(150).expandX().fillX().uniform().pad(10);
		root.add(newGame).bottom().expand();
		root.row();
		root.add(continueGame);
		root.row();
		root.add(options);
		root.row();
		root.add(help).padBottom(80);
		
		return root;
	}
	
	
	private void setListeners(){
		newGame.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.createCharacter();				
			}
		});
		continueGame.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.loadGame();
			}
		});
		help.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.openHelpScreen();
			}
		});
	}
}
