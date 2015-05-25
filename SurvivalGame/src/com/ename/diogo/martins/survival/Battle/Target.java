package com.ename.diogo.martins.survival.Battle;

import com.badlogic.gdx.Gdx;

public class Target {
	private static final String TAG="TARGET";
	
	float duration;
	float spawnTime;
	float timeSinceSpawn;
	boolean isFixed;	
	State state;
	//boolean isVisible;
	//boolean isPopped;
	
	public Target(float targetDuration, float spawnTiming, boolean fixedTarget) {
		this.duration=targetDuration;
		this.spawnTime=spawnTiming;
		this.isFixed=fixedTarget;
		this.timeSinceSpawn=0;
		this.state=State.WAITING;
		//this.isVisible=false;
		//this.isPopped=false;
		
		//Gdx.app.log(TAG,"time of spawn: " + spawnTiming);
	}
	
	public void spawn(){
		//Gdx.app.log(TAG, "target spawned " + this);
		//isVisible=true;
		if(state == State.WAITING){
			state=State.PLAYING;
			timeSinceSpawn=0;
		}
	}
	
	public void pop(){
		Gdx.app.log(TAG, "target popped " + this);
		state=State.POPPING;
		//isVisible=false;
		//isPopped=true;
	}
	
	public void update(float deltaTime){

		timeSinceSpawn+=deltaTime;
		if(timeSinceSpawn >= duration && state==State.PLAYING){
			state=State.DESTROYED;
		}
	}
	
	enum State{
		WAITING,
		PLAYING,
		POPPING,
		POPPED,
		DESTROYED;
	}

}
