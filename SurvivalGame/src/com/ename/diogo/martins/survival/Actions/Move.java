package com.ename.diogo.martins.survival.Actions;

import java.util.Map;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Maps.Tile;

public class Move extends Action {
	private static String TAG="MOVE";
	
	public Move(Character actor, Tile target) {
		super(actor);
		this.newTile=target;		
	}
	
	public boolean validate(Game_State stt){
		int moveCost=newTile.getBaseTile().getMoveCost();
		Item weapon=character.getWeapon(), armor=character.getArmor();
		if(character.getWeapon()!=null)
			for(Effects e : weapon.getEffects()){
				if(e == Effects.MOVE_ENERGY_MOD || e==Effects.MOVE_ENERGY_MOD_2)
					moveCost+=e.Value();
			}
		if(armor!=null)
			for(Effects e : armor.getEffects()){
				if(e == Effects.MOVE_ENERGY_MOD || e==Effects.MOVE_ENERGY_MOD_2)
					moveCost+=e.Value();
			}
		for (Map.Entry<String, Item> entry : newTile.getItems().entrySet()){
			Item i = entry.getValue();
			for(Effects e : i.getEffects()){
				if(e == Effects.MOVE_ENERGY_MOD || e==Effects.MOVE_ENERGY_MOD_2)
					moveCost+=e.Value();
			}
		}
		//Gdx.app.log(TAG, "isValid: " + isValid );
		if(character.getEnergyPoints()< moveCost)
			isValid=false;		
		else if(!stt.getMap().getTileNeighours(character.getTile()).contains(newTile))		
			isValid=false;

		//Gdx.app.log(TAG, "isValid: " + isValid );
		cost=moveCost;
		return isValid;
	}
	
	@Override
	public void execute() {
		if(isValid){
			character.setResting(false);
			if(newTile==previousTile){
				return;
			}
			character.setEnergy(character.getEnergyPoints()-cost);
			character.setTile(newTile);
			previousTile.getCharacters().remove(character);
			newTile.getCharacters().add(character);			
		}
	}

}
