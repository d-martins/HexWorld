package UI;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Usables;
import com.ename.diogo.martins.survival.Items.Weapon;

public class ListItem extends UIComponent {
	private static String TAG="LIST_ITEM";
	
	private Button wrapper;
	private Label cost;
	private Item item;
	private Label name;
	private boolean selected;
	private ArrayList<Button> icons; 
	private boolean isUsable; 
	
	public ListItem(Skin s, Item i) {
		super(s);
		item=i;
		icons= new ArrayList<Button>();
		selected=false;
		isUsable=false;
		buildItem();
	}
	
	private void buildItem(){
//		Table root=new Table();	
		
		wrapper=new Button(skin,"item");
		wrapper.setUserObject(this);
		cost= new Label(""+item.getCreationCost(),skin,"short");
		name=new Label(item.getName(),skin,"short");
			
		wrapper.add(cost).left().padRight(10);
		wrapper.add(name).left().expandX();
		
		if(item instanceof Weapon){
			Button attacks=new Button(skin,"empty");
			attacks.setDisabled(true);
			attacks.add(new Label(""+((Weapon) item).getAttacks(),skin,"short"));
			attacks.add(new Image(skin,"atk-ico"));
			wrapper.add(attacks).right().padRight(3);
			icons.add(attacks);
			
			Button damage= new Button(skin,"empty");
			damage.setDisabled(true);
			damage.add(new Label(""+((Weapon) item).getDamage(),skin,"short"));
			damage.add(new Image(skin,"dmg-ico"));
			wrapper.add(damage).padRight(3);
			icons.add(damage);
			
			Button duration= new Button(skin,"empty");
			duration.setDisabled(true);
			duration.add(new Label(""+((Weapon) item).getDuration(),skin,"short"));
			duration.add(new Image(skin,"dur-ico"));
			wrapper.add(duration).padRight(3);
			icons.add(duration);
		}
		else if(item instanceof Armor){
			Button armor= new Button(skin,"empty");
			armor.setDisabled(true);
			armor.add(new Label(""+((Armor)item).getBaseArmor(),skin,"short"));
			armor.add(new Image(skin,"def-ico"));
			wrapper.add(armor).padRight(3);
			icons.add(armor);
		}
		else if(item instanceof Usables){		
			isUsable=true;
			for(Effects e : item.getEffects()){
				if(e == Effects.USE_RECOVER_LIFE){
					Button health= new Button(skin,"empty");
					health.setDisabled(true);
					health.add(new Label(""+e.Value(),skin,"short"));
					health.add(new Image(skin,"health-ico"));
					wrapper.add(health).padRight(3);
					icons.add(health);
				}
				else if(e==Effects.USE_RECOVER_ENERGY){
					Button energy= new Button(skin,"empty");
					energy.setDisabled(true);
					energy.add(new Label(""+e.Value(),skin,"short"));
					energy.add(new Image(skin,"energy-ico"));
					wrapper.add(energy).padRight(3);
					icons.add(energy);
				}
				else if(e==Effects.USE_ADD_CAPACITY){
					Button cap=new Button(skin,"empty");
					cap.setDisabled(true);
					cap.add(new Label(""+e.Value(),skin,"short"));
					cap.add(new Image(skin, "bpk-ico"));
					wrapper.add(cap).padRight(3);
					icons.add(cap);
				}
				else if(e==Effects.USE_ADD_CAPACITY_2){
					Button cap=new Button(skin,"empty");
					cap.setDisabled(true);
					cap.add(new Label(""+e.Value(),skin,"short"));
					cap.add(new Image(skin, "bpk-ico"));
					wrapper.add(cap).padRight(3);
					icons.add(cap);
				}
			}
		}
		
		root.add(wrapper).expandX().fillX();
	}	

	@Override
	public void updateComponent(Game_State gameState) {
//		Gdx.app.log(TAG, "Updating listItem! Selected: "+ selected);
		Character player= gameState.getPlayer();		
		if(player.getWeapon()!= null && item.getID()== player.getWeapon().getID() || player.getArmor()!= null &&  item.getID()==player.getArmor().getID())
			wrapper.setStyle(skin.get("item-equip", ButtonStyle.class));
		else if(player.getItems().containsKey(item.getID())){
			wrapper.setStyle(skin.get("item-has", ButtonStyle.class));
			if(isUsable){
				isUsable=false;
				Usables u= (Usables)player.getItems().get(item.getID());
				if(u.getQuantity()>0)
					name.setText(name.getText()+" x"+u.getQuantity());
			}
		}
		else {
			wrapper.setStyle(skin.get("item", ButtonStyle.class));
			if(player.getCurrentOil()< item.getCreationCost()){
				cost.setColor(Color.GRAY);
				name.setColor(Color.GRAY);
				for(Button b : icons){
					for(Actor a : b.getChildren())
						a.setColor(Color.GRAY);
				}
			
		}		
		if(selected)
			wrapper.setDisabled(true);
		else 
			wrapper.setDisabled(false);
		}
		
	}
	
	public void setListener(EventListener e){
		wrapper.addListener(e);
	}
	
	public void setSelected(boolean b){
		selected=b;
	}
	
	public Item getItem(){return item;}

}
