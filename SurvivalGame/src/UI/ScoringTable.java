package UI;

import utils.Strings;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;

public class ScoringTable extends UIComponent {

	Label healthPts;
	Label dataPts;
	Label turnPts;
	Label extraPoints;
	Label totalPoints;
	
	public ScoringTable(Skin s) {
		super(s);
		buildTable();
	}
	
	private void buildTable(){
//		root.setFillParent(true);
		
		Label health=new Label(Strings.getString("health"),skin, "short");
		Image img_health=new Image(skin,"health-ico");		
		Button h=new Button(skin, "empty");
		h.add(img_health); h.add(health);
		
		Label data=new Label(Strings.getString("data"),skin, "short");
		Image img_data=new Image(skin,"data-ico");		
		Button d=new Button(skin, "empty");
		d.add(img_data); d.add(data); 
		
		Label turns=new Label(Strings.getString("turns"),skin, "short");
		Image img_turns=new Image(skin,"dur-ico");		
		Button t=new Button(skin, "empty");
		t.add(img_turns); t.add(turns); 
		
		totalPoints=new Label(Strings.getString("0000"),skin, "short");
		extraPoints=new Label(Strings.getString("0000"),skin, "short");
		
		healthPts=new Label("",skin,"short");
		dataPts=new Label("",skin,"short");
		turnPts=new Label("",skin,"short");
		
		root.defaults().expandX();
		root.add(h).left().padRight(25);
		root.add(healthPts).right();
		root.row();
		root.add(d).left();
		root.add(dataPts).right();
		root.row();
		root.add(t).left();
		root.add(turnPts).right();
		root.row();
		root.add(new Label(Strings.getString("turn_pts"),skin,"short")).left().padTop(5);
		root.add(extraPoints).right();
		root.row();
		root.add(new Label(Strings.getString("total_pts"),skin,"short")).left();
		root.add(totalPoints).right();
		
	}

	@Override
	public void updateComponent(Game_State gameState) {
		// TODO Auto-generated method stub
		Character player= gameState.getPlayer();
		healthPts.setText("+" +  player.getHealthPoints() + "x 100 pts.");
		dataPts.setText("+" +  player.getCurrentOil() + "x 150 pts.");
		turnPts.setText("-" +  gameState.getTurns() + "x 10 pts.");
		int turnPts = gameState.addExtraPoints();
		extraPoints.setText("+"+padNumber(turnPts,6));
		totalPoints.setText(padNumber(gameState.getPoints(),6));
	}

	
	private String padNumber(int num, int pad){
		int length = String.valueOf(num).length();
		int toAdd=pad-length;
		if(toAdd<=0)
			return ""+num;
		
		String zeroes="";
		for(int i=0;i<toAdd;i++){
			zeroes+="0";
		}
		
		return zeroes+num;
	}
}
