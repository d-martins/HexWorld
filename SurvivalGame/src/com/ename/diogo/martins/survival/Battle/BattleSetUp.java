package com.ename.diogo.martins.survival.Battle;

import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Maps.Tile;

public class BattleSetUp {
	//static final float TARGET_DURATION=1.5f;
	
	static Character player;
	static int pAttacks;
	static int pBonusAttacks;
	static int pDamage;
	static int pArmor;
	static float targetDuration;
	
	static NPC enemy;
	static int eAttacks;
	static int eDamage;
	static int eArmor;

	static int pAtkBonus;
	static int eAtkBonus;
	static int pDefBonus;
	static int eDefBonus;
	
	public static BattleController setUp(Character gamePlayer, NPC theEnemy, Game_State stt){
		player=gamePlayer;
		enemy=theEnemy;
		pAttacks=0;
		pBonusAttacks=0;
		pDamage=1;
		pArmor=0;
		targetDuration=0;
		eAttacks=1;
		eDamage=1;
		eArmor=0;
		pAtkBonus=0;
		eAtkBonus=0;
		pDefBonus=0;
		eDefBonus=0;
		
		checkPlayerBonuses();
		checkEnemyBonuses();
		
		Gdx.app.log("SETUP", "AtkBonus: " + pAtkBonus + " defBonus: "+ pDefBonus + "\ne AtkBonus: " + eAtkBonus + " DefBonus: " + eDefBonus);
		Gdx.app.log("SETUP", "enemyAttacks: " + eAttacks);
		pAttacks+=pAtkBonus;
		eAttacks-=pDefBonus;
		eAttacks+=eAtkBonus;
		pAttacks-=eDefBonus;
		Gdx.app.log("SETUP", "enemyAttacks: " + eAttacks);
		if(pAttacks<1)
			pAttacks=1;
		if(eAttacks<1)
			eAttacks=1;
		
		return new BattleController(gamePlayer, theEnemy, stt, pAttacks, pBonusAttacks, pArmor, pDamage, targetDuration, eAttacks, eDamage, eArmor);
	}
	
	private static void checkPlayerBonuses(){
		Tile t = player.getTile();
		pAttacks=player.getBaseAttack();
		pBonusAttacks=0;
		
		pAtkBonus+=t.getAttackBonus();
		pDefBonus+=t.getDefenseBonus();
		Gdx.app.log("SETUP", "AtkBonus: " + pAtkBonus + " defBonus: "+ pDefBonus);
		//pAttacks += t.getBaseTile().getAtkBonus();			
		
		//If player has weapon equipped
		if(player.getWeapon()!=null)
		{
			//Add attacks from weapon to bonusAttacks
			pBonusAttacks += player.getWeapon().getAttacks();
			pDamage+=player.getWeapon().getDamage();
			targetDuration+=player.getWeapon().getDuration();
			Gdx.app.log("SETUP", "target duration: " + targetDuration);
			//If the weapon also has effects 
			for(Effects e : player.getWeapon().getEffects()){
				//Any atk bonus gets added to the base Atk
				if(e==Effects.BATTLE_ATK_BONUS)
					pAtkBonus+=e.Value();//pAttacks+=e.Value();
				//Any def bonus decreases the enemy Atk
				else if(e==Effects.BATTLE_DEF_BONUS)
					pDefBonus+=e.Value();//eAttacks-=e.Value();
			}
		}
		
		//If the player has armour equipped 
		if(player.getArmor()!=null){
			//Set his armor value
			pArmor=player.getArmor().getBaseArmor();
			//if Armor also has effects
			
			for(Effects e : player.getArmor().getEffects()){
				//Any atk bonus gets added to the base Atk
				if(e==Effects.BATTLE_ATK_BONUS)
					pAtkBonus+=e.Value();//pAttacks+=e.Value();
				//Any def bonus decreases the enemy Atk
				else if(e==Effects.BATTLE_DEF_BONUS)
					pDefBonus+=e.Value();//eAttacks-=e.Value();
			}		
		}
			
//		//Extra attacks from items of tile
//		for(Entry<String, Item> entry : t.getItems().entrySet()){
//			for(Effects e : entry.getValue().getEffects()){
//				if(e==Effects.BATTLE_ATK_BONUS)
//					pAttacks+=e.Value();
//				else if(e==Effects.BATTLE_DEF_BONUS)
//					eAttacks-=e.Value();
//			}
//		}
	}
	
	private static void checkEnemyBonuses(){
		eAttacks=enemy.getBaseAttack();
		
		if(enemy.getWeapon()!=null)
		{
			//Add attacks from weapon to bonusAttacks
			eAttacks += enemy.getWeapon().getAttacks();
			eDamage+=enemy.getWeapon().getDamage();			
			//If the weapon also has effects 
			for(Effects e : enemy.getWeapon().getEffects()){
				//Any atk bonus gets added to the base Atk
				if(e==Effects.BATTLE_ATK_BONUS)
					eAtkBonus+=e.Value();//eAttacks+=e.Value();
				//Any def bonus decreases the enemy Atk
				else if(e==Effects.BATTLE_DEF_BONUS){
					eDefBonus+=e.Value();
//					if(pBonusAttacks>=e.Value())
//						pBonusAttacks -=e.Value();
//					else{
//						pBonusAttacks =0;
//						pAttacks-=e.Value();
//					}
				}
			}
		}
		if(enemy.getArmor()!=null){
			//Set his armor value
			eArmor=enemy.getArmor().getBaseArmor();
			//if Armor also has effects
			for(Effects e : enemy.getArmor().getEffects()){
				//Any atk bonus gets added to the base Atk
				if(e==Effects.BATTLE_ATK_BONUS)
					eAtkBonus+=e.Value();//eAttacks+=e.Value();
				//Any def bonus decreases the enemy Atk
				else if(e==Effects.BATTLE_DEF_BONUS){
					eDefBonus+=e.Value();
//					if(pBonusAttacks>=e.Value())
//						pBonusAttacks -=e.Value();
//					else{
//						pBonusAttacks =0;
//						pAttacks-=e.Value();
//					}
				}
			}
		}
	}
}
