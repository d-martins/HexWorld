package UI;

import utils.Strings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Usables;
import com.ename.diogo.martins.survival.Items.Weapon;

public class ItemActions extends UIComponent {

	Label description;
	ScrollPane textView;
	Table texts;
	TextButton action;
	TextButton create;
	TextButton destroy;
	Item item;
		
	public ItemActions(Skin s) {
		super(s);
		buildComponent();
	}

	private void buildComponent(){
		Button panel=new Button(skin,"pane-grey");
		texts=new Table();
//		texts.setFillParent(true);
		description=new Label("", skin,"short-small");
				
		textView= new ScrollPane(texts, skin, "pane-grey");
		textView.setOverscroll(false, false);
		
		action=new TextButton(Strings.getString("equip"), skin,"orange");
		create=new TextButton(Strings.getString("make"), skin,"orange");
		destroy=new TextButton(Strings.getString("destroy"), skin,"orange");
		
		panel.add(textView).top().left().expand().fill().padBottom(2).colspan(3);
		panel.row().expandX().fillX().uniform().pad(2);
		panel.add(action).bottom();
		panel.add(create).bottom();
		panel.add(destroy).bottom();
	
		root.add(panel).expand().fill();
	}
	
	public void setDescription(String s){
		texts.clearChildren();
		
		description.setWrap(true);		
		description.setAlignment(Align.top,Align.left);
		
		texts.add(description).left().top().expand().fill();
		description.setText(s);
	}
	
	public void addEffect(String s){
		Label l= new Label(s,skin,"short-small");
		l.setWrap(true);
		l.setAlignment(Align.top,Align.left);
		l.setColor(0,0.8f,0,1);
		texts.row();
		texts.add(l).left().expand().fill();
	}
	
	public void updateItem(Item i){
		item=i;
		if(i==null){			
			return;
		}
		setDescription(i.getDescription());
		for(Effects e : i.getEffects()){
			addEffect(e.Name()+": " +e.Description());
		}
	}
	
	@Override
	public void updateComponent(Game_State gameState) {
		if(item==null){
			setDescription("");
			action.setDisabled(true);
			create.setDisabled(true);
			destroy.setDisabled(true);
			return;
		}
		Character player=gameState.getPlayer();
		action.setDisabled(true);
		create.setDisabled(true);
		destroy.setDisabled(true);
		
		if(!player.getItems().containsKey(item.getID())){			
			if(item.getCreationCost()<=player.getCurrentOil())
				create.setDisabled(false);		
		}
		else{				
			destroy.setDisabled(false);
			if(item instanceof Usables ){
				action.setText(Strings.getString("use"));
				action.setDisabled(false);
				if(item.getCreationCost()<=player.getCurrentOil())
					create.setDisabled(false);
			}
			else
				if(item!=player.getArmor() && item!=player.getWeapon()){
					action.setDisabled(false);
				}			
		}
	}
	
	public void setCreateListener(EventListener e){
		create.addListener(e);		
	}
	
	public void setUseListener(EventListener e){
		action.addListener(e);		
	}
	
	public void setDestroyListener(EventListener e){
		destroy.addListener(e);		
	}

}
