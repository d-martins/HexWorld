package com.ename.diogo.martins.survival.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;


public class Base_Tiles {
	public static Map<String,Base_Tiles> baseTiles=new HashMap<String,Base_Tiles>();
	static String TAG="BASE_TILES";
	
	private String type;
	private int moveCost;
	private int fovBlock;
	private float spawnMod;
	private int fieldOfViewMod;
	private float foodConsumptionMod;	
	private int baseMaterials;
	private int exploreMod;
	private int defenseBonus;
	private int attackBonus;
	private int maxMaterials;
	private int materialRecoveryRate;
	private boolean canCamp;
	
	/*-------------------------------------------------------*/
	/*						Constructor						 */
	/*-------------------------------------------------------*/
	
	private Base_Tiles(){}
	
	/*-------------------------------------------------------*/
	/*						Getters							 */
	/*-------------------------------------------------------*/
	public String 	getType()		{return this.type;}	
	public int 		getMoveCost()	{return this.moveCost;}
	public int 		getFovBlock()	{return this.fovBlock;}
	public float 	getSpawnMod()	{return this.spawnMod;}
	public int 		getFovMod()		{return this.fieldOfViewMod;}
	public float 	getFoodConsumMod(){return this.foodConsumptionMod;}
	public int 		getBaseMaterials(){return this.baseMaterials;}
	public int 	getExploreMod()	{return this.exploreMod;}
	public int 		getDefBonus()	{return this.defenseBonus;}
	public int 		getAtkBonus()	{return this.attackBonus;}
	public int 		getMaxMaterials(){return this.maxMaterials;}
	public int 		getMaterialRecoveryRate()	
									{return this.materialRecoveryRate;}	
	public boolean 	canCamp()		{return canCamp;}
	
	
	/*-------------------------------------------------------*/
	/*						Methods							 */
	/*-------------------------------------------------------*/
	/*public*/ static /*void LoadTileInfo()*/{
		Gdx.app.log(TAG, "BaseTiles");
		Json json = new Json();
		
		ArrayList<Base_Tiles> b = json.fromJson(ArrayList.class, Base_Tiles.class ,Gdx.files.internal("data/config/tile_info.json"));
		for(Base_Tiles t : b){
			baseTiles.put(t.type, t);
			
		}
		//baseTiles.put(b.getType(), b);
		/*JsonReader jsonReader=new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal("data/config/tile_info.json"));
		for(JsonValue entry= map.child; entry != null; entry = entry.next)
		{			
			for(JsonValue tile=entry.child;tile!=null;tile=tile.next)
			{	
				TileType type=TileType.valueOf(tile.getString("type"));
				float eSpawnMod = tile.getFloat("eSpawnMod");
				float aSpawnMod = tile.getFloat("aSpawnMod");
				float fConsumMod = tile.getFloat("fConsumMod");
				float exploreMod = tile.getFloat("exploreMod");
				int moveCost = tile.getInt("moveCost");
				int fov = tile.getInt("fieldOfViem");
				int defBonus = tile.getInt("defBonus");
				int atkBonus = tile.getInt("atkBonus");
				int baseMats = tile.getInt("baseMats");
				int maxMats = tile.getInt("maxMats");
				int matRecRate = tile.getInt("matRecRate");
				
				Base_Tiles b=new Base_Tiles(type,eSpawnMod,aSpawnMod,fConsumMod,exploreMod,moveCost,fov,
						defBonus,atkBonus,baseMats,maxMats,matRecRate);
				
				BaseTiles.put(b.getType(), b);
			}
		}*/
	}	
}
