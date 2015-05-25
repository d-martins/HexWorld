package com.ename.diogo.martins.survival.Characters;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class BaseNPC {
	private static String TAG="BASE_NPC";
	
	static HashMap<String,BaseNPC> enemies;
	String name;
	String type;
	int str;
	int end;
	int intel;
	int oil;
	int points;
	String[] items;	
	
	private BaseNPC() {
		
	}
	
	static{
		enemies= new HashMap<String,BaseNPC>();
		loadEnemies();
	}
	
	public static void loadEnemies(){
		
		JsonReader jsonReader=new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal("data/config/enemies.json"));
		for(JsonValue j=map.child(); j!=null; j=j.next){
//			Gdx.app.log(TAG, "" + j.name);
			BaseNPC n= new BaseNPC();
			n.name=j.name;
			n.type=j.getString("type");
			n.str=j.getInt("str");
			n.end=j.getInt("end");
			n.intel=j.getInt("int");
			n.oil=j.getInt("oil");
			n.items=j.get("items").asStringArray();
			n.points= j.getInt("points");
			enemies.put(n.name, n);
		}
	}
}
