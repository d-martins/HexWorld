package com.ename.diogo.martins.survival.Items;

import java.util.List;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Characters.Character;

public class Armor extends Item implements Json.Serializable {
	private int baseArmor;
	private int currentArmor;
	
	public Armor(String itemId,String itemName, int itemCreationCost, String itemDescription, int ttl ,int arm) {
		super(itemId, itemName, itemCreationCost, itemDescription, ttl);
		
		this.baseArmor=arm;
		this.currentArmor=baseArmor;
	}
	
	protected Armor(){
		
	}

	public int getBaseArmor(){return baseArmor;}
	public int getArmorValue(){return currentArmor;}
	
	public void damageaArmor(int i){currentArmor-=i;}
	
	@Override
	public void UseItem(Character c) {
		if(c.getArmor()!=this)
			c.setArmor(this);		

	}

	@Override
	public Item clone() {
		Armor a=new Armor(this.getID(),this.getName(), this.getCreationCost(), this.getDescription(), this.getTurnsToLive(),this.baseArmor);
		for(Effects e : this.getEffects()){
			a.addEffect(e);
		}
		return a;
	}
	
	/*-------------------------------------------------------*/
	/*						Interface						 */
	/*-------------------------------------------------------*/

	@Override
	public void write(Json json) {
		json.writeValue("id",id);
		json.writeValue("currentArmor",currentArmor);
		json.writeValue("turnsToLive",turnsToLive);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		Armor i=(Armor) items.get(jsonData.getString("id")).clone();		
		this.id=i.id;
		this.name=i.name;
		this.creationCost=i.creationCost;
		this.description=i.description;
		this.effects=i.effects;
		this.baseArmor=i.baseArmor;
		this.currentArmor=jsonData.getInt("currentArmor");
		this.turnsToLive=jsonData.getInt("turnsToLive");
	}

}
