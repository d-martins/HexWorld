package com.ename.diogo.martins.survival;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utils.HexMath;
import utils.Strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Actions.Craft;
import com.ename.diogo.martins.survival.Actions.Explore;
import com.ename.diogo.martins.survival.Actions.Move;
import com.ename.diogo.martins.survival.Actions.Rest;
import com.ename.diogo.martins.survival.Actions.Use;
import com.ename.diogo.martins.survival.Battle.BattleController;
import com.ename.diogo.martins.survival.Battle.BattleSetUp;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Maps.EnemySpawner;
import com.ename.diogo.martins.survival.Maps.Game_Map;
import com.ename.diogo.martins.survival.Maps.Tile;

public class Game_State implements Json.Serializable{
	private static String TAG="GAME_STATE";
	AppController appCtrl;
	
	private Game_Map gameMap;
	private Character player;
	private Tile selectedTile;//*
	private ArrayList<NPC> enemies;
	private int currentEnemyMoving;//*
	private Set<Tile> visibleTiles;//*
	private EnemySpawner spawner;//*
	private DayNightCycle dayNighCycle;
	
	private boolean canMove;//*
	
	private int totalTurns;
	private int turns;
	private int level;
	private int points;
	/*-------------------------------------------------------*/
	/*						"Constructor"					 */
	/*-------------------------------------------------------*/
	public Game_State(AppController appController, Character c) {
		this.appCtrl=appController;
		totalTurns=0;		
		level=0;
		points=0;
		this.player=c;
		
		startNewLevel();
	}
	
	public Game_State(){
		this.visibleTiles=new HashSet<Tile>();
		this.enemies=new ArrayList<NPC>();
		canMove=true;
	}
	
	/*-------------------------------------------------------*/
	/*						 Getters						 */
	/*-------------------------------------------------------*/
	public Game_Map getMap(){ return gameMap;}
	public Character getPlayer(){return player;}	
	public int getTurns(){return turns;}
	public boolean actionsEnabled(){return canMove;}
	public DayNightCycle getDayNightCycle(){return dayNighCycle;}
	public ArrayList<NPC> getEnemies(){return enemies;}
	public Tile getSelectedTile(){return selectedTile;}
	public int getPoints(){return this.points;}
	/*-------------------------------------------------------*/
	/*						 Setters						 */
	/*-------------------------------------------------------*/
	public void selectTile(Tile t){selectedTile=t;}
	public static void setLanguage(String language){Strings.LoadStrings(language);}
	public void setAppController(AppController a){this.appCtrl=a;}
	public void addPoints(int i){this.points+=i;}
	/*-------------------------------------------------------*/
	/*					  		Methods						 */
	/*-------------------------------------------------------*/
	
	public void startNewLevel(){
//		if(appCtrl.getMapScreen()!=null)
//			appCtrl.getMapScreen().endLevel();
		
		level++;
		totalTurns+=turns;
		turns=1;
		
		this.visibleTiles=new HashSet<Tile>();
		
		this.gameMap=Game_Map.getRandomMap(level);
		this.dayNighCycle=gameMap.getBaseMap().getDayNightCycle();
		this.spawner= gameMap.getBaseMap().getEnemySpawner(); 
		this.player.setTile(gameMap.getStartingTile());
		
		this.enemies=new ArrayList<NPC>();
		this.currentEnemyMoving=0;
		this.canMove=true;
		
//		this.spawner.spawnEnemies(this);
		
		FileHandle f = Gdx.files.local("saveGame");
		new Json().toJson(this, f);
		
		appCtrl.resetMapScreen();
	}
	
	public int addExtraPoints(){
		int h=player.getHealthPoints()*100;
		int d= player.getCurrentOil()*150;
		int t= turns*10;
		
		int result = h+d-t;
		points+=result;
		
		return result;
	}
	
	public boolean Move(boolean testing){
//		Action a= new Move(player);
//		
//		return a.execute( this );		
		if(selectedTile==null)
		{
			return false;
		}		
		//Gdx.app.log(TAG, "Moving...");
		Move a=new Move(player,selectedTile);
		if(testing)
			return a.validate(this);
		else if(a.validate(this)){
			deselectTile();
			Gdx.app.log(TAG, "Valid...");
			endTurn(a);
			return true;
		}
		else
			return false;
	}
	
	public boolean Explore(boolean testing){
		Action a =new Explore(player);
		if(testing)
			return a.validate(this);
		else if(a.validate(this)){
			endTurn(a);//TODO:Add to list
			return true;
		}
		//TODO:Say you can't do that...	
		return false;
	}	

	public boolean Rest(boolean testing){	
		Action a =new Rest(player);
		if(testing)
			return a.validate(this);
		else if(a.validate(this)){
			endTurn(a);
			return true;
		}
		//TODO:Say energy already full...
		return false;
	}
	
	public boolean CraftItem(Item i,boolean use){
		Action a = new Craft(player,i,use);
		if(a.validate(this)){
			endTurn(a);//TODO:Add to list
			return true;
		}
		return false;
	}
	public boolean UseItem(Item i){
		Action a = new Use(player,i);
		if(a.validate(this)){
			endTurn(a);//TODO:Add to list
			return true;
		}
		return false;
	}
	
	public void SelectTile(int q, int r){		
		Tile t=gameMap.getTiles().get(new Vector2(q,r));

		Gdx.app.log(TAG, "Tapped: "+t+" q: "+q + " r: " +r);
		if(t !=null &&  t.isDiscovered() && t !=player.getTile()){
			
				Gdx.app.log(TAG, "q: " +t.getPosition().x+ "r: "+t.getPosition().y);
				if(selectedTile!=null){				
					selectedTile.setSelected(false);
					appCtrl.getMapScreen().updateTile(selectedTile);
				}
				selectedTile=t;
				t.setSelected(true);
				appCtrl.getMapScreen().updateTile(selectedTile);/*
				if(player.getPosition().CheckIfNeighbours(selectedTile))
					canMove=true;
				else 
					canMove=false;*/
				appCtrl.getMapScreen().UpdateUI();
				//TODO: Show the tile Info
				Gdx.app.log(TAG, "Selected Tile: "+ selectedTile.getPosition());
		}
		else
			deselectTile();	
	}
	
	public void deselectTile(){
		if(selectedTile!=null){
			selectedTile.setSelected(false);
			appCtrl.getMapScreen().updateTile(selectedTile);
			selectedTile=null;
			appCtrl.getMapScreen().UpdateUI();
		}
	}
	
	public void resumeUpdate(){
		appCtrl.openMapScreen();
		if(player.getHealthPoints()<=0){
			gameOver();
			return;
		}
		else {//if(player.getTile().getCharacters().size()>1){
			for(Character n : player.getTile().getCharacters()){
				if(n!=player && n.getHealthPoints()>0){
					startBattle((NPC)n);
					if(currentEnemyMoving>0)
						currentEnemyMoving--;
					return;
				}					
			}
		}
		moveNextEnemy();
	}
	
	private void endTurn(Action pAction){// ver  synch
		Gdx.app.log(TAG, "Updating...");
		if(canMove){
			appCtrl.openMapScreen();
			canMove=false;
			pAction.execute();
			UpdateTileVisibility();
			appCtrl.getMapScreen().moveCharacter(pAction);
		}		
	}
	
	public void moveNextEnemy(){
		Gdx.app.log(TAG, "Moving Enemy: " + currentEnemyMoving);
		if(currentEnemyMoving<enemies.size())
		{
			NPC n = enemies.get(currentEnemyMoving);
			currentEnemyMoving++;
			if(n.getHealthPoints()<=0){
				n.getTile().getCharacters().remove(n);
				enemies.remove(n);
				currentEnemyMoving--;
				appCtrl.getMapScreen().updateTile(n.getTile());
				n.kill();
				moveNextEnemy();
			}
			else {
				Action a=n.act(this);
				if(a==null){
					Gdx.app.log(TAG, "I'm null...why?");
					moveNextEnemy();
				}
				else
					appCtrl.getMapScreen().moveCharacter(a);
			}
		}
		else {
			currentEnemyMoving=0;
			updateState();
		}
	}
	
	private void startBattle(NPC n){		
		BattleController b=BattleSetUp.setUp(player, n, this);
		appCtrl.startBattle(b);
	}
	
	public void endBattle(){
		appCtrl.endBattle();
	}
	
	private void updateState(){
		if(player.getTile() == gameMap.getObjectiveTile()){
			appCtrl.getMapScreen().endLevel();
			return;
		}
		
		for(Map.Entry<Vector2, Tile> t:gameMap.getTiles().entrySet())
			t.getValue().Update();		
		
		turns++;
		
		dayNighCycle.Update();
		spawner.spawnEnemies(this);
		
		UpdateTileVisibility();	
		
		FileHandle f = Gdx.files.local("saveGame");
		new Json().toJson(this, f);
		canMove=true;
		
		appCtrl.getMapScreen().UpdateUI();	
	}
	
	public void UpdateTileVisibility(){
		Set<Tile> toUpdate=new HashSet<Tile>();
		
		for(Tile t: visibleTiles) 
			t.setVisibility(false);
		
		Set<Tile> newVisibles=new HashSet<Tile>(getVisibleTiles(player));
		for(Tile t: newVisibles) 
			t.setVisibility(true);

		toUpdate.addAll(newVisibles);
		toUpdate.addAll(visibleTiles);
		
		for(Tile t: toUpdate){
			appCtrl.getMapScreen().updateTile(t);
		}
		
		visibleTiles=newVisibles;
	}
	
	public Set<Tile> getVisibleTiles(Character c){		
		Set<Tile> results=new HashSet<Tile>();		
		Tile tile=c.getTile();
		Vector2 center=tile.getPosition();
		//Checks just how far it's possible to see from this position 
		int radius=c.getFOV() + tile.getBaseTile().getFovMod();//tile.getBaseTile().getFoV();		
		//During the night, field of view is decreased one point
		if(!dayNighCycle.isDay()) radius-=1; 
		if(radius<0) radius=1;
		//Gets the farthest possible tiles inside the field of view
		Vector2[]ring=HexMath.FindHexesOfRing(center, radius);
		
		//Draws lines from the tile to each tile in the ring, essentially filling it in...
		for(Vector2 v : ring){
			//Casts a line to a tile in the ring
			Vector2[] line = HexMath.FindHexesOfLine(center, v);
			//Checks each tile in the line for visibility, excluding the center tile (that one is always visible) 
			int limit=line.length;
			for(int i=1; i<limit;i++){
				//Checks that the line is actually part of the map and not an empty, non-existent Tile
				Tile t=gameMap.getTiles().get(line[i]);
				if(t!=null){
					results.add(t);
					limit-=t.getBaseTile().getFovBlock();
				}
			}
		}
		//even if the radius is 0, the tile itself is always visible...
		results.add(tile);

		return results;		
	}
	
	private void gameOver(){
		//TODO:GAME OVER;
		appCtrl.getMapScreen().loseGame();
		FileHandle f= Gdx.files.local("saveGame");
		f.delete();
		Gdx.app.log(TAG, "GAME OVER!");
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/

	@Override
	public void write(Json json) {
//		Gdx.app.log(TAG, "# enemies: " + enemies.size());
		json.writeValue("gameMap",gameMap);
		json.writeValue("player",player);
		json.writeValue("enemies",enemies);
		json.writeValue("dayNight-hour",dayNighCycle.getHour());
		json.writeValue("dayNight-day",dayNighCycle.isDay());
		json.writeValue("totalTurns",totalTurns);
		json.writeValue("turns",turns);
		json.writeValue("level",level);
		json.writeValue("points",points);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		this.gameMap=json.fromJson(Game_Map.class, jsonData.get("gameMap").toString());
		this.dayNighCycle=gameMap.getBaseMap().getDayNightCycle();
		this.spawner= gameMap.getBaseMap().getEnemySpawner(); 
		this.dayNighCycle.setHour(jsonData.getInt("dayNight-hour"));
		this.dayNighCycle.setIsDay(jsonData.getBoolean("dayNight-day"));
		this.totalTurns=jsonData.getInt("totalTurns");
		this.turns=jsonData.getInt("turns");
		this.level=jsonData.getInt("level");
		this.points=jsonData.getInt("points");
		
		/*---------------------------------------------
		 * Setting the player character and it's tiles 
		 *--------------------------------------------*/	
		JsonValue pVal=jsonData.get("player");
		this.player=json.fromJson(Character.class, pVal.toString());
		
		Vector2 pPos= json.fromJson(Vector2.class,pVal.get("position").toString());
		player.setTile(gameMap.getTiles().get(pPos));
		if(pVal.has("lastPosition")){
			Vector2 pPrevPos= json.fromJson(Vector2.class,pVal.get("lastPosition").toString());
			player.setPrevTile(gameMap.getTiles().get(pPrevPos));
		}
		
		/*---------------------------------------------
		 * Loading the enemies and setting up their tiles
		 *--------------------------------------------*/
		for(JsonValue enemy=jsonData.getChild("enemies"); enemy!=null; enemy=enemy.next){
			NPC n= json.fromJson(NPC.class, enemy.toString());
			
			Vector2 ePos = json.fromJson(Vector2.class,enemy.get("position").toString());
			n.setTile(gameMap.getTiles().get(ePos));
			if(enemy.has("lastPosition")){
				Vector2 ePrevPos= json.fromJson(Vector2.class,enemy.get("lastPosition").toString());
				n.setPrevTile(gameMap.getTiles().get(ePrevPos));
			}
			/*---------------------------------------------
			 * Sets the enemy behaviour
			 *--------------------------------------------*/
			n.setBehaviour(this);
			JsonValue brain=enemy.get("brain");
			if(brain.has("lastKnownPosition")){
				Tile t= gameMap.getTiles().get(json.fromJson(Vector2.class, brain.get("lastKnownPosition").toString()));
				n.getBehaviour().setLastSeenPostition(t);
			}
			n.getBehaviour().setLocationAchieved(brain.getBoolean("locationAchieved"));
			n.getBehaviour().setTurnsSearching(brain.getInt("turnsSearching"));
			n.getBehaviour().setTurnsSincePlayerSeen(brain.getInt("playerLastSeen"));
			enemies.add(n);
		}
	}	
}

















