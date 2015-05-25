package com.ename.diogo.martins.survival.Actions;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.CampItem;
import com.ename.diogo.martins.survival.Items.Item;

public class Use extends Action {
	private Item item;
	
	public Use(Character actor,Item i) {
		super(actor);
		item=i;
	}
	
	@Override
	public boolean validate(Game_State stt) {
		if(item instanceof CampItem)			
			isValid=false;
		else if(!character.getItems().containsKey(item.getID()))
			isValid=false;
		//TODO:Quantity of usables(can only have one right now...)
		return isValid;
	}

	@Override
	public void execute() {
		if(isValid){
			character.setResting(false);
			item.UseItem(character);
		}
	}

	

}
