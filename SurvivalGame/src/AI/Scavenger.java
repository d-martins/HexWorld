package AI;

import utils.HexMath;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Characters.NPC.State;

public class Scavenger extends Behaviour {
	
	public Scavenger(NPC n, Game_State stt) {
		super(n, stt);
		body.setState(State.EXPLORING);
	}
	
	@Override
	public Action getAction(){
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
	public void explore(){
		int dist=HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition());
		//If the player gets too close, we gonna bite him
		if(dist==1){
			body.setState(State.ATTACKING);
			attack();
			return;
		}
		super.explore();
		//If getting low on energy, start camping
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.CAMPING);
			return;
		}
	}
	
	@Override
	public void camp(){
		int dist=HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition());
		//If the player gets too close, we gonna bite him
		if(dist==1){
			body.setState(State.ATTACKING);
			attack();
			return;
		}
		super.camp();
		body.setState(State.RESTING);
		return;
	}
	
	@Override
	public void rest(){
		//if we get some energy and the player gets too close...
		if(body.getEnergyPoints()>=body.getEnergyIntervals()[1]){
			int dist=HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition());
			//we trying to get some sleep over here! Attack!
			if(dist==1){
				body.setState(State.ATTACKING);
				attack();
				return;
			}
		}
		super.rest();
		if(body.getPreviousState()==State.RUNNING && body.getEnergyPoints()>=body.getEnergyIntervals()[1]){
			body.setState(State.RUNNING);
			return;
		}
		if(body.getEnergyPoints()>=body.getBaseEnergy()){
			if(body.getPreviousState()==State.HEALING){
				body.setState(State.HEALING);
				return;
			}
			body.setState(State.EXPLORING);
			return;
		}
	}
	
	@Override
	public void attack(){
		//If the player isn't too close anymore, just let him go, more important stuff to get done...
		int dist=HexMath.HexDistance(player.getTile().getPosition(), body.getTile().getPosition());
		if(dist>1){
			body.setState(State.EXPLORING);
			explore();
			return;
		}
		super.attack();		
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.CAMPING);
			return;
		}		
	}
	
	@Override
	public void heal(){
		super.heal();
		//If we're sorta fixed up, we're good to go
		if(body.getHealthPoints()>body.getBaseHealth()/2){
			body.setState(State.EXPLORING);
			return;
		}
		//If we're running out of health, need to get some rest... 
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(State.RESTING);
			return;
		}
	}
	
	@Override 
	public void fight(){
		//If we're not fighting anymore...
		if(gameState.getPlayer().getTile()!= body.getTile()){
			body.setState(State.EXPLORING);
			return;
		}
		super.fight();
		//TODO: check if player can be killed in next turn
		
		//If health is getting low, run, run like the wind
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
				body.setState(State.EXPLORING);
				return;	
		}
		//if low on energy, rest
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}
	}

}
