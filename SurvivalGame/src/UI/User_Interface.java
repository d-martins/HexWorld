package UI;


import utils.TextureManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.ename.diogo.martins.survival.AppController;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public abstract class User_Interface {
	static Skin skin;
	AppController appCtrl;
	
	
	public User_Interface(AppController appController){
		appCtrl=appController;
	}
	
	static{
		skin=new Skin(Gdx.files.internal("data/UI/uiSkin.json"),TextureManager.getAtlas("uiSkin"));		
		generateFonts();
		replaceFonts();
	}
	
	private static void replaceFonts(){
		for(Entry<String, TextButtonStyle> entry : skin.getAll(TextButtonStyle.class).entries()){
			if(entry.value.font==skin.getFont("short-font")){
				Gdx.app.log("USer_INTERFACE","short");
				entry.value.font=skin.getFont("fnt12");
			}
			else if(entry.value.font==skin.getFont("short-small")){
				Gdx.app.log("USer_INTERFACE","small");
				entry.value.font=skin.getFont("fnt9");
			}
			
		}
		
		for(Entry<String, LabelStyle> entry : skin.getAll(LabelStyle.class).entries()){
			if(entry.value.font==skin.getFont("short-font")){
				Gdx.app.log("USer_INTERFACE","short");
				entry.value.font=skin.getFont("fnt12");
			}
			else if(entry.value.font==skin.getFont("short-small")){
				Gdx.app.log("USer_INTERFACE","small");
				entry.value.font=skin.getFont("fnt9");
			}
		}
		
		for(Entry<String, TextFieldStyle> entry : skin.getAll(TextFieldStyle.class).entries()){
			if(entry.value.font==skin.getFont("short-font")){
				Gdx.app.log("USer_INTERFACE","short");
				entry.value.font=skin.getFont("fnt12");
			}
			else if(entry.value.font==skin.getFont("short-small")){
				Gdx.app.log("USer_INTERFACE","small");
				entry.value.font=skin.getFont("fnt9");
			}
		}
		
		for(Entry<String, WindowStyle> entry : skin.getAll(WindowStyle.class).entries()){
			if(entry.value.titleFont==skin.getFont("short-font")){
				Gdx.app.log("USer_INTERFACE","short");
				entry.value.titleFont=skin.getFont("fnt12");
			}
			else if(entry.value.titleFont==skin.getFont("short-small")){
				Gdx.app.log("USer_INTERFACE","small");
				entry.value.titleFont=skin.getFont("fnt9");
			}
		}
	}
	
	private static void generateFonts(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/UI/airstrip.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();		
		parameter.genMipMaps=true;
		parameter.minFilter=TextureFilter.MipMapLinearLinear;
		parameter.magFilter=TextureFilter.MipMapLinearLinear;
		parameter.characters=FreeTypeFontGenerator.DEFAULT_CHARS;
		
		parameter.size=10;
		BitmapFont font9=generator.generateFont(parameter);
		
		parameter.size=12;
		BitmapFont font12=generator.generateFont(parameter);
		
		parameter.size=16;
		BitmapFont font16=generator.generateFont(parameter);
		
		generator.dispose();
		skin.add("fnt9", font9);
		skin.add("fnt12", font12);
		skin.add("fnt16", font16);
	}
}






