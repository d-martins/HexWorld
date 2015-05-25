package UI;

import utils.TextureManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;;

public class HUD extends UIComponent {

	StatusBars bars;
	Button portrait;
	Button pause;
	TextButton points;
	TextButton help;
	
	public HUD(Skin s) {
		super(s);
		buildHud();
	}
	
	public void setCharacterPortrait(Character player){
		Gdx.app.log("HUD", "character code: "+ player.getCode());
		portrait.add(new Image(skin, player.getCode()));
	}
	
	public void setPortraitListener(EventListener e){
		portrait.addListener(e);
	}
	
	public void setPauseListener(EventListener e){
		pause.addListener(e);
	}
	
	public void setHelpListener(EventListener e){
		help.addListener(e);
	}
	
	private void buildHud(){
		root.setFillParent(true);
		root.defaults().padRight(3).padLeft(3).top().left();
		points= new TextButton("Pts: 0000", skin,"points");
		
		portrait= new Button(skin, "border-all");		
//		bars= new StatusBars(skin);
		pause= new Button(skin, "empty");
		pause.add(new Image(skin, "button-cross"));
		
		help= new TextButton("?", skin, "blue");
		help.setColor(1, 1, 1, 0.5f);
		
		root.add(portrait);	
		root.add(points);
//		root.add(bars.getTable());	
		root.add(pause).right().expandX();
		root.row();
		root.add(help);
		
	}

	@Override
	public void updateComponent(Game_State gameState) {
//		bars.updateComponent(gameState);		
		points.setText("Pts: "+padNumber(gameState.getPoints(),6));
	}
	
	
	private String padNumber(int num, int pad){
		int length = String.valueOf(num).length();
		int toAdd=pad-length;
		if(toAdd<=0)
			return ""+num;
		
		String zeroes="";
		for(int i=0;i<toAdd;i++){
			zeroes+="0";
		}
		
		return zeroes+num;
	}
	

}
