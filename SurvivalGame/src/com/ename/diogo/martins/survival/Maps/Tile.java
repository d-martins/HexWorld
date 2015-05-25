package com.ename.diogo.martins.survival.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Characters.Character;

import utils.HexMath;
import utils.Strings;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Tile implements Json.Serializable{
	private Base_Tiles baseTile;
	private String code;
	private Vector2 position;
	private Map<String,Item> items;
	private int materialAmount;
	private int storedMaterialQtt;
	private boolean isSelected;
	private boolean isObjective;
	private boolean isStart;
	private boolean isVisible;
	private boolean isDiscovered;
	private ArrayList<Character>characters;
	
	/*-------------------------------------------------------*/
	/*						Constructor						 */
	/*-------------------------------------------------------*/
	
	public Tile(String type,String name,int q,int r){
		baseTile=Base_Tiles.baseTiles.get(type);
		code=name;
		position=new Vector2(q,r);
		materialAmount=baseTile.getBaseMaterials();
		isVisible=false;
		isDiscovered=false;
		items=new HashMap<String,Item>();
		characters= new ArrayList<Character>();
	}
	
	private Tile(){
		items=new HashMap<String,Item>();
	}
	/*-------------------------------------------------------*/
	/*						Getters							 */
	/*-------------------------------------------------------*/	
	public Base_Tiles getBaseTile(){return this.baseTile;}
	public String getCode(){return this.code;}
	public String getName(){return Strings.getString(code);}
	public Vector2 getPosition(){return position.cpy();}
	public int getMaterialQtt(){return this.materialAmount;}
	public int getSotoredQtt(){return this.storedMaterialQtt;}
	public boolean isSelected(){return this.isSelected;}
	public boolean isObjective(){return this.isObjective;}
	public boolean isStart(){return this.isStart;}
	public boolean isVisible(){return this.isVisible;}
	public boolean isDiscovered(){return this.isDiscovered;}
	public Map<String,Item> getItems(){return this.items;}
	public ArrayList<Character> getCharacters(){return this.characters;}
	public int getAttackBonus(){
		int i=baseTile.getAtkBonus(); 
		for(Entry<String, Item> entry: items.entrySet()){
			for(Effects e : entry.getValue().getEffects()){
				if(e==Effects.BATTLE_ATK_BONUS)
					i+=e.Value();
			}
		}
		return i;
	}
	public int getDefenseBonus(){
		int i=baseTile.getDefBonus(); 
		for(Entry<String, Item> entry: items.entrySet()){
			for(Effects e : entry.getValue().getEffects()){
				if(e==Effects.BATTLE_DEF_BONUS)
					i+=e.Value();
			}
		}
		return i;
	}
	
	/*-------------------------------------------------------*/
	/*						Setters							 */
	/*-------------------------------------------------------*/
	public void setSelected(boolean b){isSelected=b;}
	public void setVisibility(boolean b){isVisible=b; if(isVisible)isDiscovered=true;}
	public void setItem(Item i){items.put(i.getID(),i);}
	public void setObjecctive(boolean b){isObjective = b;}
	
	/*-------------------------------------------------------*/
	/*						Methods							 */
	/*-------------------------------------------------------*/
	
	public void Update(){
		RecoverMaterials();
		UpdateItems();
	}
	private void RecoverMaterials()
	{
		int recRate=baseTile.getMaterialRecoveryRate();
		int maxMats=baseTile.getMaxMaterials();
		
		if(materialAmount<maxMats)
			materialAmount+=recRate;
		if(materialAmount>maxMats)
			materialAmount = maxMats;
	}
	
	public void TakeMaterials(int amount){
		materialAmount-=amount;
		if(materialAmount<0)
			materialAmount=0;
	}
	
	private void UpdateItems()
	{
		List<String> toRemove=new ArrayList<String>();
//		if(items.size()>0){
//			Gdx.app.log("TILE", "items: "+ items.size());
			for (Map.Entry<String, Item> entry : items.entrySet()){
				Item i=entry.getValue();
				Gdx.app.log("TAG","item updated");
				if(i.getTurnsToLive()!=-2){
					i.tickItem();
					if(i.getTurnsToLive()<0)
						toRemove.add(entry.getKey());
				}
				Gdx.app.log("TILES", "ttl" + i.getTurnsToLive());
			}
		
			/*for(Item i : this.items){
				
			}*/
//			Gdx.app.log("TAG", "ToRemove: "+ toRemove.size());
			for(String s : toRemove){
				Gdx.app.log("TILES", "Removed: "+ s);
				this.items.remove(s);
//			}
		}
	}
	
	public boolean CheckIfNeighbours(Tile t){
		int tileQ=(int) t.getPosition().x;
		int tileR=(int) t.getPosition().y;
		for(HexMath.HexDirections h : HexMath.HexDirections.values()){
			int neighbourQ=(int) (position.x+h.direction.x);
			int neighbourR=(int) (position.y+h.direction.y);
			
			if(tileQ==neighbourQ && tileR==neighbourR)
				return true;
		}		
		return false;
	}
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("baseTile", baseTile.getType());
		json.writeValue("code", code);
		json.writeValue("q", position.x);
		json.writeValue("r", position.y);
		json.writeValue("items",items);
		json.writeValue("materialAmount", materialAmount);
		json.writeValue("storedMaterialQtt", storedMaterialQtt);
		json.writeValue("isObjective", isObjective);
		json.writeValue("isStart", isStart);
		json.writeValue("isVisible", isVisible);
		json.writeValue("isDiscovered", isDiscovered);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		baseTile=Base_Tiles.baseTiles.get(jsonData.getString("baseTile"));
		code=""+jsonData.getString("code");
		position=new Vector2(jsonData.getFloat("q"),jsonData.getFloat("r"));
		
		for(JsonValue item=jsonData.getChild("items");item!=null;item=item.next){
			Item i =  json.fromJson(Item.class, item.toString());
			items.put(i.getID(), i);
		}
		
		materialAmount=jsonData.getInt("materialAmount");
		storedMaterialQtt=jsonData.getInt("storedMaterialQtt");
		isObjective=jsonData.getBoolean("isObjective");
		isStart=jsonData.getBoolean("isStart");
		isVisible=jsonData.getBoolean("isVisible");
		isDiscovered=jsonData.getBoolean("isDiscovered");
		
		isSelected=false;
		characters= new ArrayList<Character>();
	}
	
	
	
}
