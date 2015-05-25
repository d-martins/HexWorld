package com.ename.diogo.martins.survival.Items;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Characters.Character;

public class CampItem extends Item implements Json.Serializable{

	public CampItem(String itemId,String itemName, int itemCreationCost,
			String itemDescription, int ttl) {
		super(itemId,itemName, itemCreationCost, itemDescription, ttl);
		// TODO Auto-generated constructor stub
	}
	
	protected CampItem(){
		
	}
	
	@Override
	public void UseItem(Character c) {
		c.getTile().setItem(this);
	}

	@Override
	public Item clone() {
		CampItem c=new CampItem(this.getID(),this.getName(), this.getCreationCost(), this.getDescription(), this.getTurnsToLive());
		for(Effects e : this.getEffects()){
			c.addEffect(e);
		}
		return c;
	}

	@Override
	public void write(Json json) {
		json.writeValue("id",id);
		json.writeValue("turnsToLive",turnsToLive);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		CampItem i=(CampItem) items.get(jsonData.getString("id")).clone();		
		this.id=i.id;
		this.name=i.name;
		this.creationCost=i.creationCost;
		this.description=i.description;	
		this.effects=i.effects;
		this.turnsToLive=jsonData.getInt("turnsToLive");
		
	}

}
