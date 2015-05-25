package com.ename.diogo.martins.survival.Actions;

import java.util.Map;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;

public class Rest extends Action {
	private int recovered;
	
	public Rest(Character actor) {
		super(actor);
		cost=0;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validate(Game_State stt) {
		Item weapon=character.getWeapon(), armor=character.getArmor();
		// TODO Auto-generated method stub
		if(character.getEnergyPoints()>=character.getBaseEnergy()){
			isValid=false;
			return false;
		}
		//if(!character.isResting())
		StartResting();
		
		if(character.getEnergyPoints()>character.getEnergyIntervals()[1]) 
			recovered=3;
		else if(character.getEnergyPoints()>character.getEnergyIntervals()[0]) 
			recovered=2;
		else 
			recovered=1;
		//Check Items
		if(weapon!=null)
			for(Effects e : weapon.getEffects()){
				if(e == Effects.REST_EXTRA_ENERGY)
					recovered+=e.Value();
			}
		if(armor!=null)
			for(Effects e : armor.getEffects()){
				if(e == Effects.REST_EXTRA_ENERGY)
					recovered+=e.Value();
			}
		for (Map.Entry<String, Item> entry : newTile.getItems().entrySet()){
			Item i = entry.getValue();
			for(Effects e : i.getEffects()){
				if(e == Effects.REST_EXTRA_ENERGY)
					recovered+=e.Value();
			}
		}
		if(recovered<1)
			recovered=1;
		
		isValid=true;
		return true;
	}

	@Override
	public void execute() {
		if(isValid){
			//Se não tiver comida suficiente, dsconta pontos de vida também
			if(character.getCurrentOil()<cost){
				int dif=cost-character.getCurrentOil();
				character.setOil(0);
				character.setHealth(character.getHealthPoints()-dif);
			}
			else
				character.setOil(character.getCurrentOil()-cost);
			character.setResting(true);
			
			//Adiciona energia a personagem		
			character.setEnergy(character.getEnergyPoints()+recovered);		
			//Interrompe descanso se a energia está cheia
			if(character.getEnergyPoints()>=character.getBaseEnergy()){
				character.setEnergy(character.getBaseEnergy());
//				character.setResting(false);
			}			
		}

	}
	
	private void StartResting(){
		Item weapon=character.getWeapon(), armor=character.getArmor();
		//Determina a quantidade de comida a descontar, com base na energia actual 
		if(character.getEnergyPoints()>character.getEnergyIntervals()[1]) 
			cost=1;
		else if(character.getEnergyPoints()>character.getEnergyIntervals()[0]) 
			cost=2; 
		else 
			cost=3; 
		
		//Adiciona os modificadores de consumo da telha e dos items 
		cost+=newTile.getBaseTile().getFoodConsumMod();
		if(weapon!=null)
			for(Effects e : weapon.getEffects()){
				if(e == Effects.REST_OIL_COST_MOD)
					cost+=e.Value();
			}
		if(armor!= null)
			for(Effects e : armor.getEffects()){
				if(e == Effects.REST_OIL_COST_MOD)
					cost+=e.Value();
			}
		for (Map.Entry<String, Item> entry : newTile.getItems().entrySet()){
			Item i = entry.getValue();
			for(Effects e : i.getEffects()){
				if(e == Effects.REST_OIL_COST_MOD)
					cost+=e.Value();
			}
		}		
		//Desconto de comida não pode ser inferior a 0, senão adiciona comida
		if(cost<0) 
			cost=0;	
	}

}
