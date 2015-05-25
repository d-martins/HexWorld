package com.ename.diogo.martins.survival.Actions;

import java.util.Map;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;

public class Explore extends Action {

	private int oilFound;
	public Explore(Character actor) {
		super(actor);
		oilFound=0;
		cost=1;
	}

	@Override
	public boolean validate(Game_State stt) {

		int extraMaterials=character.getOilDiscoveryRate();
		//adds the tile modifier to exploring action
		extraMaterials+=newTile.getBaseTile().getExploreMod();
		
		Item weapon=character.getWeapon(), armor=character.getArmor();
		//Checks the weapon for extra modifiers
		if(weapon!=null)
			for(Effects e : weapon.getEffects()){
				if(e== Effects.EXPLORE_BONUS_OIL)
					extraMaterials+=e.Value();
				else if(e == Effects.EXPLORE_ENERGY_MOD)
					cost+=e.Value();
			}
		//Checks the armor for extra modifiers
		if(armor != null)
			for(Effects e : armor.getEffects()){
				if(e == Effects.EXPLORE_BONUS_OIL)
					extraMaterials+=e.Value();
				else if(e == Effects.EXPLORE_ENERGY_MOD)
					cost+=e.Value();
			}
		//Checks the tile items for extra modifiers
		for (Map.Entry<String, Item> entry : newTile.getItems().entrySet()){
			Item i = entry.getValue();
			for(Effects e: i.getEffects()){
				if(e == Effects.EXPLORE_BONUS_OIL)
					extraMaterials+=e.Value();
				else if(e == Effects.EXPLORE_ENERGY_MOD)
					cost+=e.Value();
			}
		}	
		if(cost<0)
			cost=1;
		
		if(character.getEnergyPoints()<cost){
			isValid=false;
			return isValid;
		}
			
		
		oilFound=(int) (character.getOilDiscoveryRate()+extraMaterials);
		if(newTile.getMaterialQtt()<oilFound)
			oilFound=newTile.getMaterialQtt();
		isValid=true;
		return isValid;
		
	}
	
	@Override
	public void execute() {		
		if(isValid){
			newTile.TakeMaterials(oilFound);
			if(character.getCapacity()<character.getCurrentOil()+oilFound)
				oilFound=character.getCapacity()-character.getCurrentOil();		
			character.addOil(oilFound);
				
			character.setEnergy(character.getEnergyPoints()-cost);		
			character.setResting(false);
		}
	}


}
