package com.ename.diogo.martins.survival.Items;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Characters.Character;

public class Weapon extends Item implements Json.Serializable{

	private int attacks;
	private int damage;
	private float duration;
	
	public Weapon(String itemid,String itemName, int itemCreationCost, String itemDescription,int ttl, int att, int dmg, float dur) {
		super(itemid, itemName, itemCreationCost, itemDescription, ttl);

		this.attacks=att;
		this.damage=dmg;
		this.duration=dur;
	}
	
	protected Weapon(){
		
	}

	public int getAttacks(){ return attacks;}
	public int getDamage(){return damage;}
	public float getDuration(){return duration;}

	@Override
	public void UseItem(Character c) {
		if(c.getWeapon()!=this)
			c.setWeapon(this);		
	}

	@Override
	public Item clone() {
		Weapon w=new Weapon(this.getID(),this.getName(), this.getCreationCost(), this.getDescription(), this.getTurnsToLive(),this.attacks, this.damage, this.duration);
		for(Effects e : this.getEffects()){
			w.addEffect(e);
		}
		return w;
	}

	@Override
	public void write(Json json) {
		json.writeValue("id",id);	
		json.writeValue("turnsToLive",turnsToLive);	
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		Weapon i=(Weapon) items.get(jsonData.getString("id")).clone();		
		this.id=i.id;
		this.name=i.name;
		this.creationCost=i.creationCost;
		this.description=i.description;	
		this.effects=i.effects;
		this.attacks=i.attacks;
		this.damage=i.damage;
		this.duration=i.duration;
		this.turnsToLive=jsonData.getInt("turnsToLive");
	}
	
	
}
