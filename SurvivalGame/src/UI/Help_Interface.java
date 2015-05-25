package UI;

import utils.Strings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.ename.diogo.martins.survival.AppController;

public class Help_Interface extends User_Interface {

	Stage stage;
	
	public Help_Interface(AppController appController) {
		super(appController);
		// TODO Auto-generated constructor stub
	}
	
	public Stage getHelpInterface(){
		stage=new Stage(new ExtendViewport(320,480));
		stage.addActor(buildHelpInterface());
		return stage;
	}
	
	public Table buildHelpInterface(){
		Table root=new Table(skin);		
		root.setFillParent(true);
		
		Table contents= new Table();		
		contents.add(buildObjectives()).expandX().fillX().left().top();
		contents.row();
		contents.add(buildIcons()).expand().fillX().left().top();
		
		skin.get("chipped", TextButtonStyle.class).font=skin.getFont("fnt12");
		TextButton back=new TextButton(Strings.getString("help_back"), skin, "chipped-enabled");
		back.getLabelCell().padTop(-10);
		back.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				appCtrl.closeScreen();
			}			
		});
		
		ScrollPane scroll= new ScrollPane(contents);
		root.add(scroll).left().fill().expand();
		root.row();
		root.add(back).left().expandX().bottom().padTop(10);
		return root;
	}
	
	public Table buildObjectives(){
		Table root=new Table();
		skin.get("short", LabelStyle.class).font=skin.getFont("fnt16");
		
		Label objectives=new Label(Strings.getString("help_objectives"), skin,"short");
		objectives.getStyle().font=skin.getFont("fnt12");
		
		Table character= new Table();
		Image charIcon= new Image(skin,"player-001");
		Label charLbl= new Label(Strings.getString("help_001"), skin,"short");
		charLbl.setWrap(true);
		character.add(charIcon).left().pad(10,10,5,5);
		character.add(charLbl).left().top().expand().fill();	
		
		Table data = new Table();
		Image dataIcon= new Image(skin,"data-ico");
		Label dataLbl= new Label(Strings.getString("help_002"), skin,"short");
		dataLbl.setWrap(true);
		data.add(dataLbl).left().top().expand().fill().padRight(10).padLeft(5);	
		data.add(dataIcon).left().padRight(5).padLeft(10);		
		
		Table endTile=new Table();
		Image tileIcon = new Image(skin,"end-001");
		Label tileLbl = new Label(Strings.getString("help_003"), skin,"short");
		tileLbl.setWrap(true);		
		endTile.add(tileIcon).left().pad(10, 5, 5, 10);
		endTile.add(tileLbl).left().expand().fill();
		
		root.add(objectives).left();
		root.row();
		root.defaults().left().expandX().fillX().padTop(5).padBottom(10);
		root.add(character);
		root.row();
		root.add(data);	
		root.row();
		root.add(endTile);
//		root.debug();
		return root.top();
	}
	
	public Table buildActions(){
		Table root=new Table();
		return root;
	}
	
	public Table buildIcons(){
		Table root=new Table();
		
		skin.get("short", LabelStyle.class).font=skin.getFont("fnt16");		
		Label icons=new Label(Strings.getString("help_icons"), skin,"short");
		icons.getStyle().font=skin.getFont("fnt12");		
		
		Table health= new Table();
		Image healthIcon= new Image(skin,"health-ico");
		Label healthLbl= new Label(Strings.getString("help_health"), skin,"short");
		healthLbl.setWrap(true);
		health.add(healthIcon).left().pad(0, 5, 0, 10);;
		health.add(healthLbl).left().expand().fill();;
		
		Table energy= new Table();
		Image energyIcon= new Image(skin,"energy-ico");
		Label energyLbl= new Label(Strings.getString("help_energy"), skin,"short");
		energyLbl.setWrap(true);
		energy.add(energyIcon).left().pad(0, 5, 0, 10);
		energy.add(energyLbl).left().expand().fill();
		
		Table data= new Table();
		Image dataIcon= new Image(skin,"data-ico");
		Label dataLbl= new Label(Strings.getString("help_data"), skin,"short");
		dataLbl.setWrap(true);
		data.add(dataIcon).left().pad(0, 5, 0, 10);
		data.add(dataLbl).left().expand().fill();
		
		Table fov= new Table();
		Image fovIcon= new Image(skin,"fov-ico");
		Label fovLbl= new Label(Strings.getString("help_fov"), skin,"short");
		fovLbl.setWrap(true);
		fov.add(fovIcon).left().pad(0, 5, 0, 10);
		fov.add(fovLbl).left().expand().fill();
		
		Table search= new Table();
		Image searchIcon= new Image(skin,"search-ico");
		Label searchLbl= new Label(Strings.getString("help_search"), skin,"short");
		searchLbl.setWrap(true);
		search.add(searchIcon).left().pad(0, 5, 0, 10);
		search.add(searchLbl).left().expand().fill();
		
		Table rest= new Table();
		Image restIcon= new Image(skin,"sleep-ico");
		Label restLbl= new Label(Strings.getString("help_rest"), skin,"short");
		restLbl.setWrap(true);
		rest.add(restIcon).left().pad(0, 5, 0, 10);
		rest.add(restLbl).left().expand().fill();
		
		Table def= new Table();
		Image defIcon= new Image(skin,"def-ico");
		Label defLbl= new Label(Strings.getString("help_def"), skin,"short");
		defLbl.setWrap(true);
		def.add(defIcon).left().pad(0, 5, 0, 10);
		def.add(defLbl).left().expand().fill();
		
		Table atk= new Table();
		Image atkIcon= new Image(skin,"atk-ico");
		Label atkLbl= new Label(Strings.getString("help_atk"), skin,"short");
		atkLbl.setWrap(true);
		atk.add(atkIcon).left().pad(0, 5, 0, 10);
		atk.add(atkLbl).left().expand().fill();
		
		root.add(icons).left().padBottom(20);
		root.row();
		root.defaults().left().expandX().fillX().padBottom(10);
		root.add(health);
		root.row();
		root.add(energy);
		root.row();
		root.add(data);
		root.row();
		root.add(fov);
		root.row();
		root.add(search);
		root.row();
		root.add(rest);
		root.row();
		root.add(def);
		root.row();
		root.add(atk);
		
		return root.top();
	}
}
