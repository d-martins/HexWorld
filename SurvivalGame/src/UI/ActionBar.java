package UI;

import utils.Strings;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ename.diogo.martins.survival.Game_State;

public class ActionBar extends UIComponent {

	Button actions;
	Label lbl_move;
	TextButton tBtn_Move;
	TextButton tBtn_Cancel;
	TextButton tBtn_Explore;
	TextButton tBtn_Rest;
	TextButton tBtn_Craft;
	
	public ActionBar(Skin s) {
		super(s);
		buildActionBar();
	}
	
	private void buildActionBar(){
		root.setFillParent(true);
		
		actions=new Button(skin,"pane");
		root.add(actions).height(53).expandX().fillX();
		
		tBtn_Move=new TextButton(Strings.getString("confirm"),skin,"green");
		tBtn_Cancel=new TextButton(Strings.getString("cancel"),skin,"orange");
		lbl_move = new Label(Strings.getString("map_Move"),skin,"short"); 
		lbl_move.setAlignment(Align.center);
	
		tBtn_Explore= new TextButton(Strings.getString("map_Explore"), skin,"green");		
		tBtn_Rest=new TextButton(Strings.getString("map_Rest"),skin,"blue");		
		tBtn_Craft=new TextButton(Strings.getString("map_Craft"),skin,"orange");

		actions.defaults().pad(11, 6, 11, 6).expand().fill().uniform();
		actions.add(tBtn_Explore);
		actions.add(tBtn_Rest);
		actions.add(tBtn_Craft);

	}
	
	public void setListener(EventListener e){
		root.addListener(e);
	}
	
	public void setMoveListener(EventListener e){
		tBtn_Move.addListener(e);
	}
	
	public void setCancelListener(EventListener e){
		tBtn_Cancel.addListener(e);
	}
	
	public void setExploreListener(EventListener e){
		tBtn_Explore.addListener(e);
	}
	
	public void setRestListener(EventListener e){
		tBtn_Rest.addListener(e);
	}
	
	public void setCraftListener(EventListener e){
		tBtn_Craft.addListener(e);
	}
	
	public void updateComponent(Game_State gameState){
		if(!gameState.actionsEnabled()){
			tBtn_Move.setDisabled(true);
			tBtn_Explore.setDisabled(true);
			tBtn_Rest.setDisabled(true);
			tBtn_Craft.setDisabled(true);
		}
		else if(gameState.getSelectedTile()!=null){
			actions.clearChildren();
			actions.add(lbl_move);
			actions.add(tBtn_Move);
			actions.add(tBtn_Cancel);
			
			tBtn_Cancel.setDisabled(false);			
			if(gameState.Move(true))
				tBtn_Move.setDisabled(false);
			else
				tBtn_Move.setDisabled(true);
			
		}
		else{
			actions.clearChildren();
			actions.add(tBtn_Explore);
			actions.add(tBtn_Rest);
			actions.add(tBtn_Craft);
			tBtn_Craft.setDisabled(false);
			
			if(gameState.Explore(true))
				tBtn_Explore.setDisabled(false);
			else
				tBtn_Explore.setDisabled(true);
			
			if(gameState.Rest(true))
				tBtn_Rest.setDisabled(false);
			else
				tBtn_Rest.setDisabled(true);
		}
		if(gameState.getSelectedTile()!=null){
			actions.clearChildren();
			actions.add(lbl_move);
			actions.add(tBtn_Move);
			actions.add(tBtn_Cancel);
		}
		else{
			actions.clearChildren();
			actions.add(tBtn_Explore);
			actions.add(tBtn_Rest);
			actions.add(tBtn_Craft);
		}
	}
}
