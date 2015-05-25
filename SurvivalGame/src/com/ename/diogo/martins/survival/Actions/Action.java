package com.ename.diogo.martins.survival.Actions;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Maps.Tile;
import com.ename.diogo.martins.survival.Characters.Character;

public abstract class Action {
	protected int cost;
	protected Tile previousTile;
	protected Tile newTile;
	protected Character character;
	protected boolean isValid;
	
	public Action(Character actor) {
		character=actor;
		previousTile=actor.getTile();
		newTile=previousTile;
		cost=0;
		isValid=true;
	}
	
	public int getCost(){return cost;}
	public Tile getEndTile(){return newTile;}
	public Tile getPreviousTile(){return previousTile;}
	public Character getActor(){return character;}
	public boolean isValid(){return isValid;}
	
	public abstract void execute();
	
	public abstract boolean validate(Game_State stt);
}
