package com.ename.diogo.martins.survival.Battle;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Characters.NPC;

public class BattleController {
	static private String TAG = "BATTLE_CONTROLLER";
	State state;
	Game_State gameState;
	ArrayList<Target> targets;	
	final static float START_DELAY;
	
	final static float ATTACK_FIXED_TARGET_DELAYS;	
	final static float ATTACK_FIXED_TARGET_DURATION;
	final static float ATTACK_MOVING_TARGET_DELAYS;
	final static float ATTACK_MOVING_TARGET_DURATION;
	
	final static float DEFENSE_MIN_TARGET_DELAYS;
	final static float DEFENSE_MAX_TARGET_DELAYS;
	final static float TRANSITION_DURATION;
	final static float FASE_DURATION;
	final static float DEFENSE_MIN_TARGET_DURATION;
	final static float DEFENSE_MAX_TARGET_DURATION;
	final static float END_DELAY;
	final static float DELAY;
	
	final static float MATERIAL_GAIN;
	
	static{
		JsonReader jsonReader=new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal("data/config/battle/battleConfigs.json"));
		START_DELAY = map.getFloat("START_DELAY");
		ATTACK_FIXED_TARGET_DELAYS=map.getFloat("ATTACK_FIXED_TARGET_DELAYS");	
		ATTACK_FIXED_TARGET_DURATION= map.getFloat("ATTACK_FIXED_TARGET_DURATION");
		ATTACK_MOVING_TARGET_DELAYS=map.getFloat("ATTACK_MOVING_TARGET_DELAYS");
		ATTACK_MOVING_TARGET_DURATION= map.getFloat("ATTACK_MOVING_TARGET_DURATION");
		DEFENSE_MIN_TARGET_DELAYS=map.getFloat("DEFENSE_MIN_TARGET_DELAYS");
		DEFENSE_MAX_TARGET_DELAYS=map.getFloat("DEFENSE_MAX_TARGET_DELAYS");
		TRANSITION_DURATION =map.getFloat("TRANSITION_DURATION");
		FASE_DURATION =map.getFloat("FASE_DURATION");
		DEFENSE_MIN_TARGET_DURATION= map.getFloat("DEFENSE_MIN_TARGET_DURATION");
		DEFENSE_MAX_TARGET_DURATION= map.getFloat("DEFENSE_MAX_TARGET_DURATION");
		END_DELAY=map.getFloat("END_DELAY");
		DELAY=map.getFloat("DELAY");
		MATERIAL_GAIN=map.getFloat("MATERIAL_GAIN");
		
	}
	
	Character player;
	int pAttacks;
	int pBonusAttacks;
	int pArmor;
	int pDamage;
	boolean atkPhase;
	float targetDuration;
	
	NPC enemy;
	int eAttacks;
	int eDamage;
	int eArmor;
	
	private float timeSinceState;
	
	public BattleController(Character gamePlayer, NPC theEnemy, Game_State stt, int playerAtk, int playerBAtk, int playerArm, int playerDmg, float duration,
			int enemyAtk, int enemyDmg, int enemyArm) {
		Gdx.app.log(TAG, "Starting battle");
		
		setState(State.STARTING);
		this.gameState=stt;
		this.player=gamePlayer;
		this.enemy=theEnemy;
		this.pAttacks=playerAtk;
		this.pBonusAttacks=playerBAtk;
		this.pDamage=playerDmg;
		this.pArmor=playerArm;
		this.targetDuration=duration;
		this.eAttacks=enemyAtk;
		this.eDamage=enemyDmg;
		this.eArmor=enemyArm;
		
		atkPhase=false;
		timeSinceState=0;
		targets=new ArrayList<Target>();
		
	}	
	
	public void setState(State s){
		state=s;
		timeSinceState=0;
		Gdx.app.log(TAG, "new State: " + s);
	}
	
	public Character getPlayer(){
		return player;
	}
	
	public Character getEnemy(){
		return enemy;
	}
	
	
	
	public float getPlayerBaseHealth(){
		return player.getBaseHealth();
	}
	
	public float getPlayerHealth(){
		return player.getHealthPoints();
	}
	
	public float getEnemyBaseHealth(){
		return enemy.getBaseHealth();
	}
	
	public float getEnemyHealth(){
		return enemy.getHealthPoints();
	}
	
	
	public synchronized void  update(){
		playerDamaged=false;
		timeSinceState+=Gdx.graphics.getDeltaTime();
		//TODO: case
		if(state==State.STARTING)
			start();
		else if(state==State.TRANSITIONING)
			transition();
		else if(state==State.ATTACKING)
			attack();
		else if(state==State.DEFENDING)
			defend();
		else if((state==State.ENDING))
			end();
		else if((state==State.DELAYING))
			delay();
		else if(state==State.OVER)
			endBattle();
	}
	
	private void start(){
		if(timeSinceState >= START_DELAY){
			setState(State.TRANSITIONING);
			atkPhase=true;
		}
	}
	
	private void transition(){
		if(targets.size()>0){
			targets.clear();
			poppedTargets.clear();
		}
		if(timeSinceState >= TRANSITION_DURATION){
			createTargets();
			if(atkPhase){
				setState(State.ATTACKING);
			}
			else
				setState(State.DEFENDING);
			atkPhase=!atkPhase;
			
		}
	}
	
	private void attack(){
		
		//float deltaTime=Gdx.graphics.getDeltaTime();
		//Gdx.app.log(TAG, "target size: " +poppedTargets.size());
		if(/*timeSinceState>=FASE_DURATION ||*/ targets.size()==poppedTargets.size()){
			setState(State.DELAYING);
		}
		else{				
			updateTargets();
		}
	}
	ArrayList<Target> poppedTargets=new ArrayList<Target>();
	
	private void defend(){	
		//if(timeSinceState>=FASE_DURATION)
		if(targets.size()==poppedTargets.size()){			
			setState(State.DELAYING);
		}
		else{
			updateTargets();
		}
	}	
	
	private void delay(){
		if(timeSinceState>=DELAY)
			setState(State.TRANSITIONING);
	}
	
	private void updateTargets(){
		float deltaTime=Gdx.graphics.getDeltaTime();
		for(Target t : targets){
			t.update(deltaTime);
			if(timeSinceState >= t.spawnTime){
				t.spawn();
			}
			if(t.state==Target.State.POPPING){						
				if(!poppedTargets.contains(t)){					
					poppedTargets.add(t);
					
				}
			}
			else if(t.state==Target.State.DESTROYED){
				if(!poppedTargets.contains(t)){
					poppedTargets.add(t);
					if(state==State.DEFENDING){
						popTarget(t);
					}
				}
			}
		}
	}
	
	private void createTargets(){
		float timing=0;
		if(atkPhase){
			for(int i=0;i<pAttacks;i++){
			//for(int i=0; i<5;i++){
				Target t =new Target(ATTACK_FIXED_TARGET_DURATION+targetDuration, timing, true);
				Gdx.app.log(TAG, "t Duration: " + t.duration);
				timing+=ATTACK_FIXED_TARGET_DELAYS;
				targets.add(t);
			}timing=ATTACK_MOVING_TARGET_DELAYS;
			for(int i=0; i<pBonusAttacks;i++){
				Target t =new Target(ATTACK_MOVING_TARGET_DURATION, timing, false);
				timing+=ATTACK_MOVING_TARGET_DELAYS+Math.random()*(ATTACK_MOVING_TARGET_DELAYS-ATTACK_FIXED_TARGET_DELAYS);
				targets.add(t);
			}
		}
		else
			for(int i=0;i<eAttacks;i++){
				//for(int i=0; i<5;i++){
				float duration = DEFENSE_MIN_TARGET_DURATION/2+(float)Math.random()*(DEFENSE_MAX_TARGET_DURATION-DEFENSE_MIN_TARGET_DURATION/2);
				Target t =new Target(duration, timing, true);
				Gdx.app.log(TAG, "t Duration: " + t.duration);
				timing+=DEFENSE_MIN_TARGET_DELAYS+Math.random()*(DEFENSE_MAX_TARGET_DELAYS-DEFENSE_MIN_TARGET_DELAYS);
				targets.add(t);
			}
		Gdx.app.log(TAG, "Created the targets: " + targets.size());
	}
	
	public void end(){
		if(targets.size()>0)
			targets.clear();
		if(timeSinceState>=END_DELAY)
			setState(State.OVER);
	}
	
	public void endBattle(){
		Gdx.app.log(TAG, "End battle");
		if(player.getHealthPoints()>0){
//			Gdx.app.log(TAG,""+(int)(enemy.getAvailableOil()*.2f));
			player.addOil((int)(enemy.getCurrentOil()*MATERIAL_GAIN)+player.getOilDiscoveryRate());
			Gdx.app.log(TAG,"enemyPoints: "+enemy.getPoints());
			gameState.addPoints(enemy.getPoints());
		}
		
		gameState.resumeUpdate();
	}
	
	public void popTarget(Target t){			
		t.pop();	
		if(state==State.ATTACKING)
			damageEnemy();
		else if(state==State.DEFENDING)
			damagePlayer();
	}
	
	public void damageEnemy(){
		int dmg=pDamage;
		if(enemy.getArmor()!=null){
			if(enemy.getArmor().getArmorValue()==pDamage){
				enemy.destroyArmor();
				dmg=0;
			}
			else if(enemy.getArmor().getArmorValue()>pDamage){
				enemy.getArmor().damageaArmor(pDamage);
				dmg=0;
			}
			else{
				dmg-=enemy.getArmor().getArmorValue();
				enemy.destroyArmor();
			}		
		}
		
		enemy.setHealth(enemy.getHealthPoints()-dmg);
		Gdx.app.log(TAG, "enemy Health: " + enemy.getHealthPoints());
		if(enemy.getHealthPoints()<=0)
			setState(State.ENDING);
	}
	boolean playerDamaged=false;
	public void damagePlayer(){
		int dmg=eDamage;
		playerDamaged=true;
		if(player.getArmor()!=null){
			if(player.getArmor().getArmorValue()==eDamage){
				player.destroyArmor();
				dmg=0;
			}
			else if(player.getArmor().getArmorValue()>eDamage){
				player.getArmor().damageaArmor(eDamage);
				dmg=0;
			}
			else{
				dmg-=player.getArmor().getArmorValue();
				player.destroyArmor();
			}			
		}
		player.setHealth(player.getHealthPoints()-dmg);
		if(player.getHealthPoints()<=0)
			setState(State.ENDING);
	}
	
	enum State{
		STARTING,
		ATTACKING,
		DEFENDING,
		DELAYING,
		TRANSITIONING,
		LOSING,
		WINNING,
		ENDING,
		OVER;
	}
}


