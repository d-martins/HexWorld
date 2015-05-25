package com.ename.diogo.martins.survival.Maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.DayNightCycle;

public class Base_Map implements Json.Serializable{
	
//	public static Map<MapType,Base_Map> BaseMaps=new HashMap<MapType,Base_Map>();
	private static final String PATH="data/config/maps/baseMaps.json";
	static String TAG="BASE_MAP";

	private String id;
	private int level;
	private String enemySpawner;
	private int dayDuration;
	private int nightDuration;
	
	/*-------------------------------------------------------*/
	/*						Constructor						 */
	/*-------------------------------------------------------*/
	public Base_Map(String id , int level){
		LoadBaseMap(id,level);
		this.id=id;
		this.level=level;
	}
	
	public Base_Map(){
		
	}
	
	/*-------------------------------------------------------*/
	/*						Getters							 */
	/*-------------------------------------------------------*/
	
	public DayNightCycle getDayNightCycle(){return new DayNightCycle(dayDuration, nightDuration);}
	public EnemySpawner getEnemySpawner(){return new EnemySpawner(enemySpawner);}
	public String getID(){return id;}
	public int getLevel(){return level;}
	
	/*-------------------------------------------------------*/
	/*						Methods							 */
	/*-------------------------------------------------------*/
	
	private void LoadBaseMap(String name, int level){
		JsonReader jsonReader= new  JsonReader();
		JsonValue map = jsonReader.parse(Gdx.files.internal(PATH));
		
		JsonValue base=map.get(name);
		String lvl="level-"+level;
		if(base.has(lvl))
			base=base.get(lvl);
		else
			base=base.get(base.size-1);

		this.dayDuration=base.getInt("dayDuration");
		this.nightDuration=base.getInt("nightDuration");
		this.enemySpawner=base.getString("enemySpawner");
		
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/

	@Override
	public void write(Json json) {
		json.writeValue("id", id);
		json.writeValue("level", level);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		this.id=jsonData.getString("id");
		this.level=jsonData.getInt("level");
		
		Base_Map b=new Base_Map(id,level);
		this.enemySpawner=b.enemySpawner;
		this.dayDuration=b.dayDuration;
		this.nightDuration=b.nightDuration;
	}	
}

