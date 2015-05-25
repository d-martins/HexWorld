package com.ename.diogo.martins.survival.Maps;

import java.util.ArrayList;

import utils.HexMath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.NPC;

public class EnemySpawner {
	private static final String path="data/config/enemySpawners.json";
	
	int[] keyTurns;
	int[] maxPlayerDistance;
	int[] minPlayerDistance;
	int[] maxEnemies;
	int[] spawnPerTurn;
	int[] nightSpawns;
	String[] enemyTypes;
	
	int index;
	ArrayList<Tile> possiblePositions;
	
	public EnemySpawner(String id) {
		loadSpawner(id);
		index=0;
	}
	
	public void spawnEnemies(Game_State gameState){
		int turns=gameState.getTurns();
		int enemyQtt=gameState.getEnemies().size();
		
		//Advances the index when enough turns have passed	
		while(keyTurns[index]<turns && index<keyTurns.length-1){
			if(minPlayerDistance[index] != minPlayerDistance[index+1] || maxPlayerDistance[index] != maxPlayerDistance[index+1])
				possiblePositions=null;
			index++;
		}
//		if(index+1<keyTurns.length && keyTurns[index+1] <= turns){
//			//If the min or max spawn distance to the player is changed, resets the possible positions array
//			if(minPlayerDistance[index] != minPlayerDistance[index+1] || maxPlayerDistance[index] != maxPlayerDistance[index+1])
//				possiblePositions=null;
//			index++;
//			
//		}
		
		//Calculates the amount of enemies to spawn this turn
		int toSpawn= maxEnemies[index] - enemyQtt;
		toSpawn+=nightSpawns[index];
		Gdx.app.log("SPAWNER", "spawnPTurn: " + spawnPerTurn.length + " index: " + index);
		if(toSpawn > spawnPerTurn[index])
			toSpawn= spawnPerTurn[index];
		
		//Gets all the possible position for spawning enemies
		if(possiblePositions == null){
			possiblePositions = new ArrayList<Tile>();
			
			int minRadius= minPlayerDistance[index];
			int maxRadius= maxPlayerDistance[index];
			Tile playerPosition= gameState.getPlayer().getTile();
			
			//Calculates all the rings within the specified limits
			for(int i=minRadius; i<=maxRadius; i++){
				//Adds all the tiles of the ring to the list
				possiblePositions.addAll(HexMath.FindHexesOfRing(playerPosition,i,gameState.getMap()));
			}
		}
		
		//Clones the list of possible positions 
		ArrayList<Tile> openSpots= new ArrayList<Tile>(possiblePositions);
		
		//Spawns the required amount of enemies
		for(int i=0; i<toSpawn; i++){
			//Randomly decides what type of enemy to Spawn
			int r= (int) (Math.random()*enemyTypes.length);
			String enemy= enemyTypes[r];
			r=-1;
			while(r==-1 && openSpots.size()>0){
				//Randomly decides which tile to spawn the enemy in
				r=(int)(Math.random()*openSpots.size());
				Tile t=openSpots.get(r);
				//If the retrieved position already has an enemy on it, remove it from the openSpots and try again
				if(t.getCharacters().size()>0){
					openSpots.remove(r);
					r=-1;
				}
				else{
					gameState.getEnemies().add(NPC.getNPC(enemy, t, gameState));
				}
				
			}
			
		}
	}
	
	private void loadSpawner(String id){
		JsonReader jsonReader=new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal(path));
		//Gets the json entry with name==id
		JsonValue j = map.get(id);
		Gdx.app.log("SPAWNER", id);
		this.keyTurns= j.get("keyTurns").asIntArray();
		this.maxPlayerDistance= j.get("maxPlayerDistance").asIntArray();
		this.minPlayerDistance= j.get("minPlayerDistance").asIntArray();
		this.maxEnemies= j.get("maxEnemies").asIntArray();
		this.spawnPerTurn= j.get("spawnPerTurn").asIntArray();
		this.enemyTypes= j.get("enemyTypes").asStringArray();
		this.nightSpawns=j.get("nightSpawns").asIntArray();
	}
	
}
