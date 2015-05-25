package com.ename.diogo.martins.survival.Actions;

import com.badlogic.gdx.Gdx;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.CampItem;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Weapon;

public class Craft extends Action {
	private Item item;
	private boolean useNow;
	
	public Craft(Character actor, Item i, boolean use) {
		super(actor);
		cost=i.getCreationCost();
		item=i;
		useNow=use;
		// TODO item cost based on attributes
	}

	@Override
	public boolean validate(Game_State stt) {
		if(item instanceof CampItem){
			if(this.character.getTile().getItems().containsKey(item.getID()))
				isValid=false;			
		}
		else if(item instanceof Weapon || item instanceof Armor){
			if(this.character.getItems().containsKey(item.getID()))
				isValid=false;			
		}
		else if(cost>character.getCurrentOil())
			isValid=false;	
		
		
		return isValid;
	}
	
	@Override
	public void execute() {
		if(isValid){
			character.setResting(false);
			Item.CreateItem(item, character);
			character.setOil(character.getCurrentOil()-cost);
			if(useNow){
				Gdx.app.log("CRAFT", "Use Now = true");
				Action a=new Use(character, item);
				if(a.isValid())
					a.execute();
			}
		}
		
	}

	

}
