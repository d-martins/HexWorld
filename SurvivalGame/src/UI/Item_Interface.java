package UI;

import java.util.Map;

import utils.Strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ename.diogo.martins.survival.AppController;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.CampItem;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Usables;
import com.ename.diogo.martins.survival.Items.Weapon;
import com.ename.diogo.martins.survival.Maps.Tile;

public class Item_Interface extends User_Interface {
	private static String TAG="ITEM_INTERACE";
	private ItemsList list;
	private StatusBars stats;
	private Dialog useDialog;
	
	private Stage stage;
	private TextButton confirmButton;
	private TextButton denyButton;
	
	Game_State gameState;
	
	public Item_Interface(AppController appController) {
		super(appController);
		gameState=appController.getGameState();
	}
	
	public Stage getItemUserInterface(){
		stage=new Stage(new ExtendViewport(320,480));
		stage.addActor(BuildItemsTable());
		stage.addListener(new ClickListener(){
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode==Keys.BACK){
					if(useDialog.isVisible()){
						useDialog.hide();
						useDialog.setVisible(false);
					}
					else
						appCtrl.openMapScreen();
				}
				return super.keyDown(event, keycode);
			}
		});
		
		buildDialogs();
		return stage;
	}
	
	public void UpdateItemsUI(){		
		stats.updateComponent(gameState);
		list.updateComponent(gameState);
	}
	
		
	private Table BuildItemsTable(){
		Table t= new Table();
		t.setFillParent(true);
		
		stats= new StatusBars(skin);
		list = new ItemsList(skin);
		
		list.setCreateListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				TODO:USE NOW?
//				gameState.CraftItem(list.getSelectedItem());
				useDialog.setVisible(true);
				useDialog.show(stage);
			}
		});
		
		list.setUseListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.UseItem(list.getSelectedItem());
			}
		});
	
		t.row().padTop(5);
		t.add(stats.getTable()).top().left().expandX().padBottom(10);
		t.add(buildBackButton()).right().padRight(5);
		t.row();
		t.add(list.getTable()).expandX().fillX().expandY().top().colspan(2);
		
//		useDialog = new Dialog(Strings.getString("use_now_title"), skin);
		return t;
	}
		
	private Table buildBackButton(){		
		Button btn_Pause=new Button(skin,"close");
		btn_Pause.addListener(new ChangeListener(){
				@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				appCtrl.openMapScreen();
			}			
		});
		
		return btn_Pause;
	}
	
	private void buildDialogs(){
		useDialog = new Dialog("", skin);		
		useDialog.text(Strings.getString("use_now"));
		useDialog.setVisible(false);
		Dialog.fadeDuration=0;
		confirmButton=new TextButton(Strings.getString("confirm_use_now"),skin,"orange");
		confirmButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.CraftItem(list.getSelectedItem(),true);
			}
		});		
		denyButton=new TextButton(Strings.getString("deny_use_now"),skin,"orange");
		denyButton.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameState.CraftItem(list.getSelectedItem(),false);
			}
		});
		useDialog.button(confirmButton);
		useDialog.button(denyButton);
	}
	
	enum Filter{
		WEAPON(Weapon.class),
		ARMOR(Armor.class),
		USABLE(Usables.class),
		CAMP(CampItem.class);
		
		Class cls;
		Filter(Class c){
			this.cls=c;			
		}
		
	}
}

