package com.ename.diogo.martins.survival;

import java.util.Locale;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String language =Locale.getDefault().getLanguage();
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration(); 
        cfg.useAccelerometer=false;
        cfg.useImmersiveMode=false;
        cfg.useCompass=false;
        initialize(new AppController(), cfg); 
        Game_State.setLanguage(language);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	String language =Locale.getDefault().getLanguage();
    	Game_State.setLanguage(language);
    }
}