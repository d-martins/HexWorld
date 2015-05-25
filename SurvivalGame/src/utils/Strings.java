package utils;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Strings {
	private static String TAG = "STRINGS";
	private static Map<String,String>strings=new TreeMap<String,String>();
	
	/*-------------------------------------------------------*/
	/*						 Getters						 */
	/*-------------------------------------------------------*/
	public static String getString(String key){
		if(strings.containsKey(key))
			return strings.get(key);
		else
			return key;
	}

	/*-------------------------------------------------------*/
	/*						 Methods						 */
	/*-------------------------------------------------------*/
	public static void LoadStrings(String language){
		Gdx.app.log(TAG, language);
		JsonReader jsonReader= new JsonReader();
		JsonValue map = jsonReader.parse(Gdx.files.internal("data/strings/strings.json").reader("UTF8"));
		if(map.has(language))
			map=jsonReader.parse(Gdx.files.internal("data/strings/"+map.getString(language)).reader("UTF8"));
		else
			map=jsonReader.parse(Gdx.files.internal("data/strings/"+map.getString("default")).reader("UTF8"));
		
		for(JsonValue str = map.child; str!=null; str=str.next){
			strings.put(str.name, str.asString());
		}
		
	}

}
