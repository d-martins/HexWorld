package com.ename.diogo.martins.survival.Items.Effect;

import java.util.HashMap;
import java.util.Map;

public abstract class Effect{
	public static Map<String, Effect> effects = new HashMap<String,Effect>();	
	String code;
	
	public Effect(String effectCode){
		this.code=effectCode;
	}
	
	public abstract void apply(Object arg);
}
