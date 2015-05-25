package UI;

import utils.Strings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ename.diogo.martins.survival.AppController;
import com.ename.diogo.martins.survival.Characters.Character;

public class CharacerCreation_Interface extends User_Interface {

	Stage stage;
	TextButton confirm;
	CharacterButton selectedCharacter;
	
	public CharacerCreation_Interface(AppController appController) {
		super(appController);
	}
	
	public Stage getCharacterCreationInterface(){
		stage=new Stage();
		stage.addActor(buildBackground());
		stage.addActor(buildCharacterButtons());
		return stage;
	}
	
	public Table buildBackground(){
		Table root=new Table();
		root.setFillParent(true);
		
		Image color = new Image(skin,"pane-white");
		Table b = new Table();
		root.add(color).expand().fill().uniform();	
		root.add(b).expand().fill().uniform();
			
		
		return root;
	}
	
	private Table buildCharacterButtons(){
		Table root=new Table();
		root.setFillParent(true);
		root.padTop(100);
		for(Character c : Character.playerCharacters){
			CharacterButton cb=new CharacterButton(skin,new Character(c));
			root.add(cb.getTable()).colspan(2).padBottom(5);
			root.row();
			
			cb.setListener(new ChangeListener() {				
				@Override
				public void changed(ChangeEvent event, Actor actor) {
//					Gdx.app.log("","actor: ");
					
//					Gdx.app.log("","UObj: " + actor.getUserObject());
					CharacterButton cb= (CharacterButton) actor.getUserObject();
					cb.setSelected(true);
					cb.updateComponent(null);
					if(selectedCharacter != null){					
						selectedCharacter.setSelected(false);
						selectedCharacter.updateComponent(null);
					}
					selectedCharacter=cb;
					confirm.setDisabled(false);
				}
			});
		}
		
		confirm= new TextButton(Strings.getString("confirm_Char"), skin, "chipped-enabled-inv");
		confirm.getLabelCell().padTop(-10);
		confirm.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.startNewGame(selectedCharacter.getCharacter());
			}
		});
		confirm.setDisabled(true);
		
		TextButton cancel= new TextButton(Strings.getString("cancel_Char"), skin, "chipped-enabled");
		cancel.getLabelCell().padTop(-10);
		cancel.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.openMainMenuScreen();
			}
		});
		
		root.add(cancel).bottom().left().expand();
		root.add(confirm).bottom().right();
		
		
		return root;
	}
}
