package UI;

import java.util.ArrayList;
import java.util.Map;

import utils.Strings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Items.Armor;
import com.ename.diogo.martins.survival.Items.CampItem;
import com.ename.diogo.martins.survival.Items.Effects;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Items.Usables;
import com.ename.diogo.martins.survival.Items.Weapon;

public class ItemsList extends UIComponent {
	private static String TAG="ITEM_LIST";

	private ScrollPane scroll;	
	private Filter filter;
	private Filter prevFilter;
	private ItemActions actions;
	TextButton weaponFilter;
	TextButton armorFilter;
	TextButton usableFilter;
	ListItem selectedItem;
	
	ArrayList<ListItem> listItems;
	
	enum Filter{
		WEAPON(Weapon.class),
		ARMOR(Armor.class),
		USABLE(Usables.class),
		CAMP(CampItem.class);
		
		Class cls;
		Filter(Class c){
			this.cls=c;			
		}
		
	}
	
	public ItemsList(Skin s) {
		super(s);
		filter = Filter.WEAPON;
		prevFilter=null;
		selectedItem=null;
		listItems=new ArrayList<ListItem>();
		buildItemsList();
		buildComponent();
		setListeners();
	}
	
	private void buildComponent(){
		//root.setFillParent(true);
		
		scroll=new ScrollPane(getFilteredItemsList());
		scroll.setFlingTime(.5f);
		scroll.setOverscroll(false, false);
		Table t= new Table(skin);
		t.setBackground("pane");
		
		root.add(getFiltersTable()).expandX().left();
		root.row();		
		t.add(scroll).expandX().fillX().padTop(5);
		t.row();
		actions=new ItemActions(skin);
		t.add(actions.getTable()).expandX().fillX().height(150).pad(20,5,5,5);
		
		root.add(t).expand().fill();
	}
	
	private void setListeners(){
		weaponFilter.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				filter=Filter.WEAPON;
				armorFilter.setDisabled(false);
				usableFilter.setDisabled(false);
				weaponFilter.setDisabled(true);
				clearSelectedItem();
				
			}
		});
		armorFilter.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				filter=Filter.ARMOR;
				armorFilter.setDisabled(true);
				usableFilter.setDisabled(false);
				weaponFilter.setDisabled(false);
				clearSelectedItem();
			}
		});
		usableFilter.addListener(new ChangeListener() {			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				filter=Filter.USABLE;			
				armorFilter.setDisabled(false);
				usableFilter.setDisabled(true);
				weaponFilter.setDisabled(false);
				clearSelectedItem();
			}
		});
	}
	
	private void clearSelectedItem(){
		if(selectedItem!=null)
			selectedItem.setSelected(false);
		selectedItem=null;
		actions.updateItem(null);
	}
		
	private void buildItemsList(){
		for(Map.Entry<String, Item> entry : Item.items.entrySet()){
			ListItem li=new ListItem(skin, entry.getValue());
			listItems.add(li);
			li.setListener(new ChangeListener() {					
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if(selectedItem!=null)
						selectedItem.setSelected(false);
					selectedItem=(ListItem)actor.getUserObject();
					selectedItem.setSelected(true);
					actions.updateItem(selectedItem.getItem());
				}
			});
		}
	}

	@Override
	public void updateComponent(Game_State gameState) {
		actions.updateComponent(gameState);
		if(prevFilter!= filter){
			scroll.clearChildren();
			scroll.setWidget(getFilteredItemsList());
			prevFilter=filter;
		}
		for(ListItem li : listItems){
			if(filter.cls == li.getItem().getClass()){
				li.updateComponent(gameState);
			}
		}
		if(scroll.isFlinging())
			Gdx.graphics.requestRendering();
	}
	
	private Table getFiltersTable(){
		Table wrapper=new Table();
		wrapper.defaults().padRight(-3);
		
		weaponFilter=new TextButton(Strings.getString("weapon"), skin, "square");
		armorFilter= new TextButton(Strings.getString("armor"), skin, "square");
		usableFilter=new TextButton(Strings.getString("usables"), skin, "square"); 
		weaponFilter.setDisabled(true);
		wrapper.add(weaponFilter.pad(8,5,3,5));
		wrapper.add(armorFilter.pad(8,5,3,5));
		wrapper.add(usableFilter.pad(8,5,3,5));
		
		return wrapper;
	}
	
	private Table getFilteredItemsList(){
		Table list=new Table();
		for(ListItem li : listItems){
			if(filter.cls == li.getItem().getClass()){
				list.add(li.getTable()).left().expandX().fillX().pad(0,1,1,1);
				list.row();
			}
		}
		return list;
	}
	
	public Item getSelectedItem(){
		return selectedItem.getItem();
	}
	
	public void setCreateListener(EventListener e){
		actions.setCreateListener(e);
	}
	public void setUseListener(EventListener e){
		actions.setUseListener(e);
	}
	public void setDestroyListener(EventListener e){
		actions.setDestroyListener(e);
	}
}
