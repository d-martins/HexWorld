package UI;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Characters.Character;

public class CharacterButton extends UIComponent {
	
	Character player;
	boolean isSelected;
	Button wrapper;
	
	public CharacterButton(Skin s,Character c) {
		super(s);
		player=c;
		buildCharacterButton();
		isSelected=false;
	}

	
	private void buildCharacterButton(){
		
		Table portrait = new Table(skin);
		portrait.add(new Image(skin,player.getCode()));
		
		Table info = new Table(skin);
		
		Table health =  new Table();
		Label lbl_health= new Label(""+player.getBaseHealth(),skin, "short");
		Image img_health= new Image(skin, "health-ico");
		health.add(lbl_health);
		health.add(img_health);
		
		Table energy =  new Table();
		Label lbl_energy= new Label(""+player.getBaseEnergy(),skin, "short");
		Image img_energy= new Image(skin, "energy-ico");
		energy.add(lbl_energy);
		energy.add(img_energy);
		
		Table capacity=  new Table();
		Label lbl_capacity= new Label(""+player.getCapacity(),skin, "short");
		Image img_capacity= new Image(skin, "bpk-ico");
		capacity.add(lbl_capacity);
		capacity.add(img_capacity);
		
		Table search=  new Table();
		Label lbl_search= new Label("+"+player.getOilDiscoveryRate(),skin, "short");
		Image img_search= new Image(skin, "search-ico");
		search.add(lbl_search);
		search.add(img_search);
		
		Table fov=  new Table();
		Label lbl_fov= new Label(""+player.getFOV(),skin, "short");
		Image img_fov= new Image(skin, "fov-ico");
		fov.add(lbl_fov);
		fov.add(img_fov);
		
		Table atk=  new Table();
		Label lbl_atk= new Label(""+player.getAttack(),skin, "short");
		Image img_atk= new Image(skin, "atk-ico");
		atk.add(lbl_atk);
		atk.add(img_atk);
		
		info.add(health);
		info.add(capacity);
		info.add(fov);
		info.row();
		info.add(energy);
		info.add(search);
		info.add(atk);
		
		wrapper = new Button(skin,"pane");
		wrapper.add(portrait).uniformY().fillY();
		wrapper.add(info);
		
		root.add(wrapper);
	}
	
	public void setSelected(Boolean selected){isSelected=selected;}
	public Character getCharacter(){return player;}
	
	public void setListener(EventListener e){
		wrapper.setUserObject(this);
		wrapper.addListener(e);
	}
	
	@Override
	public void updateComponent(Game_State gameState) {
		// TODO Auto-generated method stub
		if(isSelected){
			wrapper.setDisabled(true);
			for(Actor a : root.getChildren()){
				a.setColor(0.8f,0.8f,0.8f,1);
			}
		}
		else{
			wrapper.setDisabled(false);
			for(Actor a : root.getChildren()){
				a.setColor(1f,1f,1f,1);
			}
		}
			
	}

}
