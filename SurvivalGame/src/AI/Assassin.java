package AI;

import utils.HexMath;

import com.badlogic.gdx.Gdx;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Characters.NPC.State;

public class Assassin extends Behaviour {
	private static String TAG="ASSASSIN";
	//private int playerLastSeen;
	
	public Assassin(NPC n, Game_State stt) {
		super(n, stt);
	}
	
	@Override
	public Action getAction(){
		//if the NPC has no health
		if(body.getHealthPoints()<=0)
			body.setState(State.DYING);//he dies
		//if he and the layer are in the same tile
		else if(player.getTile()==body.getTile())
			body.setState(State.FIGHTING);//they fight
		else if(body.getEnergyPoints()<=0)
			body.setState(State.RESTING);
		return super.getAction();
	}
	
	@Override
	public void wander(){
		//if the player has come in visible range on the last turn, start tailing immediately
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.TAILING);
			tail();
			return;
		}
		//if not, just wander arround
		super.wander();
		//if after moving the player is in range, start tailing next turn
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.TAILING);
			return;
		}//if NPC is getting below average energy, he rests...Keep his energy up at all times
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(State.RESTING);
			return;
		}//if he's running low on oil, go look for some
		if(body.getCurrentOil()<=3){
			body.setState(State.EXPLORING);
			return;
		}	
	}
	
	@Override
	public void tail(){
		Gdx.app.log(TAG, "tailing...");
		//If the player is no longer visible,  search for him
		if(!body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.SEARCHING);
			//TODO:lastKnownLocation=path.get(0);
			search();
			return;
		}
		//if the player is really low on health, attack him
		if(player.getEnergyPoints()<=player.getEnergyIntervals()[0]){
			body.setState(State.ATTACKING);
			attack();
			return;
		}
		//TODO: take more opportunities...
		super.tail();
		//if after NPC moved, the player disappeared, search next turn 
		if(!body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.SEARCHING);
			//TODO:lastKnownLocation=path.get(0);
			return;
		}//if getting  below average energy, he rests...
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(State.RESTING);
			return;
		}//if low on oil, search for some next turn	
		if(body.getCurrentOil()<=3){
			body.setState(State.EXPLORING);
			return;
		}
	}

	@Override
	public void explore(){
		//If the player gets too close and we have energy, tail him (get away from him);
		if(HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition())<2){
			if(body.getEnergyPoints()>=body.getEnergyIntervals()[0]){
				body.setState(State.TAILING);
				tail();
				return;
			}
		}
		super.explore();
		//if oil reserves are reasonable again, go back to what we were doing
		if(body.getCurrentOil()>=6){
			if(body.getVisibleTiles().contains(player.getTile())){
				body.setState(State.TAILING);
				return;
			}
			if(body.getPreviousState()==State.SEARCHING){
				body.setState(State.SEARCHING);
				return;
			}
			body.setState(State.WANDERING);
			return;
		}//if energy getting below average, start resting
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(State.RESTING);
			return;
		}			
	}
	
	@Override
	public void attack(){
		//if player is no longer visible, search for him
		if(!body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.SEARCHING);
			search();
			return;
		}
		super.attack();
		
		//if player has more energy than NPC, stop attackng, start tailing
		if(player.getEnergyPoints()>body.getEnergyPoints()){
			body.setState(State.TAILING);
			return;
		}//if player not visible after NPC moves, start searching for him
		if(!body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.SEARCHING);
			return;
		}
	}
	
	@Override
	public void fight(){
		if(body.getTile()!=player.getTile()){
			body.setState(State.TAILING);
			tail();
			//TODO: Check if this is best option...
			return;
		}
		super.fight();
		//When low on health, start running
		if(body.getHealthPoints()<=2){
			body.setState(State.RUNNING);
			return;
		}
	}
	
	@Override
	public void run(){
		super.run();		
		if(playerLastSeen>=3){//if player not seen for 3 turns...
			//if low on health, start healing
			if(body.getHealthPoints()<=body.getBaseHealth()/2){
					body.setState(State.HEALING);
					return;
				}
			//else, start wandering
				body.setState(State.WANDERING);
				return;	
		}
		//if low on energy, rest
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}
	}
	
	@Override
	public void search(){
		Gdx.app.log(TAG, "seaching...");
		//if player is visible again, start tailing him, forget searching
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.TAILING);
			tail();
			return;
		}
		super.search();
		//if player is back in visible range, start tailing him again
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.TAILING);
			return;
		}//if we've been looking for 10turns, stop the searches, it's hopeless
		if(turnsSearching>=10){
			body.setState(State.WANDERING);
			return;
		}
	}
	
	@Override
	public void heal(){
		super.heal();
		//If we're sorta fixed up, we're good to go
		if(body.getHealthPoints()>body.getBaseHealth()/2){
			body.setState(State.WANDERING);
			return;
		}
		//If we're running out of energy, need to get some rest... 
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(State.RESTING);
			return;
		}
	}
	
	@Override
	public void rest(){
		//If the player gets too close and we have energy, tail him (get away from him);
		if(HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition())<2){
			if(body.getEnergyPoints()>=body.getEnergyIntervals()[0]){
				body.setState(State.TAILING);
				tail();
				return;
			}
		}
		super.rest();
		//if we were running and we have some energy left, resume the running
		if(body.getPreviousState()==State.RUNNING){
			if(body.getEnergyPoints()>=body.getEnergyIntervals()[1]){
				body.setState(State.RUNNING);
				return;
			}
		}		
		//if we weren't just keep calm and rest up
		if(body.getEnergyPoints()>=body.getBaseEnergy()){
			body.setState(body.getPreviousState());
			return;
		}
	}
}
