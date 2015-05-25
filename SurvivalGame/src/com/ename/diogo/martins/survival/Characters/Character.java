package com.ename.diogo.martins.survival.Characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Weapon;
import com.ename.diogo.martins.survival.Maps.Tile;

public class Character implements Json.Serializable{
	public static ArrayList<Character>playerCharacters;
	
	@SuppressWarnings("unused")
	private static String TAG="CHARACTER";
	protected String name;
	protected String code;
	
	protected int strength;
	protected int endurance;
	protected int intelligence;
	
	protected int baseAttack;
	protected int baseCapacity;
	protected int baseHealth;
	protected int baseEnergy;
	protected int[] energyIntervals;
	protected int oilDiscoveryRate;
	protected int energyRecoveryRate;
	
	protected int baseFOV;
	protected int currentOil;
	protected int healthPoints;
	protected int energyPoints;
	protected int extraCapacity;
	
	protected Tile tile;
	protected boolean isResting;
	protected boolean isFrozen;
	protected Map<String,Item> items;
	protected Weapon  weapon;
	protected Armor armor;
	
	protected Tile lastPosition;
	protected Set<Tile> visible;
	/*--------------------------------------------------*/
	/*					Constructor						*/
	/*--------------------------------------------------*/
	public Character(String charName,String charCode,int str, int end, int intel, Tile pos){
		this.name=charName;
		this.code=charCode;
		this.tile=pos;
		if(tile!=null)
			tile.getCharacters().add(this);
		this.strength=str;
		this.endurance=end;
		this.intelligence=intel;		
		LoadAttributeValues();
		
		this.currentOil=0;
		this.healthPoints=this.baseHealth;
		this.energyPoints=this.baseEnergy;
		this.isResting=false;
		this.isFrozen=false;
		this.extraCapacity=0;
		this.items=new HashMap<String,Item>();		
	}
	
	public Character(Character c){
		this.name=c.name;
		this.code=c.code;
		this.tile=c.tile;
		if(tile!=null)
			tile.getCharacters().add(this);
		this.strength=c.strength;
		this.endurance=c.endurance;
		this.intelligence=c.intelligence;
		LoadAttributeValues();
		
		this.currentOil=0;
		this.healthPoints=this.baseHealth;
		this.energyPoints=this.baseEnergy;
		this.isResting=false;
		this.isFrozen=false;
		this.extraCapacity=0;
		this.items=new HashMap<String,Item>();			
	}
	
	public Character(){
		this.items=new HashMap<String,Item>();	
	}
	/*--------------------------------------------------*/
	/*						Getters						*/
	/*--------------------------------------------------*/
	
	public String getName(){return name;}
	public String getCode(){return code;}
	public int getStrength(){return strength;}
	public int getEndurance(){return endurance;}
	public int getIntelligence(){return intelligence;}
	public int getBaseAttack(){return baseAttack;}
	public int getAttack(){if(weapon==null)return baseAttack; else return baseAttack+weapon.getAttacks();}
	public int getDamage(){if(weapon==null)return 1; else return 1+weapon.getDamage();}
	public int getCapacity(){return baseCapacity+extraCapacity;}
//	public int getAvailableOil() {return currentOil+tile.getSotoredQtt();}
	public int getBaseHealth(){return baseHealth;}
	public int getBaseEnergy(){return baseEnergy;}	
	public int[] getEnergyIntervals(){return energyIntervals;}
	public int getFOV(){return this.baseFOV;}
	public int getOilDiscoveryRate(){return oilDiscoveryRate;}
	public int getCurrentOil(){return currentOil;}
	public int getHealthPoints(){return healthPoints;}
	public int getEnergyPoints(){return energyPoints;}
	public Tile getTile(){return tile;}
	public boolean isResting(){return isResting;}
	public boolean isFrozen(){return isFrozen;}
	public Map<String,Item> getItems(){return items;}
	public Weapon getWeapon(){return weapon;}
	public Armor getArmor(){return armor;}
	public Tile getPreviousPosition(){return this.lastPosition;}
	public Set<Tile> getVisibleTiles(){return this.visible;}
	
	/*--------------------------------------------------*/
	/*						Setters						*/
	/*--------------------------------------------------*/
	
	public void WakeUp(){isResting=false;}
	public void setWeapon(Weapon w){weapon=w;}
	public void setArmor(Armor a){armor=a;}
	public void addItem(Item i){items.put(i.getID(), i);}
	public void addCapacity(int i){extraCapacity+=i;}
	public void addOil(int i){currentOil+=i; if(currentOil>baseCapacity+extraCapacity)currentOil=baseCapacity+extraCapacity;}
	public void addEnergy(int i){this.energyPoints+=i; if(energyPoints>baseEnergy)energyPoints=baseEnergy;}
	public void addHealth(int i){this.healthPoints+=i; if(healthPoints>baseHealth)healthPoints=baseHealth;}
	public void setVisibleTiles(Set<Tile> t){this.visible= t;}
	public void setEnergy(int i){this.energyPoints=i;}
	public void setResting(boolean resting){isResting=resting;}
	public void setOil(int i){currentOil=i;}
	public void setHealth(int i){this.healthPoints=i;}
	public void destroyArmor(){items.remove(armor.getID()); armor=null;}
	public void setTile(Tile t){
		if(this.tile!=null) {
			tile.getCharacters().remove(this);
			this.lastPosition=this.tile;
		}
		this.tile=t; 
		tile.getCharacters().add(this);
	}
	public void setPrevTile(Tile t){this.lastPosition=t;}
	
	/*--------------------------------------------------*/
	/*						Methods						*/
	/*--------------------------------------------------*/
	
	public static void loadPlayerCharacters(){
		playerCharacters= new ArrayList<Character>();
		JsonReader jsonReader= new JsonReader();
		JsonValue map= jsonReader.parse(Gdx.files.internal("data/config/characters/playerCharacters.json"));
		for(JsonValue character = map.child(); character!=null; character=character.next){
			String code=character.name;
			String name=character.getString("name");
			int strength=character.getInt("str");
			int endurance=character.getInt("end");
			int inteligence=character.getInt("int");
			
			Character c= new Character(name, code, strength, endurance, inteligence, null);
			playerCharacters.add(c);
//			Gdx.app.log(TAG, ""+character);
		}
	}
	
	public void kill(){
		this.tile.getCharacters().remove(this);
		this.tile=null;
	}
	
	
		//Loads Attributes based on strength, endurance and intelligence values
	protected void LoadAttributeValues(){
		JsonReader jsonReader=new JsonReader();
		JsonValue root= jsonReader.parse(Gdx.files.internal("data/config/atributes.json"));
		
			//Gathers info from the strength attribute
		for(JsonValue str= root.getChild("str");str!=null;str=str.next){
			if(Integer.parseInt(str.name)==strength){
				this.baseAttack=str.getInt("atk");
				this.baseCapacity=str.getInt("cap");
			}
		}
			//Gathers info from the intelligence attribute
		for(JsonValue intel= root.getChild("int");intel!=null;intel=intel.next){
			if(Integer.parseInt(intel.name)==intelligence){
				this.baseFOV=intel.getInt("fov");
				this.oilDiscoveryRate=intel.getInt("oil");
			}
		}
			//Gathers info from the endurance attribute
		for(JsonValue end= root.getChild("end");end!=null;end=end.next){
			if(Integer.parseInt(end.name)==endurance){
				this.baseHealth=end.getInt("health");
				this.baseEnergy=end.getInt("energy");
				this.energyIntervals=new int[2];
				this.energyIntervals[0]=end.getInt("low");
				this.energyIntervals[1]=end.getInt("med");
			}
		}
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/
	@Override
	public void write(Json json) {
		json.writeValue("name", name);
		json.writeValue("code", code);
		
		json.writeValue("strength", strength);
		json.writeValue("endurance", endurance);
		json.writeValue("intelligence", intelligence);
		
		json.writeValue("currentOil", currentOil);
		json.writeValue("healthPoints", healthPoints);
		json.writeValue("energyPoints", energyPoints);
		json.writeValue("extraCapacity", extraCapacity);
		
		json.writeValue("position", tile.getPosition());
		json.writeValue("isResting", isResting);
		json.writeValue("isFrozen", isFrozen);
		json.writeValue("items", items);
		
		if(weapon!=null)
			json.writeValue("weapon",weapon.getID());
		if(armor!=null)
			json.writeValue("armor",armor.getID());
		if(lastPosition!=null)
			json.writeValue("lastPosition",lastPosition.getPosition());
		
		//TODO: do i need the visible tiles?
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		this.name=jsonData.getString("name");
		this.code=jsonData.getString("code");
		
		this.strength=jsonData.getInt("strength");
		this.endurance=jsonData.getInt("endurance");
		this.intelligence=jsonData.getInt("intelligence");
		
		Character c = new Character(name,code,strength,endurance,intelligence,null);
		
		this.baseAttack=c.baseAttack;
		this.baseCapacity=c.baseCapacity;
		this.baseHealth=c.baseHealth;
		this.baseEnergy=c.baseEnergy;
		this.energyIntervals=c.energyIntervals;
		this.oilDiscoveryRate=c.oilDiscoveryRate;
		this.energyRecoveryRate=c.energyRecoveryRate;		
		this.baseFOV=c.baseFOV;
		
		this.currentOil=jsonData.getInt("currentOil");
		this.healthPoints=jsonData.getInt("healthPoints");
		this.energyPoints=jsonData.getInt("energyPoints");
		this.extraCapacity=jsonData.getInt("extraCapacity");
		
		this.isResting=jsonData.getBoolean("isResting");
		this.isFrozen=jsonData.getBoolean("isFrozen");
		
		for(JsonValue item=jsonData.getChild("items");item!=null;item=item.next){
			Item i =  json.fromJson(Item.class, item.toString());
			this.items.put(i.getID(), i);
		}
		if(jsonData.has("weapon"))
			this.weapon = (Weapon) this.items.get(jsonData.getString("weapon"));
		if(jsonData.has("armor"))
			this.armor = (Armor) this.items.get(jsonData.getString("armor"));
		
//		protected Set<Tile> visible;
	}	
}
