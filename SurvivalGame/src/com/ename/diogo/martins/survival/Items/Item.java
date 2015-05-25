package com.ename.diogo.martins.survival.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import utils.Strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Characters.Character;

public abstract class Item {
	@SuppressWarnings("unused")
	static private String TAG="ITEM";
	public static Map<String,Item> items =new TreeMap<String,Item>();
	
	protected String id;
	protected String name;
	protected int creationCost;
	protected String description;
	protected int turnsToLive;
	protected List<Effects> effects;
	
	public Item(String itemId, String itemName, int itemCreationCost, String itemDescription, int ttl){
		this.id=itemId;
		this.name=itemName;
		this.creationCost=itemCreationCost;
		this.description=itemDescription;
		this.effects=new ArrayList<Effects>();
		this.turnsToLive=ttl;
	}
	protected Item(){
		
	}
	
	public String getID(){return this.id;}
	public String getName(){return this.name;}
	public int getCreationCost(){return this.creationCost;}
	public String getDescription(){return this.description;}
	public int getTurnsToLive(){return this.turnsToLive;}
	public List<Effects> getEffects(){return effects;}
	
	public void setTurnsToLive(int i){turnsToLive=i;}
	public void tickItem(){this.turnsToLive--;}
	public void addEffect(Effects e){effects.add(e);}
	
	public static void LoadItems(){
		JsonReader jsonReader=new JsonReader();
		JsonValue root= jsonReader.parse(Gdx.files.internal("data/config/items.json"));
		
		for(JsonValue wpn=root.getChild("weapons");wpn!=null;wpn=wpn.next){
			addWeapon(wpn);
		}		
		for(JsonValue arm=root.getChild("armor");arm!=null;arm=arm.next){
			addArmor(arm);
		}		
		for(JsonValue use=root.getChild("usables");use!=null;use=use.next){
			addUsablesAndCampItems(use,true);
		}
		for(JsonValue cmp=root.getChild("camp");cmp!=null;cmp=cmp.next){
			addUsablesAndCampItems(cmp,false);
		}
	}
	
	private static void addWeapon(JsonValue j){
		String iID=j.getString("name");
		String iName="name_"+iID;
		int iCost=j.getInt("cost");
		String iDesc="desc_"+iID;
		
		int att = j.getInt("attack");
		int dmg = j.getInt("damage");
		float dur = j.getFloat("duration");
		int ttl = -2;
		
		if(j.has("ttl"))
			ttl=j.getInt("ttl");
		
		iName=Strings.getString(iName);
		iDesc=Strings.getString(iDesc);
		
		Item i=new Weapon(iID, iName, iCost, iDesc, ttl, att, dmg, dur);
		items.put(i.id,i);	
		addEffects(i,j);
	}
	
	private static void addArmor(JsonValue j){
		String iID=j.getString("name");
		String iName="name_"+iID;
		int iCost=j.getInt("cost");
		String iDesc="desc_"+iID;
		
		int armor = j.getInt("armor");
		int ttl = -2;
		
		if(j.has("ttl"))
			ttl=j.getInt("ttl");
		iName=Strings.getString(iName);
		iDesc=Strings.getString(iDesc);
		
		Item i= new Armor(iID, iName, iCost, iDesc , ttl , armor);
		items.put(i.id,i);	
		addEffects(i,j);
	}
	
	private static void addUsablesAndCampItems(JsonValue j, boolean usable){
		String iID=j.getString("name");
		String iName="name_"+iID;
		int iCost=j.getInt("cost");
		String iDesc="desc_"+iID;
		
		int ttl = -2;
		iName=Strings.getString(iName);
		iDesc=Strings.getString(iDesc);
		if(j.has("ttl")){
			ttl=j.getInt("ttl");}
		Item i;
		if(usable)
			i= new Usables(iID,iName, iCost, iDesc,ttl);
		else
			i= new CampItem(iID,iName, iCost, iDesc,ttl);
		
		items.put(i.id,i);	
		addEffects(i,j);
	}
	
	private static void addEffects(Item i, JsonValue j){
		if(j.has("effects")){
			for(JsonValue e=j.getChild("effects");e!=null;e=e.next){
				i.addEffect(Effects.valueOf(e.getString("name")));
			}
		}
	}
	
	public static void CreateItem(Item i, Character c){
		Item t= items.get(i.id).clone();
		if(t instanceof CampItem)
			t.UseItem(c);
		else if(t instanceof Usables && c.getItems().containsKey(i.getID())){
			((Usables)c.getItems().get(i.getID())).addQuantity();			

			Gdx.app.log(TAG, "Adding Quantity"+((Usables) c.getItems().get(i.getID())).quantity);
		}
		else
			c.addItem(t);
	}
	
	public static void DestroyItem(){
		
	}
	
	public abstract void UseItem(Character c);
	
	public abstract Item clone();
}
