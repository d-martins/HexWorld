package com.ename.diogo.martins.survival.Items;

import utils.Strings;

public enum Effects {
	BATTLE_DEF_BONUS		("eDesc_BatDefBon"	,1),
	BATTLE_ATK_BONUS		("eDesc_BatAttBon"	,1),
	CAMP_PLACE_FIRE			("eDesc_CmpFire"	,1),
	CAMP_PLACE_CRATE		("eDesc_CmpCrate"	,1),
	CAMP_PLACE_TENT			("eDesc_CmpTent"	,1),
	CAMP_PLACE_BARRICADE	("eDesc_CmpBarr"	,1),
	CAMP_PLACE_WORKSTATION	("eDesc_CmpWork"	,1),
	EXPLORE_ENERGY_MOD		("eDesc_ExpEngMod"	,1),
	EXPLORE_BONUS_OIL		("eDesc_ExpBonMat"	,1),
	BATTLE_OIL_FOUND_MOD	("eDesc_BatFoodMod"	,1),
	MOVE_ENERGY_MOD			("eDesc_MovEngMod"	,2),
	MOVE_ENERGY_MOD_2		("eDesc_MovEngMod"	,4),
	REST_OIL_COST_MOD		("eDesc_RstFooCost",1),
	REST_EXTRA_ENERGY		("eDesc_RstExtEng",1),
	USE_RECOVER_LIFE		("eDesc_UseRecLife",1),
	USE_RECOVER_ENERGY		("eDesc_UseRecEng",1),
	USE_ADD_OIL			("eDesc_UseAddFood",1),
	USE_ADD_OIL_2			("eDesc_UseAddFood",1),
	USE_ADD_CAPACITY		("eDesc_UseAddCap",5),
	USE_ADD_CAPACITY_2		("eDesc_UseAddCap",10),
	USE_PLACE_TRAP			("eDesc_UsePlcTrap",1);	
	//TODO:Place Can just be the same effect...
	private String description;
	private int value;
	
	Effects(String d, int v){
		description=Strings.getString(d);
		value=v;
	}
	
	public String Description(){return Strings.getString(description);}
	public String Name(){return Strings.getString(this.toString());}
	public int Value(){return value;}
}
