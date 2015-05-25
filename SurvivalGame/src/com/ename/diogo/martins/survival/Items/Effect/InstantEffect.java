package com.ename.diogo.martins.survival.Items.Effect;
import com.ename.diogo.martins.survival.Characters.Character;

public class InstantEffect extends Effect {
	
	int lifeRec;
	int energyRec;
	int oilAdd;
	int capacityAdd;

	public InstantEffect(String code, int life, int energy, int oil, int capacity) {
		super(code);
		
		this.lifeRec=life;
		this.energyRec=energy;
		this.oilAdd=oil;
		this.capacityAdd=capacity;
	}

	@Override
	public void apply(Object character) {
		if(character instanceof Character){
			((Character)character).addHealth(lifeRec);
			((Character)character).addEnergy(energyRec);
			((Character)character).addCapacity(capacityAdd);
			((Character)character).addOil(oilAdd);
		}		
	}

}
