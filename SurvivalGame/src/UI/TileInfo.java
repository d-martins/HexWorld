package UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Maps.Tile;

public class TileInfo extends UIComponent {

	private Button infoBar;
	private TextButton tileName;
	private TextButton enemyName;
	Tile selectedTile;
	boolean selectEnemy;
	
	public TileInfo(Skin s) {
		super(s);
		buildTileInfo();
		setListeners();
		selectEnemy=true;
	}

	private void buildTileInfo(){
		root.setFillParent(true);
		//root.bottom().left();
		
		infoBar=new Button(skin,"border-top");
		infoBar.align(Align.left);
		infoBar.defaults().left().expandY();
//		
		tileName=new TextButton("afg",skin,"chipped-inv");
		tileName.pad(10,15,5,5);
		tileName.setDisabled(true);
//		
		enemyName= new TextButton("adf", skin, "chipped");
		enemyName.pad(10, 5, 5, 15);
		enemyName.setVisible(false);
		
		root.add(enemyName).height(26).left();
		root.add(tileName).height(26).right();//.expandX();
		root.row();
		root.add(infoBar).height(32).expandX().fillX().colspan(2);;
		//		
	}
	
	private void setListeners(){
		tileName.addListener(new ChangeListener(){			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				Gdx.app.log("TILE_INFO", "isChecked: " + tileName.isDisabled());
				if(!tileName.isDisabled()){
					activateTileName();
					selectEnemy=false;
				}
			}
		});
		
		enemyName.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("TILE_INFO", "enemy isDisabled: " + tileName.isDisabled());
				if(!enemyName.isDisabled()){
					activateEnemyName();
					selectEnemy=true;
				}
				
			}
		});
	}
	
	public void activateTileName(){
		if(!tileName.isDisabled()){
			tileName.setDisabled(true);		
			Gdx.app.log("Tile_INFO","TileName set Disbaled");
			enemyName.setDisabled(false);
		}			
	}
	public void activateEnemyName(){
		if(!enemyName.isDisabled()){
			tileName.setDisabled(false);
			Gdx.app.log("Tile_INFO","TileName set Enabled");
			enemyName.setDisabled(true);
		}
	}
		
	
	public void setEnemyNameListener(EventListener e){
		enemyName.addListener(e);
	}
	
	public void setTileNameListener(EventListener e){
		tileName.addListener(e);
	}
	
	@Override
	public void updateComponent(Game_State gameState) {
		Tile t=gameState.getSelectedTile();
		
		if(t==null)
			t=gameState.getPlayer().getTile();			
		if(t!=selectedTile)
			selectEnemy=true;
		
		if(t.isVisible() && t.getCharacters().size()>0){
			enemyName.setVisible(true);
			enemyName.setText(t.getCharacters().get(0).getName());
			Gdx.app.log("TILE_INFO", "selectEnemy: "+ selectEnemy);
			if(selectEnemy) activateEnemyName();
		}
		else{
			enemyName.setVisible(false);
			activateTileName();
			selectEnemy=true;
		}
		
		tileName.setText(t.getName());
		infoBar.clearChildren();
		populateInfo(t, gameState);
		selectedTile=t;
	}	
	
	public void populateInfo(Tile t,Game_State gameState){
		if(enemyName.isDisabled()){
			Character c= t.getCharacters().get(0); 
			
			Button health = new Button(skin,"empty");
			Image img_Health= new Image(skin,"health-ico");
			Label lbl_Health=new Label(""+c.getHealthPoints(), skin,"short");
			health.add(lbl_Health);
			health.add(img_Health);
			if(c.getHealthPoints()<3){
				lbl_Health.setColor(1,0,0,1); 
				img_Health.setColor(1,0,0,1);
			}
			
			Button energy = new Button(skin,"empty");
			Image img_Energy = new Image(skin,"energy-ico");
			Label lbl_Energy =new Label(""+c.getEnergyPoints(), skin,"short");
			energy.add(lbl_Energy);
			energy.add(img_Energy);
			if(c.getEnergyPoints()==0){
				lbl_Energy.setColor(Color.GRAY); 
				img_Energy.setColor(Color.GRAY);
			}
			else if(c.getEnergyPoints()<=c.getEnergyIntervals()[0]){
				lbl_Energy.setColor(1,0,0,1); 
				img_Energy.setColor(1,0,0,1);
			}
			else if(c.getEnergyPoints()<=c.getEnergyIntervals()[1]){
				lbl_Energy.setColor(1,1,0,1); 
				img_Energy.setColor(1,1,0,1);
			}
			
			Button data = new Button(skin,"empty");
			Image img_Data = new Image(skin,"data-ico");
			Label lbl_Data =new Label(""+c.getCurrentOil()+"/"+ c.getCapacity(),skin,"short");
			data.add(lbl_Data);
			data.add(img_Data);	
			if(c.getCurrentOil()>=c.getCapacity()-3){
				lbl_Energy.setColor(Color.GRAY); 
				img_Energy.setColor(Color.GRAY);
			}
			
			Button attacks = new Button(skin,"empty");
			Image img_Attacks=new Image(skin, "atk-ico");
			Label lbl_Attacks= new Label(""+c.getAttack(), skin,"short");
			attacks.add(lbl_Attacks);
			attacks.add(img_Attacks);
			
			Button damage = new Button(skin,"empty");
			Image img_Damage=new Image(skin, "dmg-ico");
			Label lbl_Damage= new Label(""+c.getDamage(), skin,"short");
			damage.add(lbl_Damage);
			damage.add(img_Damage);
			
			int arm=0;
			if(c.getArmor()!=null)
				arm=c.getArmor().getBaseArmor();
			Button armor = new Button(skin,"empty");
			Image img_Armor=new Image(skin,"def-ico");
			Label lbl_Armor= new Label(""+arm,skin, "short");
			armor.add(lbl_Armor);
			armor.add(img_Armor);
			
			infoBar.add(health);
			infoBar.add(energy);	
			infoBar.add(data);
			infoBar.add(attacks).right().expandX();
			infoBar.add(damage);
			infoBar.add(armor);
			
			//infoBar.debug();
		}else{
			String s;
			Gdx.app.log("Tile_INFO","TileName is Disbaled");
			Button cost= new Button(skin,"empty");
			Image img_Cost= new Image(skin,"energy-ico");
			Label lbl_Cost=new Label(""+t.getBaseTile().getMoveCost(), skin, "short");
			cost.add(lbl_Cost);
			cost.add(img_Cost);
			infoBar.add(cost);
			
			int eMod=t.getBaseTile().getExploreMod();
			s=""+eMod;
			if(eMod>0)
				s="+"+s;
			Button search=new Button(skin,"empty");
			Image img_Search=new Image(skin,"search-ico");
			Label lbl_Search= new Label(s,skin,"short");
			search.add(lbl_Search);
			search.add(img_Search);
			infoBar.add(search);
			if(eMod==0){
				img_Search.setColor(Color.GRAY);
				lbl_Search.setColor(Color.GRAY);
			}
			
			int fmod=(int)t.getBaseTile().getFovMod();
			s=""+fmod;
			if(fmod>0)
				s="+"+s;
			Button fov= new Button(skin, "empty");
			Image img_Fov= new Image(skin, "fov-ico");
			Label lbl_Fov= new Label(s, skin, "short");
			fov.add(lbl_Fov);
			fov.add(img_Fov);
			infoBar.add(fov);
			if(fmod==0){
				img_Fov.setColor(Color.GRAY);
				lbl_Fov.setColor(Color.GRAY);
			}
			
			int cmod=(int)t.getBaseTile().getFoodConsumMod();		
			s=""+cmod;
			if(cmod>0)
				s="+"+s;
			Button consum= new Button(skin, "empty");
			Image img_Consum=new Image(skin,"sleep-ico");
			Label lbl_Consum=new Label(""+cmod,skin,"short");
			consum.add(lbl_Consum);
			consum.add(img_Consum);
			t.getBaseTile().getFoodConsumMod();
			infoBar.add(consum);
			if(cmod==0){
				img_Consum.setColor(Color.GRAY);
				lbl_Consum.setColor(Color.GRAY);
			}
			
			int aBonus=t.getAttackBonus();			
			s=""+aBonus;
			if(aBonus>0)
				s="+"+s;
			Button attacks=new Button(skin,"empty");
			Image img_Attacks=new Image(skin, "atk-ico");
			Label lbl_Attacks=new Label(s,skin,"short");
			attacks.add(lbl_Attacks);
			attacks.add(img_Attacks);
			infoBar.add(attacks);
			if(aBonus==0){
				img_Attacks.setColor(Color.GRAY);
				lbl_Attacks.setColor(Color.GRAY);
			}
			
			int dBonus=t.getDefenseBonus();			
			s=""+dBonus;
			if(dBonus>0)
				s="+"+s;
			Button defense=new Button(skin,"empty");
			Image img_Defense=new Image(skin, "def-ico");
			Label lbl_Defense=new Label(s,skin,"short");
			defense.add(lbl_Defense);
			defense.add(img_Defense);
			infoBar.add(defense);
			if(dBonus==0){
				img_Defense.setColor(Color.GRAY);
				lbl_Defense.setColor(Color.GRAY);
			}
			
			int dQtt=t.getMaterialQtt();
			Button data=new Button(skin,"empty");
			Image img_Data=new Image(skin, "data-ico");
			data.add(img_Data);
			infoBar.add(data).right().expandX();
			if(dQtt<=0)
				img_Data.setColor(Color.GRAY);
			else if(dQtt<5)
				img_Data.setColor(Color.RED);
			else 
				img_Data.setColor(Color.GREEN);
			
			
		}
	}
	
	
	
	
	

}
