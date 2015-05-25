package AI;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Characters.NPC.State;

public class Neutral extends Behaviour {
	
	public Neutral(NPC n, Game_State stt) {
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
		//If no more energy remains, nothing to do but rest
		else if(body.getEnergyPoints()<=0)
			body.setState(State.RESTING);
		
		return super.getAction();
	}
	
	@Override
	public void wander(){
		super.wander();	
		//If getting low on energy
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);//TODO:you should camp...
			return;
		}//If getting low on oil reserves, go exploring a bit
		if(body.getCurrentOil()<3){
			body.setState(State.EXPLORING);
			return;
		}
	}
	
	@Override
	public void explore(){
		super.explore();
		//If oil is over half capacity, we got plenty. Back to wandering...
		if(body.getCurrentOil()>body.getCapacity()/2){
			body.setState(State.WANDERING);
			return;
		}//If getting low on energy, time to rest.
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}
	}
	
	@Override
	public void rest(){
		//If we were healing and now we see the player...RUN!
		if(body.getPreviousState()==State.HEALING && body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.RUNNING);
			run();
			return;
		}
		super.rest();
		//If we were healing and now we see the player...RUN!
		if(body.getPreviousState()==State.HEALING && body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.RUNNING);
			return;
		}//If energy is back to full, resume the previous state
		if(body.getEnergyPoints()>=body.getBaseEnergy()){
			body.setState(body.getPreviousState());
			return;
		}
	}
	
	@Override
	public void run(){
		//If player is out of sight
		if(!body.getVisibleTiles().contains(player.getTile())){
			//If low health, set state to healing
			if(body.getHealthPoints()<=body.getBaseHealth()/2){
				body.setState(State.HEALING);
				return;
			}//Else go and wander around
			body.setState(State.WANDERING);
			return;
		}
		super.run();
		//If player is out of sight
		if(!body.getVisibleTiles().contains(player.getTile())){
			//If low health, set state to healing
			if(body.getHealthPoints()<=body.getBaseHealth()/2){
				body.setState(State.HEALING);
				return;
			}//Else go and wander around
			body.setState(State.WANDERING);
			return;
		}
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
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
		if(body.getHealthPoints()<=2){
			body.setState(State.RUNNING);
			return;
		}
		if(player.getTile()!=body.getTile()){
			body.setState(State.WANDERING);
			return;
		}
	}

}
