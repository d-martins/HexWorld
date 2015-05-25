package UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;

public class StatusBars extends UIComponent {

	ProgressBar healthBar;
	TextButton healthValue;
	ProgressBar energyBar;
	TextButton energyValue;
	ProgressBar dataBar;
	TextButton dataValue;
	
	public StatusBars(Skin s) {
		super(s);
		buildStatusBar();
	}

	private void buildStatusBar(){
		root.defaults().padRight(3).padLeft(3).top().left();
		
		Stack health=new Stack();
		healthBar =	new ProgressBar(0, 10, 1, false, skin,"health");
		healthBar.setValue(0);
		healthValue = getBarText();
		health.add(healthBar);
		health.add(healthValue);
				
		Stack energy=new Stack();
		energyBar =	new ProgressBar(0, 10, 1, false, skin,"energy");
		energyBar.setValue(0);
		energyValue=getBarText();
		energy.add(energyBar);
		energy.add(energyValue);
		
		Stack data=new Stack();
		dataBar =	new ProgressBar(0, 10, 1, false, skin,"data");
		dataBar.setValue(0);
		dataValue=getBarText();
		data.add(dataBar);
		data.add(dataValue);
		
		root.add(health).width(72);
		root.add(energy).width(72);
		root.add(data).width(72);
	}
	
	@Override
	public void updateComponent(Game_State gameState) {
		Character player=gameState.getPlayer();
		healthBar.setRange(0,player.getBaseHealth());
		healthBar.setValue(player.getHealthPoints());
		healthValue.setText(getBarValue((int)healthBar.getValue(),(int)healthBar.getMaxValue()));
		
		energyBar.setRange(0, player.getBaseEnergy());
		energyBar.setValue(player.getEnergyPoints());		
		energyValue.setText(getBarValue((int)energyBar.getValue(),(int)energyBar.getMaxValue()));
		
		dataBar.setRange(0, player.getCapacity());
		dataBar.setValue(player.getCurrentOil());		
		dataValue.setText(getBarValue((int)dataBar.getValue(),(int)dataBar.getMaxValue()));

	}
	
	private String getBarValue(int minValue, int maxValue){
		String min, max;
		
		if(minValue<10) min="0"+minValue;
		else min=""+minValue;
		
		if(maxValue<10) max="0"+maxValue;
		else max=""+maxValue;
		
		return min+"/"+max;
	}
	
	private TextButton getBarText(){
		TextButton t=new TextButton("88/88",skin,"empty");
		t.getLabel().setColor(Color.BLACK);		
		t.getLabel().setAlignment(Align.right);
		t.pad(2, 0, 0, 5).padRight(10);
		return t;
	}

}
