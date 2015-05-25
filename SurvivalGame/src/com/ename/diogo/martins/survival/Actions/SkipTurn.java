package com.ename.diogo.martins.survival.Actions;

import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;

public class SkipTurn extends Action {

	public SkipTurn(Character actor) {
		super(actor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		character.setResting(false);
	}

	@Override
	public boolean validate(Game_State stt) {
		// TODO Auto-generated method stub
		return true;
	}

}
