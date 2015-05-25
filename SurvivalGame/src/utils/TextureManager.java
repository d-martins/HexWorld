package utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class TextureManager {

	public static Map<String,TextureAtlas> textureAtlases=new HashMap<String,TextureAtlas>();
	
	public static TextureAtlas getAtlas(String name){
		return textureAtlases.get(name);
	}
	
	public static void LoadTextures(){
		textureAtlases.put("tiles", new TextureAtlas(Gdx.files.internal("data/atlas/tiles.atlas")));
		textureAtlases.put("skinTest", new TextureAtlas(Gdx.files.internal("data/UI/skinTest.atlas")));
		textureAtlases.put("uiSkin", new TextureAtlas(Gdx.files.internal("data/UI/uiSkin.atlas")));
		textureAtlases.put("characters", new TextureAtlas(Gdx.files.internal("data/atlas/characters.atlas")));
		textureAtlases.put("sides", new TextureAtlas(Gdx.files.internal("data/atlas/sides.atlas")));
	}
}
