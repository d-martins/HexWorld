package com.ename.diogo.martins.survival;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

public class DayNightCycle {

	static final Color morn=new Color(1,1,1,1);
	static final Color dusk=new Color(.8f,0.4f,0.4f,1);
	static final Color dawn=new Color(.8f,1f,1f,1);
	static final Color night=new Color(.4f,0.4f,0.8f,1);
	
	int dayDuration;
	int nightDuration;
	
	int hour;
	Color tint;
	
	boolean isDay;
	boolean cycle;
	
	public DayNightCycle(int dayDur, int nightDur) {
		isDay=true;
		dayDuration=dayDur;
		nightDuration=nightDur;
		
		cycle=true;
		if(dayDuration==0 || nightDuration==0)
			cycle=false;
		if(!cycle && dayDuration==0)
			isDay=false;			
		
		hour=1;
		calculateTint();
	}
	
	public boolean isDay(){return isDay;}
	public int getHour(){return this.hour;}	
	public Color getTint(){return this.tint;}
	
	public void setIsDay(boolean b){isDay=b; calculateTint();}
	public void setHour(int i){this.hour=i; calculateTint();}
	
	
	
	public void Update(){		
		if(!cycle)
			return;
		
		hour++;
		if(isDay && dayDuration<hour){
			hour=1;
			isDay=false;
		}
		else if(!isDay && nightDuration<hour){
			hour=1;
			isDay=true;
		}		
		
		calculateTint();
	}
	
	private void calculateTint(){
		Interpolation i=Interpolation.linear;
//		float r=1,g=1,b=1;
		if(isDay){
			tint=morn;
			if(cycle && hour >= dayDuration*.75f){
//				tint=dusk;
				float r=i.apply(morn.r,dusk.r,hour/(float)dayDuration);
				float g=i.apply(morn.g,dusk.g,hour/(float)dayDuration);
				float b=i.apply(morn.b,dusk.b,hour/(float)dayDuration);
				tint=new Color(r,g,b,1);
			}
		}else{
			tint=night;
			if(cycle && hour >=nightDuration*.75f){
//				tint=dawn;
				float r=i.apply(night.r,dawn.r,hour/(float)nightDuration);
				float g=i.apply(night.g,dawn.g,hour/(float)nightDuration);
				float b=i.apply(night.b,dawn.b,hour/(float)nightDuration);
				tint=new Color(r,g,b,1);
			}
		}
		
		/*
		if(hour>=0 && hour <=3){
			r=morn.r;//i.apply(morn.x,dusk.x,hour/3f);
			g=morn.g;//i.apply(morn.y,dusk.y,hour/3f);
			b=morn.b;//i.apply(morn.z,dusk.z,hour/3f);
		}else if(hour>=4 && hour<=5){
			r=i.apply(morn.r,dusk.r,(hour-3)/3f);
			g=i.apply(morn.g,dusk.g,(hour-3)/3f);
			b=i.apply(morn.b,dusk.b,(hour-3)/3f);
		}
		else if(hour>=6&& hour<=9){
			r=night.r;//i.apply(morn.x,dusk.x,hour/3f);
			g=night.g;//i.apply(morn.y,dusk.y,hour/3f);
			b=night.b;//i.apply(morn.z,dusk.z,hour/3f);
		}
		else{
			r=i.apply(dawn.r,night.r,(12-hour)/4f);
			g=i.apply(dawn.g,night.g,(12-hour)/4f);
			b=i.apply(dawn.b,night.b,(12-hour)/4f);
		}*/
		
//		tint =new Color(r,g,b,1);
	}
	


}
