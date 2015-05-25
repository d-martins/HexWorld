package com.ename.diogo.martins.survival;

import utils.CameraAccessor;
import utils.ExtendedSprite;
import utils.ExtendedSpriteAccessor;
import utils.ScreenshotFactory;
import utils.Strings;
import utils.TextureManager;
import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Json;
import com.ename.diogo.martins.survival.Battle.BattleController;
import com.ename.diogo.martins.survival.Battle.BattleScreen;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Items.Item;
import com.ename.diogo.martins.survival.Screens.CharacterCreation_Screen;
import com.ename.diogo.martins.survival.Screens.Help_Screen;
import com.ename.diogo.martins.survival.Screens.Item_Screen;
import com.ename.diogo.martins.survival.Screens.MainMenu_Screen;
import com.ename.diogo.martins.survival.Screens.Map_Screen;

public class AppController extends Game {
	private static String TAG="APP_MANAGER";
	
	private Map_Screen mapScreen;
	private Item_Screen itemScreen;
	private BattleScreen battleScreen;
	private MainMenu_Screen mainMenuScreen;
	private Help_Screen helpScreen;
	private Screen previousScreen;
	
	private CharacterCreation_Screen characterCreationScreen;
	
	private InputMultiplexer multiplexer;
	
	private Game_State gameState;
	
	
	/*-------------------------------------------------------*/
	/*						"Constructor"					 */
	/*-------------------------------------------------------*/
	@Override
	public void create() {		
		Tween.registerAccessor(OrthographicCamera.class,new CameraAccessor());
		Tween.registerAccessor(ExtendedSprite.class,new ExtendedSpriteAccessor());
		Gdx.input.setCatchBackKey(true);
		Item.LoadItems();
		Character.loadPlayerCharacters();
//		gameState=new Game_State(this);
		multiplexer=new InputMultiplexer();
		
		//Base_Tiles.LoadTileInfo();
//		Base_Map.LoadBaseMaps();
		
		TextureManager.LoadTextures();	
		mainMenuScreen = new MainMenu_Screen(this,multiplexer);
		helpScreen=new Help_Screen(this, multiplexer);
//		mapScreen=new Map_Screen(this,multiplexer);
//		gameState.UpdateTileVisibility();
//		itemScreen=new Item_Screen(this,multiplexer);
		setScreen(mainMenuScreen);
		Gdx.app.log(TAG, Strings.getString("mainMenu_loaded"));
		Gdx.input.setInputProcessor(multiplexer);
//		mapScreen.UpdateUI();
		
//		Tile t=new Tile("plain", "plain-001", 12, 54);
//		Character c= new Character(TAG, TAG, 0, 0, 0, t);
//		Item.CreateItem(Item.items.get("cmp_001"), c);
//		Item.CreateItem(Item.items.get("cmp_002"), c);
//		Json json= new Json();
//
//		t.Update();t.Update();
//		String s= json.prettyPrint(t);
//		Gdx.app.log(TAG, s);
//		
//		Tile tile=json.fromJson(Tile.class, s);
//		Gdx.app.log(TAG, ""+tile.getItems().get("cmp_002").getTurnsToLive());
	}
	
	/*-------------------------------------------------------*/
	/*						 Getters						 */
	/*-------------------------------------------------------*/
	public Game_State getGameState(){return this.gameState;}
	public Map_Screen getMapScreen(){return this.mapScreen;}
	/*-------------------------------------------------------*/
	/*						 Setters						 */
	/*-------------------------------------------------------*/
	public void openItemScreen(){previousScreen=getScreen(); setScreen(itemScreen);}
	private void openCharacterCreationScreen(){previousScreen=getScreen(); setScreen(characterCreationScreen);}
	public void openMapScreen(){previousScreen=getScreen(); setScreen(mapScreen);}
	public void openMainMenuScreen(){previousScreen=getScreen(); setScreen(mainMenuScreen);}
	public void openHelpScreen(){previousScreen=getScreen(); setScreen(helpScreen);}
	public void closeScreen(){Screen temp = getScreen(); setScreen(previousScreen); previousScreen=temp;}
	/*-------------------------------------------------------*/
	/*					  	 Interface					     */
	/*-------------------------------------------------------*/
	@Override
	public void dispose() {
		if(battleScreen!=null)
			battleScreen.dispose();
		if(itemScreen!=null)
			itemScreen.dispose();
		if(mainMenuScreen!=null)
			mainMenuScreen.dispose();
		if(mapScreen!=null)
			mapScreen.dispose();		
		if(characterCreationScreen!=null)
			characterCreationScreen.dispose();
	}

	@Override
	public void render() {	
		super.render();
		if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
			ScreenshotFactory.saveScreenshot();
			Gdx.app.log(TAG,"ScreenShot");
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	/*-------------------------------------------------------*/
	/*					  		Methods						 */
	/*-------------------------------------------------------*/
	
	public void createCharacter(){
		if(characterCreationScreen!=null)
			characterCreationScreen.dispose();
		characterCreationScreen= new CharacterCreation_Screen(this, multiplexer);
		openCharacterCreationScreen();
	}
	
	public void startNewGame(Character c){
		gameState=new Game_State(this,c);
		mapScreen=new Map_Screen(this,multiplexer);
		gameState.UpdateTileVisibility();
		itemScreen=new Item_Screen(this,multiplexer);
		openMapScreen();
		mapScreen.UpdateUI();
	}
	
	public void loadGame(){
		gameState=new Json().fromJson(Game_State.class, Gdx.files.local("saveGame"));
		gameState.setAppController(this);
		mapScreen=new Map_Screen(this,multiplexer);
		gameState.UpdateTileVisibility();
		itemScreen=new Item_Screen(this,multiplexer);
		openMapScreen();
		mapScreen.UpdateUI();
	}
	
	public void resetMapScreen(){
		if(gameState == null)
			return;
		disposeMapScreen();
		disposeItemScreen();
		
		mapScreen=new Map_Screen(this,multiplexer);
		itemScreen=new Item_Screen(this,multiplexer);
		gameState.UpdateTileVisibility();
		mapScreen.UpdateUI();
		openMapScreen();	
	}
	
	public void startBattle(BattleController b){
		battleScreen= new BattleScreen(b, multiplexer);
		setScreen(battleScreen);
	}
	
	public void endBattle(){
		disposeBattleScreen();
		setScreen(mapScreen);
	}
	
	private void gameOver(){
		//TODO:GAME OVER;
		Gdx.app.log(TAG, "GAME OVER!");
	}
	
	public void endGame(){
		//TODO: CHECK what needs to be done when ending game.
		setScreen(mainMenuScreen);
		disposeMapScreen();
		disposeItemScreen();
		
	}
	
	private void disposeMapScreen(){
		if(mapScreen!= null){
			mapScreen.dispose();
			mapScreen=null;
		}
	}
	
	private void disposeItemScreen(){
		if(itemScreen!=null){
			itemScreen.dispose(); 
			itemScreen=null;
		}
	}
	
	private void disposeBattleScreen(){
		if(battleScreen!=null){
			battleScreen.dispose(); 
			battleScreen=null;
		}
	}
}
