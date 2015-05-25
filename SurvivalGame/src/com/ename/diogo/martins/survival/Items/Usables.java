package com.ename.diogo.martins.survival.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ename.diogo.martins.survival.Characters.Character;

public class Usables extends Item implements Json.Serializable{
	
	int quantity;
	
	public Usables(String itemId ,String itemName, int itemCreationCost,
			String itemDescription, int ttl) {
		super(itemId,itemName, itemCreationCost, itemDescription, ttl);
		quantity=1;
	}
	protected Usables(){
		
	}
	
	@Override
	public void UseItem(Character c) {
		for(Effects e : this.getEffects()){
			if(e == Effects.USE_ADD_CAPACITY || e == Effects.USE_ADD_CAPACITY_2)
				c.addCapacity(e.Value());
			else if( e== Effects.USE_ADD_OIL || e== Effects.USE_ADD_OIL_2)
				c.addOil(e.Value());
			else if( e== Effects.USE_RECOVER_ENERGY)
				c.addEnergy(e.Value());
			else if( e== Effects.USE_RECOVER_LIFE)
				c.addHealth(e.Value());
			else if(e==Effects.USE_PLACE_TRAP){
				c.getTile().setItem(this);
			}
		}
		Usables u= (Usables)c.getItems().get(this.getID());
		Gdx.app.log("USABLE", "Quantity: " + this.quantity);
		if(u.quantity>1){
			Gdx.app.log("USABLE", "removing Quantity");
			u.removeQuantity();
		}
		else
			c.getItems().remove(this.getID());
	}

	@Override
	public Item clone() {
		Usables u=new Usables(this.getID(),this.getName(), this.getCreationCost(), this.getDescription(), this.getTurnsToLive());
		for(Effects e : this.getEffects()){
			u.addEffect(e);
		}
		return u;
	}
	
	public int getQuantity(){
		return quantity;
	}
	
	public void addQuantity(){
		quantity++;
	}
	
	public void removeQuantity(){
		quantity--;
	}

	@Override
	public void write(Json json) {
		json.writeValue("id",id);
		json.writeValue("quantity",quantity);
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
		this.quantity=jsonData.getInt("quantity");
		this.turnsToLive=jsonData.getInt("turnsToLive");
	}

}
