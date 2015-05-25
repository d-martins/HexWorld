package com.ename.diogo.martins.survival.Screens;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import utils.ExtendedSprite;
import utils.ExtendedSpriteAccessor;
import utils.HexMath;
import utils.TextureManager;
import UI.Map_Interface;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.ename.diogo.martins.survival.AppController;
import com.ename.diogo.martins.survival.Game_State;
import com.ename.diogo.martins.survival.Actions.Action;
import com.ename.diogo.martins.survival.Characters.Character;
import com.ename.diogo.martins.survival.Characters.NPC;
import com.ename.diogo.martins.survival.Maps.Tile;
import utils.ScreenshotFactory;

public class Map_Screen implements Screen, GestureListener {
	
	static String TAG="MAP_SCREEN";
	
	private float resizeRatio;
	private Game_State gameState;
	private AppController appCtrl;
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Map <Tile,ExtendedSprite> tiles;
	private Map <Character, ExtendedSprite> characters;
	private ExtendedSprite selection;
	private TweenManager tweenManager;
	
	private Vector3 camTargetPosition;
	private float camTargetZoom;
	private float originalFingerDistance;
	private float camOriginalZoom;
	private float camMaxZoom;
	private float camMinZoom;
	private BoundingBox mapLeft;
	private BoundingBox mapTop;
	private BoundingBox mapRight;
	private BoundingBox mapBot;
	private float vMargin;
	private float hMargin;
	
	private Vector2 mapOrigin;
	
	private float tileSize;
	private final float ORIGINAL_TILESIZE=64; 
	
	private Stage stage;
	private Map_Interface ui;	
	private InputMultiplexer inputMultiplexer;
	private GestureDetector gestureDetector;
	
	private boolean invalid;
	/*-------------------------------------------------------*/
	/*						Constructor						 */
	/*-------------------------------------------------------*/
	public Map_Screen(AppController appController, InputMultiplexer multiplexer){
		inputMultiplexer=multiplexer;
		gestureDetector=new GestureDetector(this);
		appCtrl= appController;
		this.gameState=appCtrl.getGameState();
		//Draws the UI
		ui=new Map_Interface(appCtrl);
		stage=ui.getMapUserInterface();
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		invalid=false;
		camMinZoom=.65f;//0.3f;
		camMaxZoom=1.5f;
		camera.zoom=camMaxZoom;
		
		

		batch = new SpriteBatch();
		
		tiles=new TreeMap<Tile,ExtendedSprite>(new comparator());
		characters=new HashMap<Character, ExtendedSprite>();
		
		drawMap();
		drawCharacters();
		tweenManager=new TweenManager();
//		gameState.UpdateTileVisibility();
	}
	

	/*-------------------------------------------------------*/
	/*					  	 Interfaces					     */
	/*-------------------------------------------------------*/
	
	/*_______Screen______*/
	@Override
	public void render(float delta) {	
		updateCharacters();
		checkOutOfBounds();
		Gdx.gl.glClearColor(0,0,0,1);
//		if(invalid)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		Gdx.app.log(TAG, "Invalid: " + invalid );
		batch.setProjectionMatrix(camera.combined);
		Color tint= gameState.getDayNightCycle().getTint();
//		Vector3 v=DayNightCycle.getTint(gameState.getHour());
		batch.begin();
		batch.setColor(tint);
		
		for(Map.Entry<Tile,ExtendedSprite> entry :tiles.entrySet()){
			if(entry.getKey().isDiscovered())
				entry.getValue().draw(batch);
				if(entry.getKey().isSelected())
					selection.draw(batch);
		}
		for(Character c: gameState.getEnemies()){
			if(c.getTile().isVisible()){
				if(characters.containsKey(c))
					characters.get(c).draw(batch);
			}
		}
		characters.get(gameState.getPlayer()).draw(batch);
		batch.end();
		//shader.end();
		//debugRenders();
		
		stage.act();
		stage.draw();
		Table.drawDebug(stage);
		invalid=false;

		tweenManager.update(Gdx.graphics.getDeltaTime());
	}
	
	//Renders a bunch of lines and circles for debug purposes
	@SuppressWarnings("unused")
	private void debugRenders(){
		ShapeRenderer r =new ShapeRenderer();
		r.setProjectionMatrix(camera.combined);
		r.begin(ShapeType.Line);
		r.setColor(Color.RED);
		r.line(mapRight.max,mapRight.min);
		r.setColor(Color.BLUE);
		r.line(mapLeft.max,mapLeft.min);
		r.setColor(Color.CYAN);
		r.line(mapBot.max,mapBot.min);
		r.setColor(Color.GREEN);
		r.line(mapTop.max,mapTop.min);
		//r.line(Vector2.Zero, new Vector2(tileSize,0));
		r.circle(camera.position.x, camera.position.y, 10);
		r.circle(mapOrigin.x, mapOrigin.y, 10);
		for(Map.Entry<Tile,ExtendedSprite> sprite :tiles.entrySet()){				
			sprite.getValue().drawDebug(r);
			
		}
		r.setColor(0,1,1,1);
		//r.circle(0, 0, 10);
		r.end();
	}
	

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		//The target resolution height for which the graphics were developed for  
		final float TARGET_HEIGHT=768;//1280;
		//final float orTILE_SIZE=12;
		//The ratio between the real height and the target
		resizeRatio=height/TARGET_HEIGHT;
		//Resizes the tileSize to fit with the new height
		tileSize=ORIGINAL_TILESIZE*resizeRatio;

	//	Gdx.app.log(TAG, "tileSize: "+ tileSize);
		//Sets the new camera Viewport 
		if(camera==null)
			camera = new OrthographicCamera(width, height);
		else{
			camera.viewportWidth=width;
			camera.viewportHeight=height;
		}
	}
		
	@Override
	public void show() {
		Gdx.app.log(TAG, "Showing mapScreen!");
		//Sets the input processors
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(gestureDetector);
		//Turns off continuous rendering. It now renders when inputEvents occur
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();

	}

	@Override
	public void hide() {
		inputMultiplexer.removeProcessor(stage);
		inputMultiplexer.removeProcessor(gestureDetector);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		batch.dispose();		
		stage.dispose();
	}
	
	/*_______GestureListener______*/
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		Gdx.app.log(TAG, "HAndled");
		return false;
	}


	@Override
	public boolean tap(float x, float y, int count, int button) {;//Finds the position in screen coordinates of where the touch was released 
		Vector3 touch=new Vector3(x,y,0);		
		//Transforms the screen coordinates to world space coordinates 
		camera.unproject(touch);
		touch.x-=mapOrigin.x;
		touch.y-=mapOrigin.y;
		
		//Converts the pixel position of the touch to Axial Coordinates (hex coordinates)
		Vector2 coords = HexMath.PixelToAxialCoordinates(touch.x,touch.y,tileSize);
		//Selects the tile with the calculated coordinates
		
		gameState.SelectTile((int)coords.x,(int)coords.y);
		
		if(count==2){
			//Move a = new Move(player);
			//a.execute(gamestate);
			gameState.Move(false);
		}
		return false;
	}


	@Override
	public boolean longPress(float x, float y) {
		return false;
	}


	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}


	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {		
		//gets the vector of the finger movement on screen		
		//Gdx.app.log(TAG, "deltaX: " +deltaX + "deltaY: " + deltaY);
		Vector3 deltaXYZ=new Vector3(deltaX,deltaY,0);
		//inverts the x coordinate of the target, so the camera follows the finger
		deltaXYZ.x*=-1;
		//sets the target position of the camera as it's position vector + deltaXYZ 
		camTargetPosition=camera.position.cpy().add(deltaXYZ.cpy());
		dragCamera();
		return false;
	}


	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}


	@Override
	public boolean zoom(float initialDistance, float distance) {
		if(initialDistance!=originalFingerDistance){
			originalFingerDistance=initialDistance;
			camOriginalZoom=camera.zoom;
		}
		float ratio=initialDistance/distance;
		camTargetZoom=camOriginalZoom*ratio;
		zoomCamera();
		
		return false;
	}


	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		return false;
	}	
	

	/*-------------------------------------------------------*/
	/*						 Methods						 */
	/*-------------------------------------------------------*/	
	private void drawMap()
	{	
		//loads the map information from the GameState
		//Game_Map m=this.gameState.getMap();
		Sprite tmp=new Sprite(TextureManager.getAtlas("tiles").findRegion("selected_hex"));
		tmp.setScale(resizeRatio);
//		tmp.setOrigin(0, 0);
		selection= new ExtendedSprite(tmp);
		
		for(Map.Entry<Vector2, Tile> entry: gameState.getMap().getTiles().entrySet())//for each tile in the map
		{			
			Tile t=entry.getValue();
			AtlasRegion texture;
			Sprite sprite;			
			//gets the region from the texture atlas corresponding to this tile's type
			texture=TextureManager.getAtlas("tiles").findRegion(t.getCode());
			//Gdx.app.log(TAG, t.getCode());
			//creates the sprite for the tile
			sprite = new Sprite(texture);
			
			sprite.setScale(resizeRatio);
			sprite.setOrigin(0, 0);			
			//calculates the pixel coordinates for the tile
			Vector2 pxCoordinates=HexMath.AxialToPixelCoordinates((int)t.getPosition().x,(int)t.getPosition().y,tileSize);	
			
			//places the sprite at the retrieved position
			sprite.setPosition(pxCoordinates.x, pxCoordinates.y);
			ExtendedSprite eSprite=new ExtendedSprite(sprite);			
			tiles.put(t,eSprite);
			updateTile(t);
		}	
		mapOrigin=new Vector2(tileSize,(float) (Math.sqrt(3)/2*tileSize));
		setUpCamera();
	}
	
	//Sets the camera boundaries as well as its position and maximum zoom
	private void setUpCamera(){

		final float MARGIN=50;
		
		float camX,camY;
		float top, left, right,bot;
		int columns=gameState.getMap().getColumns();
		int rows=gameState.getMap().getRows();
		float vDistance=(float) (Math.sqrt(3)/2*tileSize*2);
		float hDistance=(float) (tileSize*2*(3f/4F));

		vMargin=MARGIN+vDistance/2;
		hMargin=MARGIN+hDistance;
		//calculates the bounds of the map
		top=rows*vDistance+vDistance/2 +vMargin;
		right=columns*hDistance+tileSize*.5f +hMargin;
		left=0-hMargin;
		bot=0-vMargin;		
		
		Vector2 pPos= gameState.getPlayer().getTile().getPosition();//getMap().getStartingTile().getPosition();
		pPos = HexMath.AxialToPixelCoordinates((int)pPos.x,(int) pPos.y, tileSize);
		Vector3 v3Pos=new Vector3(pPos.x, pPos.y,0);

		camX=v3Pos.x;
		camY=v3Pos.y;

		camera.position.set(camX, camY, 0);
		camTargetPosition=camera.position;	
		
		//sets bounding boxes at the edges of the map bounds
		mapLeft=new BoundingBox(new Vector3(left,bot,0),new Vector3(left,top,0));
		mapBot=new BoundingBox(new Vector3(left,bot,0),new Vector3(right,bot,0));
		mapRight=new BoundingBox(new Vector3(right,bot,0),new Vector3(right,top,0));
		mapTop=new BoundingBox(new Vector3(left,top,0),new Vector3(right,top,0));
		
		//calculates the maximum zoom you can have without having two bounds in the screen at the same time
		float maxZoomX=((right)-(left))/camera.viewportWidth; Gdx.app.log(TAG, "mZX: "+maxZoomX);
		float maxZoomY=((top)-(bot))/camera.viewportHeight;Gdx.app.log(TAG, "mZY: "+maxZoomY);
		//check smallest of the zoom components and sets it as the maximum amount you can zoom out
		if(maxZoomX<maxZoomY){
			if(maxZoomX<camMaxZoom)
				camMaxZoom=maxZoomX;
		}
		else{
			if(maxZoomY<camMaxZoom)
				camMaxZoom=maxZoomY;
		}
		
		Gdx.app.log(TAG, "camMaxZoom: "+camMaxZoom);
		//if the original zoom of the camera is too big for the map size, updates it 
//		if(camTargetZoom>camMaxZoom)
//			camTargetZoom=camMaxZoom;
		
		//camOriginalZoom=camTargetZoom;
		camTargetZoom=1f;
		zoomCamera();
	}
	
	private void drawCharacters(){
		Character player = gameState.getPlayer();
		
		Sprite tmp= new Sprite(TextureManager.getAtlas("characters").findRegion(gameState.getPlayer().getCode()));
		tmp.setScale(resizeRatio);		
		ExtendedSprite e= new ExtendedSprite(tmp);
		ExtendedSprite tile= tiles.get(player.getTile());
		e.centerOnPosition(tile.getPositionOfCenterV2());
		characters.put(player, e);

		for(NPC n : gameState.getEnemies()){
			tmp= new Sprite(TextureManager.getAtlas("characters").findRegion(n.getCode()));
			tmp.setScale(resizeRatio);
			e= new ExtendedSprite(tmp);
			tile= tiles.get(n.getTile());
			e.centerOnPosition(tile.getPositionOfCenterV2());
			characters.put(n, e);
		}
	}
	
	private void dragCamera(){
		if(camTargetPosition!=null){
			camera.position.set(camTargetPosition);
			camera.update();
			invalidate();
		}
	}
	
	private void zoomCamera(){	
		//Checks if the target zoom of the camera is within the limits
		if(camTargetZoom>camMaxZoom)
			camTargetZoom=camMaxZoom;
		else if(camTargetZoom<camMinZoom)
			camTargetZoom=camMinZoom;
		//sets the zoom of the camera to be the same as the target
		camera.zoom=camTargetZoom;
		camera.update();
		invalidate();
	}
	
	private void checkOutOfBounds(){
		//Check if any of he bounding boxes around the map are in view.
		//if they are, the camera repositions itself accordingly
		if(camera.frustum.boundsInFrustum(mapLeft))
			camera.position.x=mapLeft.getMax().x+camera.viewportWidth/2*camera.zoom;
		else if(camera.frustum.boundsInFrustum(mapRight))
			camera.position.x=mapRight.getMin().x-camera.viewportWidth/2*camera.zoom;
		if(camera.frustum.boundsInFrustum(mapBot))
			camera.position.y=mapBot.getMax().y+camera.viewportHeight/2*camera.zoom;
		else if(camera.frustum.boundsInFrustum(mapTop))
			camera.position.y=mapTop.getMin().y-camera.viewportHeight/2*camera.zoom;
		camera.update();
	}
	
	public void updateTile(Tile t){
		tiles.get(t).reset();
		if(t.isSelected()){
			selection.centerOnPosition(tiles.get(t).getPositionOfCenterV2());
		}	
		if(!t.isDiscovered()){
			tiles.get(t).setColor(1,1,1,.1f);
		}
		else if(!t.isVisible()){			
			tiles.get(t).setColor(1,1,1,.4f);
		}
	}
	
	private void updateCharacters(){
		Sprite tmp;
		ExtendedSprite e;
		ExtendedSprite tile;
		ExtendedSprite player= characters.get(gameState.getPlayer());
		
		for(NPC n : gameState.getEnemies()){
			if(!characters.containsKey(n)){
				Gdx.app.log(TAG, "adding a new Sprite");
				tmp= new Sprite(TextureManager.getAtlas("characters").findRegion(n.getCode()));
				tmp.setScale(resizeRatio);
				e= new ExtendedSprite(tmp);
				tile= tiles.get(n.getTile());
				e.centerOnPosition(tile.getPositionOfCenterV2());
				characters.put(n, e);
			}	
			else 
				e=characters.get(n);
			if(n.isResting())
				e.setColor(0.6f,0.6f,0.6f,-1);
			else
				e.setColor(1,1,1,-1);
		}
		if(gameState.getPlayer().isResting())
			player.setColor(0.6f,0.6f,0.6f,-1);
		else
			player.setColor(1,1,1,-1);
	}
	
	public void centerOnPlayer(){
		Vector2 pPos= characters.get(gameState.getPlayer()).getPositionOfCenterV2();
		camTargetPosition=new Vector3(pPos.x,pPos.y,camera.position.z);
		dragCamera();
	}
	
	public void moveCharacter(Action a){
		Gdx.app.log(TAG, "Setting Up animation! target: " + a.getActor());
		UpdateUI();
		ExtendedSprite e = characters.get(a.getActor());
		Vector2 targetPos=tiles.get(a.getEndTile()).getPositionOfCenterV2();
		
		if(a.getEndTile()!=a.getPreviousTile() && (a.getEndTile().isVisible() || a.getPreviousTile().isVisible())){	
			Tween.to(e, ExtendedSpriteAccessor.POSITION_XY_CENTER, 1)
			.target(targetPos.x,targetPos.y)
			.start(tweenManager)
			.setCallback(new TweenCallback(){
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(type == TweenCallback.COMPLETE){
						Gdx.app.log("CHARACTER_TWEEN", "Tween Complete!");
						Gdx.graphics.setContinuousRendering(false);
						gameState.resumeUpdate();
					}
				}
			});
			Gdx.graphics.setContinuousRendering(true);
		}
		else{
			Gdx.app.log("TAG", "Skipping Animation...");
			e.centerOnPosition(targetPos);
			gameState.resumeUpdate();
		}
			
	}
	
	public void UpdateUI(){
		ui.UpdateMapUI();
	}
	
	private void invalidate(){
		invalid=true;
		selection.invalidate();
		for(Entry<Tile, ExtendedSprite> entry : tiles.entrySet()){
			entry.getValue().invalidate();
		}
		for(Entry<Character, ExtendedSprite> entry : characters.entrySet()){
			entry.getValue().invalidate();
		}
		
	}	
	
	public void endLevel(){
		ExtendedSprite e=characters.get(gameState.getPlayer());
		Tween.to(e, ExtendedSpriteAccessor.ROTATION_ALPHA, 3)
		.target(1800,0)
		.start(tweenManager)
		.setCallback(new TweenCallback(){
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(type == TweenCallback.COMPLETE){
						Gdx.app.log("CHARACTER_TWEEN", "Tween Complete!");
						Gdx.graphics.setContinuousRendering(false);
						ui.showNextLevelDialog();
						
						Gdx.graphics.requestRendering();
					}
				}
			});
//		Tween.to(e, ExtendedSpriteAccessor.ALPHA, 3)
//		.target(0)
//		.start(tweenManager);
		Gdx.graphics.setContinuousRendering(true);
	}
	
	public void loseGame(){
		ExtendedSprite e=characters.get(gameState.getPlayer());
		Tween.to(e, ExtendedSpriteAccessor.ALPHA, 3)
		.target(0)
		.start(tweenManager)
		.setCallback(new TweenCallback(){
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if(type == TweenCallback.COMPLETE){
						Gdx.app.log("CHARACTER_TWEEN", "Tween Complete!");
						Gdx.graphics.setContinuousRendering(false);
						ui.showGameOverDialog();
						
						Gdx.graphics.requestRendering();
					}
				}
			});
		Gdx.graphics.setContinuousRendering(true);
	}
}


class comparator implements Comparator<Tile>{

	@Override
	public int compare(Tile a, Tile b) {
		Vector2 arg0=a.getPosition();
		Vector2 arg1=b.getPosition();
		//as colunas de baixo são superiores que às de cima
		if(arg0.y<arg1.y){
			return 1;
		}//as colunas de cima são inferiores às de baixo
		else if(arg0.y>arg1.y){
			return -1;
		}
		else{//as colunas da esquerda são superiores às da direita
			if(arg0.x>arg1.x)
				return 1;
			else if(arg0.x<arg1.x)//as colunas da direita são inferiores às da esquerda
				return -1;
		}
		return 0;
		
	}
	
}
