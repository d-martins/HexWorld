package com.ename.diogo.martins.survival.Items.Effect;

import com.ename.diogo.martins.survival.Battle.BattleController;

public class BattleEffect extends Effect {
	
	int attackBonus;
	int defBonus;	
	float durationBonus;
	
	public BattleEffect(String code, int atk, int def, float dur){
		super(code);
		
		this.attackBonus=atk;
		this.defBonus=def;
		this.durationBonus=dur;
	}

	public  void apply(Object battle) {
		if(battle instanceof BattleController){
			BattleController b=((BattleController)battle);} 
			
		
		
	}
	

}
