package AI;

import java.util.ArrayList;
import java.util.Map.Entry;

import utils.A_Star;
import utils.HexMath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Actions.Craft;
import com.ename.diogo.martins.survival.Actions.Explore;
import com.ename.diogo.martins.survival.Actions.Move;
import com.ename.diogo.martins.survival.Actions.Rest;
import com.ename.diogo.martins.survival.Actions.Use;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Maps.Tile;

public abstract class Behaviour  implements Json.Serializable{
	private static String TAG="BEHAVIOUR";
	
	protected NPC body;
	protected Game_State gameState;
	protected Character target;
	protected ArrayList<Tile> path;
	protected Tile lastKnownPosition;
	protected Character player;
	protected int turnsSearching;
	protected int playerLastSeen;
	protected Vector2 finalDirection;
	protected Action action;
	boolean locationAchieved;
	
	public Behaviour(NPC n, Game_State stt){
		body=n;
		gameState=stt;
		player=stt.getPlayer();
		target=player;
		turnsSearching=0;
		playerLastSeen=0;
		locationAchieved=false;
	}
	
	public Behaviour(){
		
	}
	
	public void  setTurnsSincePlayerSeen(int i){playerLastSeen=i;}
	public void  setTurnsSearching(int i){turnsSearching=i;}
	public void setLastKnownPosition(Tile t){lastKnownPosition=t;}
	public void setFinalDirection(Vector2 v){finalDirection=v;}
	public void setLocationAchieved(boolean b){locationAchieved=b;}
	
	//checks the current state of the body and calls the corresponding action
	public Action getAction(){	
		action=null;
		playerLastSeen++;
		//updates the visible tiles of the NPC, if he doesn't have any yet
		if(body.getVisibleTiles()==null)
			body.setVisibleTiles(gameState.getVisibleTiles(body));
		//Sets the last known position of the player if, on his turn he came in visible range
		if(body.getVisibleTiles().contains(player.getTile())){
			lastKnownPosition=player.getTile();
			playerLastSeen=0;
		}		
		//if resting -> rest(), if attacking -> attack()...
		switch(body.getState()){
			case WANDERING:
				wander();
				break;
			case ATTACKING:
				attack();
				break;
			case FIGHTING:
				fight();
				break;
			case RUNNING:
				run();
				break;
			case EXPLORING:
				explore();
				break;
			case SEARCHING:
				search();
				break;
			case RESTING:
				rest();
				break;
			case CAMPING:
				camp();
				break;
			case TAILING:
				tail();
				break;
			case DYING:
				die();
				break;
			case PATROLLING:
				patrol();
				break;
			case HEALING:
				heal();
				break;
			default:
				break;		
		}
		
		return action;
	}
	
	public Character getTarget(){return target;}
	public void setLastSeenPostition(Tile t){this.lastKnownPosition=t; this.playerLastSeen=0; this.turnsSearching=0;}
	
	//return a behaviour according to the NPC type
	public static Behaviour getBehaviour(NPC n, Game_State stt){
		Gdx.app.log(TAG, n.getType());
		BehaviourType t= BehaviourType.valueOf(n.getType());
		switch(t){
		case BERSERKER:
			return new Berserker(n,stt);
		case SCAVENGER:
			return new Scavenger(n,stt);
		case SCARED:
			return new Scared(n,stt);
		case ASSASSIN:
			return new Assassin(n,stt);
		case NEUTRAL:
		default:
			return new Neutral(n,stt);	
		}
	}
	
	/**
	 * Selects a random position the NPC can move to.
	 * Tries to ignore tiles in the direction he came from.
	 * @return The random tile he can move to.	 */
	final protected Tile getRandomMovePosition(){
		ArrayList<Tile> neighbours=gameState.getMap().getTileNeighours(body.getTile());
		
		for(int i=0; i< neighbours.size();i++){
			Tile t=neighbours.get(i);
			if(t.getCharacters().size()>0 && t!=player.getTile()){
				neighbours.remove(i);
				i--;
			}
		}
		if(neighbours.size()==0)
			return null;
		
		if(body.getPreviousPosition()!=null){
			//checks the tiles he could have moved to last turn (also includes the tile he was standing on)
			ArrayList<Tile> lastNeighbours=gameState.getMap().getTileNeighours(body.getPreviousPosition());
			lastNeighbours.add(body.getPreviousPosition());
			ArrayList<Tile> intersection=new ArrayList<Tile>(lastNeighbours);
			intersection.retainAll(neighbours);
			//Removes the previous neighbours from the list of tiles he can move to
			//This works better than just removing the last position, as it gives the movement a sense of direction/purpose
			if(neighbours.size()>intersection.size())//prevents the movement possibilities from being 0
				//TODO:Instead of random, go for the least expensive or more attractive...
				neighbours.removeAll(intersection);
		}
		//Randomly selects a new position to move to from the list...
		double r=Math.random()*neighbours.size();
		int i = (int)r;
		return neighbours.get(i);
	}
	
	/**
	 * Selects a random position away from the player, the NPC can move to.
	 * @return The random tile he can move to.	 */
	final protected Tile getTileAwayFromTarget(){
		ArrayList<Tile> neighbours=gameState.getMap().getTileNeighours(body.getTile());
		ArrayList<Tile> probableTiles=new ArrayList<Tile>();
		int maxDist=0;
		int minCost=10;

		for(int i=0; i< neighbours.size();i++){
			Tile t=neighbours.get(i);
			if(t.getCharacters().size()>0 && t!=player.getTile()){
				neighbours.remove(i);
				i--;
			}
		}
		if(neighbours.size()==0) neighbours.add(body.getTile());
		
		for(Tile t : neighbours){
			int dist = HexMath.HexDistance(lastKnownPosition.getPosition(), t.getPosition());
			if(dist>maxDist){
				maxDist=dist;
				probableTiles.clear();
				probableTiles.add(t);
			}
			else if(dist==maxDist)
				probableTiles.add(t);
		}
		ArrayList<Tile> options=new ArrayList<Tile>();
		for(Tile t : probableTiles){
			if(t.getBaseTile().getMoveCost()<=minCost){
				minCost=t.getBaseTile().getMoveCost();
				options.add(t);
			}
		}
		double r=Math.random()*options.size();
		int i = (int)r;
		return options.get(i);	
	}
	/**
	 * Selects a random position closer to the player, the NPC can move to.
	 * @return The random tile he can move to.	 */
	final protected Tile getTileCloserToTarget(){
		ArrayList<Tile> neighbours=gameState.getMap().getTileNeighours(body.getTile());
		ArrayList<Tile> probableTiles=new ArrayList<Tile>();
		int minDist=Integer.MAX_VALUE;
		int minCost=10;

		for(int i=0; i< neighbours.size();i++){
			Tile t=neighbours.get(i);
			if(t.getCharacters().size()>0 && t!=player.getTile()){
				neighbours.remove(i);
				i--;
			}
		}
		if(neighbours.size()==0) return null;
		
		for(Tile t : neighbours){
			int dist = HexMath.HexDistance(lastKnownPosition.getPosition(), t.getPosition());
			if(dist<minDist){
				minDist=dist;
				probableTiles.clear();
				probableTiles.add(t);
			}
			else if(dist==minDist)
				probableTiles.add(t);
		}
		ArrayList<Tile> options=new ArrayList<Tile>();
		for(Tile t : probableTiles){
			if(t.getBaseTile().getMoveCost()<=minCost){
				minCost=t.getBaseTile().getMoveCost();
				options.add(t);
			}
		}
		double r=Math.random()*options.size();
		int i = (int)r;
		return options.get(i);
	}
	
	final protected Tile getExplorableTile(){
		//Gets all the neighbouring tiles
		ArrayList<Tile> neighbours=gameState.getMap().getTileNeighours(body.getTile());
		ArrayList<Tile> options=new ArrayList<Tile>();
		int max=0;
		
		for(int i=0; i< neighbours.size();i++){
			Tile t=neighbours.get(i);
			if(t.getCharacters().size()>0 && t!=player.getTile()){
				neighbours.remove(i);
				i--;
			}
		}
		//checks surroundings to see which is the tile with most materials
		for(Tile t: neighbours){
			int base=t.getMaterialQtt();
			int real= (int) (base + t.getBaseTile().getExploreMod()*base);
			if(real == max && t.getBaseTile().getMoveCost()<body.getEnergyPoints()){
				options.add(t);
				max=t.getMaterialQtt();
			}
			else if(real>max && t.getBaseTile().getMoveCost()<body.getEnergyPoints()){
				options.clear();
				options.add(t);
				max=t.getMaterialQtt();				
			}
		}
		//checks if the current tile is better for exploring 
		if(body.getTile().getMaterialQtt()>=max){
			return body.getTile();
		}//if not, moves to a more profitable tile
		else{
			double r=Math.random()*options.size();
			int i = (int)r;
			return options.get(i);
		}		
	}
	
	/**
	 Selects a random position for the NPC to move to, while avoiding backtracking.*/	 
	public void wander(){
		Gdx.app.log(TAG, "Wandering...");
		//lastKnownPosition=null; //if we wanted to use it, we would have by now...
		
		//gets a random tile for the NPC to move to
		Tile toMove=getRandomMovePosition();	
		if(toMove==null) return;
		Action a=new Move(body, toMove);
		if(a.validate(gameState)){
			action= a;
		}
	}	
	
	/**
	 * Charges at the enemy by following the fastest path to him.
	 * Uses A* algorithm for finding the best path.
	 * Resets the counter for the number of turns we've been searching*/	
	public void attack(){
		//if we're attackin, we know where the enemy is and reset the turnsSeaching counter.
		turnsSearching=0;
		Gdx.app.log(TAG, "attacking...");
		//if we dont have a path yet, the path is now empty, or the path destination is not the target position
		if(path==null || path.size()==0 || path.get(0)!=target.getTile())
			//calculate a new pathto the target based on the A* Pathfinding Algorithm
			path = A_Star.getPathToTile(body.getTile(), target.getTile(), gameState.getMap());
		if(path.size()==0){
			return;
		}
		Tile toMove=path.get(path.size()-1);
		if(toMove.getCharacters().size()>0  &&  toMove!=player.getTile())
			path = A_Star.getPathToTile(body.getTile(), target.getTile(), gameState.getMap());
		
		//If no path was found, do nothing
		if(path.size()==0){
			return;
		}
		//move the NPC to the next point in the path
		Action a=new Move(body, toMove);
		if(a.validate(gameState)){
			action= a;
			path.remove(path.size()-1);
		}	
	}
	
	public void fight(){
		Gdx.app.log(TAG, "Fighting...");
		//TODO: Fight...if that's even necessary...
		action=null;
	}
	
	/**
	 * Has the NPC choosing a path that gets him away from the player.
	 */
	public void run(){
		Gdx.app.log(TAG, "Running...");
		Tile toMove = getTileAwayFromTarget();
		Action a=new Move(body,toMove);
		if(a.validate(gameState)){
			action= a;
		}	
	}
	
	/**
	 * Has the NPC check surrounding tiles for the best one to explore.
	 * He will move to surrounding tile if it can produce better results, or
	 * explore the one he's in otherwise.
	 */
	public void explore(){
		Gdx.app.log(TAG, "Exploring...");
		//Gets all the neighbouring tiles
		Tile moveTo= this.getExplorableTile();
		if(moveTo==body.getTile()){
			Action a=new Explore(body);
			if(a.validate(gameState))
				action= a;
		}
		else{
			Action a=new Move(body,moveTo);
			if(a.validate(gameState))
				action= a;			
		}			
	}
	
	/**
	 * Has he NPC searching for the player location.
	 * First he moves to the last location where the player was found.
	 * Then he starts moving at random in hopes of finding him...
	 */
	public void search(){
		Gdx.app.log(TAG, "Searching..." + "LastKnownPosition: " + lastKnownPosition.getPosition());
		
		if(!locationAchieved && (path==null || path.size()==0|| path.get(path.size()-1).getCharacters().size()>0))
			path = A_Star.getPathToTile(body.getTile(), lastKnownPosition, gameState.getMap());
		else if(locationAchieved && (path==null || path.size()==0 || path.get(path.size()-1).getCharacters().size()>0)){
			Vector2 direction=body.getTile().getPosition().cpy().sub(body.getPreviousPosition().getPosition());
			Vector2 newTarget=body.getTile().getPosition().cpy().add(direction).scl(2);
			if(gameState.getMap().getTiles().containsKey(newTarget))
				path = A_Star.getPathToTile(body.getTile(), gameState.getMap().getTiles().get(newTarget), gameState.getMap());
		}
		if(path==null || path.size()==0){
			wander(); 
			return;
		}
		Action a=new Move(body,path.get(path.size()-1));
		if(a.validate(gameState)){
			action= a;
			path.remove(path.size()-1);
		}
		
		turnsSearching++;
	}
	
	/**
	 * Makes the NPC rest. Simply calls his rest().*/	 
	public void rest(){
		Gdx.app.log(TAG, "Resting..." + body.getEnergyPoints());
		Action a=new Rest(body);
		if(a.validate(gameState))
			action= a;
		else
			explore();
	}
	
	public void heal() {		
		Gdx.app.log(TAG, "Healing...");
		
		//Search for items with heal abilities in them
		ArrayList<Item> possibleItems=new ArrayList<Item>();
		for(Entry<String, Item> entry :Item.items.entrySet()){
			Item i=entry.getValue();
			if(i.getEffects().contains(Effects.USE_RECOVER_LIFE)){
				possibleItems.add(i);
			}
		}//Check if any of the previous items is in the inventory
		for(Item i: possibleItems){
			if(body.getItems().containsKey(i.getID())){
				Action a=new Use(body,i);
				if(a.validate(gameState))
					action= a;
				return;
			}
		}//If no item was found...
		for(Item i: possibleItems){
			if(body.getCurrentOil()>=i.getCreationCost()){
				Action a=new Craft(body,i,true);
				if(a.validate(gameState))
					action= a;
				return;
			}
		}//If we simply can't craft an item...
		Tile moveTo=this.getExplorableTile();
		//Let's get exploring...
		if(moveTo==body.getTile()){
			Action a=new Explore(body);
			if(a.validate(gameState))
				action= a;
		}
		else{
			Action a=new Move(body,moveTo);
			if(a.validate(gameState))
				action= a;
		}
	}

	public void patrol() {
		// TODO Auto-generated method stub
		Gdx.app.log(TAG, "Patroling...");
		action=null;
	}

	public void die() {
		// TODO Auto-generated method stub
		Gdx.app.log(TAG, "Dying...");
		action=null;
	}

	/**
	 * Has the NPC keeping a distance of 2 tiles from the player.
	 */
	public void tail() {
		Gdx.app.log(TAG, "Tailing...");
		int dist = HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition());
		
		//if player got too close, move away from him
		if(dist<2){
			Tile toMove = getTileAwayFromTarget();
			Action a = new Move(body,toMove);
			if(a.validate(gameState)){
				action=a;
			}
		}//if he got too far, get closer
		else if(dist>2){
			Tile toMove = getTileCloserToTarget();
			if(toMove==null) return;
			Action a = new Move(body,toMove);
			if(a.validate(gameState)){
				action=a;
			}
		}//Else take the opportunity to do something else... 
//		else{
//			//TODO: that something else
//			rest();
//		}
	}

	public void camp() {
		Gdx.app.log(TAG, "Camping...");
		//Searches the items in the current tile for a fire
		for(Entry<String, Item> entry : body.getTile().getItems().entrySet()){
			Item i=entry.getValue();
			if(i.getID()=="cmp_001"){
				Action a = new Rest(body);
				if(a.validate(gameState)){
					action=a;
					return;
				}
			}
		}//If a fire wasn't found, then try to place one.
		if(body.getCurrentOil()>=Item.items.get("cmp_001").getCreationCost()){
			Action a = new Craft(body,Item.items.get("cmp_001"),true);
			if(a.validate(gameState)){
				action=a;
			}
			Gdx.app.log(TAG, "Built a wonderfull fire");
		}
	}
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/
	@Override
	public void write(Json json) {
		if(lastKnownPosition!=null)
			json.writeValue("lastKnownPosition",lastKnownPosition.getPosition());
		json.writeValue("turnsSearching",turnsSearching);
		json.writeValue("playerLastSeen",playerLastSeen);
		json.writeValue("locationAchieved",locationAchieved);
//		protected Character target;
//		protected ArrayList<Tile> path;
//		protected Character player;
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		
	}
	
	public enum BehaviourType{
		SCAVENGER(),
		SCARED,
		NEUTRAL,
		ASSASSIN,
		BERSERKER,
		GUARD();
	}
}
