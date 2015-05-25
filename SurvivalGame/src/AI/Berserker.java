package AI;


import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Characters.NPC.State;

public class Berserker extends Behaviour {
	
	public Berserker(NPC n, Game_State stt) {
		super(n, stt);
	}
	
	@Override
	public Action getAction(){
		//if health reaches 0...
		if(body.getHealthPoints()<=0)
			body.setState(State.DYING);//he dies
		//if player and NPC on the same tile
		else if(player.getTile()==body.getTile())
			body.setState(State.FIGHTING);//they fight
		//if fresh out of energy
		else if(body.getEnergyPoints()<=0)
			body.setState(State.RESTING);//he rests
		return super.getAction();
	}
	
	@Override
	public void wander(){
		//If player come in visible range, attack, no questions asked
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.ATTACKING);
			attack();
			return;
		}
		super.wander();
		//If after moving, player is back in sight, attack him (next turn)
		if(body.getVisibleTiles().contains(player.getTile())){
			target=player;
			body.setState(State.ATTACKING);
			return;
		}//If energy getting medium low, rest. We need to be fresh for the hunt!
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[1]){
			body.setState(NPC.State.RESTING);
			return;
		}
	}
	
	@Override
	public void attack(){
		//If the player mysteriously disappeared, start searching for him
		if(!body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.SEARCHING);
			search();
			return;
		}
		//If, in our brave charge, we lost the player from sight, set the state to search for next turn
		super.attack();
		if(!body.getVisibleTiles().contains(target.getTile())){
			body.setState(State.SEARCHING);
			return;
		}//If no more energy left, stop and rest
		if(body.getEnergyPoints()<=0){
			body.setState(NPC.State.RESTING);
			return;
		}
	}
	
	@Override
	public void fight(){
		//if the player broke the fight, attack him
		if(target.getTile()!=body.getTile()){
			body.setState(State.ATTACKING);
			return;
		}
		super.fight();		
	}
	
	@Override
	public void rest(){
		//The damage is irrelevant, we will charge until we die! 
		if(body.getEnergyPoints()>0 && body.getVisibleTiles().contains(player.getTile())){
			if(body.getEnergyPoints()>body.getEnergyIntervals()[0]){
				body.setState(State.ATTACKING);
				attack();
				return;
			}
		}
		super.rest();
		//If we were attacking and we got some energy under the belt...
		if(body.getPreviousState() == State.ATTACKING ){			
			if(body.getEnergyPoints()>body.getEnergyIntervals()[0]){
				//Resume the hunt!
				body.setState(State.ATTACKING);
				return;				
			}
		}
		//If we were searching for the target and are back to a reasonable energy level...
		if(body.getPreviousState()==State.SEARCHING){
			if(body.getEnergyPoints()>=body.getEnergyIntervals()[1]){
				//Resume searching!
				body.setState(State.SEARCHING);
				return;
			}
		}//Otherwise, f health is back to full, go back to wandering
		else if(body.getEnergyPoints()>=body.getBaseEnergy()){
			body.setState(State.WANDERING);
			return;			
		}
	}
	
	@Override
	public void search(){
		//If player came back in visible range, resume the hunt
		if(body.getVisibleTiles().contains(player.getTile())){
			body.setState(State.ATTACKING);
			attack();
			return;
		}
		super.search();
		//If the player came back in vsible range, set state to attacking
		if(body.getVisibleTiles().contains(target.getTile())){
			body.setState(State.ATTACKING);
			return;
		}//If search has been going on for 10 turns or more, give up.
		if(turnsSearching>=10){
			body.setState(State.WANDERING);
			return;
		}//If energy getting low, stop and rest
		if(body.getEnergyPoints()<=body.getEnergyIntervals()[0]){
			body.setState(State.RESTING);
			return;
		}		
	}

}
