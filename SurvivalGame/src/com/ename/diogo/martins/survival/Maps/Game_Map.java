package com.ename.diogo.martins.survival.Maps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import utils.HexMath;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Game_Map implements Json.Serializable{
	static String TAG="GAME_MAP";
	private static final String PATH="data/config/maps/" ;
	
	private Base_Map baseMap;
	private Map <Vector2,Tile> tiles;
	private Tile objectiveTile;
	private Tile startingTile;
	private int columns;
	private int rows;
	
	/*-------------------------------------------------------*/
	/*						Constructor						 */
	/*-------------------------------------------------------*/
	public Game_Map(String id, int level){
		this.tiles=new HashMap<Vector2,Tile>();
		//this.tiles=new HashMap<Vector2,Tile>();
		this.LoadMap(id, level);		
	}
	public Game_Map(){
		this.tiles=new HashMap<Vector2,Tile>();
	}
	
	/*-------------------------------------------------------*/
	/*						Accessors						 */
	/*-------------------------------------------------------*/	
	public Base_Map getBaseMap(){return baseMap;}
	public Map<Vector2,Tile> getTiles(){return tiles;}
	public int getRows(){return rows;}
	public int getColumns(){return columns;}
	public Tile getStartingTile(){return startingTile;}
	public Tile getObjectiveTile(){return objectiveTile;}
	/*-------------------------------------------------------*/
	/*						 Methods						 */
	/*-------------------------------------------------------*/	
	public void LoadMap(String id, int level){
		JsonReader jsonReader=new JsonReader();
		JsonValue map = jsonReader.parse(Gdx.files.internal(PATH+id+".json"));
		
		this.baseMap= new Base_Map(map.getString("baseMap"),level);
		this.rows= map.getInt("rows");
		this.columns= map.getInt("columns");
		
		for(JsonValue tile = map.getChild("tiles"); tile!=null; tile=tile.next){
//			Gdx.app.log(TAG, ""+ tile);
			String type = tile.getString("type");
			String code = tile.getString("code");
			int x = tile.getInt("x");
			int y = tile.getInt("y");
			int [] coords = HexMath.offsetToAxialCoordinates(x, y);
			
			Tile t= new Tile(type, code, coords[0], coords[1]);
			
			if(tile.has("isObjective")){
				objectiveTile=t;
				t.setObjecctive(true);
			}
			else if(tile.has("isStart")){
				startingTile=t;
				
			}
			
			tiles.put(new Vector2(coords[0], coords[1]), t);
		}
	}
	
	public static Game_Map getRandomMap(int level){
		JsonReader jsonReader =  new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal(PATH+"maps.json"));
		
		if(map.size<level)
			level=map.size;
		
		map=map.get("level-"+level);
		String[] maps = map.asStringArray();
		
		int r= (int)(Math.random()*maps.length);
		
		return new Game_Map(maps[r], level);
	}

	public ArrayList<Tile> getTileNeighours(Tile t){
		Vector2[] ring = HexMath.FindHexesOfRing(t.getPosition(), 1);
		ArrayList<Tile> result=new ArrayList<Tile>();
		for(Vector2 v : ring){
			if(tiles.containsKey(v))
				result.add(tiles.get(v));
		}
		return result;
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/

	@Override
	public void write(Json json) {
		json.writeValue("baseMap", baseMap);//TODO: baseMap serialize
		json.writeValue("rows", rows);
		json.writeValue("cols", columns);
		json.writeValue("tiles",tiles.values());
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		this.baseMap=json.fromJson(Base_Map.class, jsonData.get("baseMap").toString());
		this.rows=jsonData.getInt("rows");
		this.columns=jsonData.getInt("cols");
		
		for(JsonValue tile=jsonData.getChild("tiles");tile!=null;tile=tile.next){
			Tile t= json.fromJson(Tile.class, tile.toString());
			if(t.isObjective())
				this.objectiveTile=t;
			else if(t.isStart())
				this.startingTile=t;
			tiles.put(t.getPosition(), t);
		}
	}
}

