package com.ename.diogo.martins.survival.Characters;

import utils.Strings;
import AI.Behaviour;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Actions.SkipTurn;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Usables;
import com.ename.diogo.martins.survival.Maps.Tile;

public class NPC extends Character implements Json.Serializable{
	private static final String TAG="NPC";
	
	private Behaviour brain;
	private String type; //TODO: enum...
	private State state;
	private State previousState;
	private int points;
	
	public NPC(String charName, int str, int end, int intel, Tile pos, String cType,Game_State gstt) {
		super(charName,charName, str, end, intel, pos);
		this.type=cType;
		this.state=State.WANDERING;
		Gdx.app.log(TAG, "gameState: " + gstt);
		this.brain=Behaviour.getBehaviour(this,gstt);
	}
	
	public NPC(){
		
	}
	
	public State getPreviousState(){return this.previousState;}
	public State getState(){return this.state;}
	public String getType(){return this.type;}
	public int getPoints(){return this.points;}
	public Behaviour getBehaviour(){return brain;}
	public void setBehaviour(Game_State gameState){this.brain=Behaviour.getBehaviour(this, gameState);}
	public void setState(State s){previousState=this.state;this.state=s;}
	@Override public String getName(){return Strings.getString(name);}
	
	public Action act(Game_State gameState){
		Action a = this.brain.getAction();
		if(a==null)
			a=new SkipTurn(this);
		a.execute();
		visible=gameState.getVisibleTiles(this);
		if(visible.contains(brain.getTarget().getTile())){
			brain.setLastSeenPostition(brain.getTarget().getTile());
		}			
		return a;
	}
	
	public static NPC getNPC(String name, Tile tile, Game_State stt){
		BaseNPC base= BaseNPC.enemies.get(name);
		Gdx.app.log(TAG, ""+name);
		NPC n = new NPC(base.name,base.str,base.end,base.intel,tile,base.type,stt);
		n.points=base.points;
		//n.gameState=stt;
		n.currentOil=base.oil;
		for(String s: base.items){
			Item i= Item.items.get(s);
			Item.CreateItem(i, n);
			if(!(i instanceof Usables))
				i.UseItem(n);
		}
		//n.setBehaviour();
		Gdx.app.log(TAG, "getNPC-> gameState: "+stt );
		return n;
	}
	
	public enum State{
		WANDERING,//aimlessly
		CAMPING,
		TAILING,
		RESTING,
		FIGHTING,
		DYING,
		PATROLLING,
		EXPLORING,
		RUNNING,
		SEARCHING,
		ATTACKING,
		HEALING;
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("brain", brain);
		json.writeValue("type", type);
		json.writeValue("state", state);
		json.writeValue("points", points);
		if(previousState!=null)
			json.writeValue("previousState", previousState);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.type=jsonData.getString("type");
		this.state=State.valueOf(jsonData.getString("state"));
		this.points=jsonData.getInt("points");
		if(jsonData.has("previousState"))
			this.previousState=State.valueOf(jsonData.getString("previousState"));
	}
}
