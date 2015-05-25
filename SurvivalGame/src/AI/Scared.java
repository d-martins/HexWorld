package AI;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Characters.NPC.State;

public class Scared extends Behaviour {
	//private static String TAG="SCARED";
	
	public Scared(NPC n, Game_State stt) {		
		super(n, stt);
	}
	
	@Override
	public Action getAction(){
		//If the NPC has no health
		if(body.getHealthPoints()<=0)
			body.setState(State.DYING);//he dies
		else{
			//If he and the layer are in the same tile
			if(player.getTile()==body.getTile())
				body.setState(State.FIGHTING);//they fight
			//If player is in the visible field, start running
			if(body.getVisibleTiles()==null)
				body.setVisibleTiles(gameState.getVisibleTiles(body));
			if(body.getVisibleTiles().contains(player.getTile()))
				body.setState(State.RUNNING);
			if(body.getEnergyPoints()<=0)
				body.setState(State.RESTING);
		}
		return super.getAction();
	}
	
	@Override
	public void wander(){
		super.wander();		
		//If, after moving, the player is spotted: Ruuuun!
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.RUNNING);
			return;
		}//If getting low on energy, rest
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}//If getting low on oil, explore
		if(body.getCurrentOil()<=3){
			body.setState(State.EXPLORING);
			return;
		}
	}
	
	@Override
	public void explore(){
		super.explore();
		//If, after moving, the player is spotted: Ruuuun!
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.RUNNING);
			return;
		}//if getting low on energy, rest
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}
	}
	
	@Override
	public void rest(){
		super.rest();
		//If we were running and we got decent energy, go back to running
		if(body.getPreviousState()==State.RUNNING){
			if(body.getEnergyPoints()>=body.getEnergyIntervals()[1]){
				body.setState(State.RUNNING);
				return;
			}
		}//If back to full health, go back to wandering
		if(body.getEnergyPoints()>=body.getBaseEnergy()){
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
	public void fight(){
		super.fight();
		body.setState(State.RUNNING);
		return;
	}
	
	@Override
	public void run(){
		super.run();
		if(playerLastSeen>=3){
			//if low on health, start healing
			if(body.getHealthPoints()<=body.getBaseHealth()/2){
					body.setState(State.HEALING);
					return;
				}
			//else, start wandering
				body.setState(State.WANDERING);
				return;	
		}
		if(body.getEnergyPoints()<=0){
			body.setState(State.RUNNING);
			return;
		}
			
	}

}
