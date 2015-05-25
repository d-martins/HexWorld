package com.ename.diogo.martins.survival.Battle;

import java.util.HashMap;

import sun.swing.BakedArrayList;
import utils.CameraAccessor;
import utils.ExtendedSprite;
import utils.ExtendedSpriteAccessor;
import utils.TextureManager;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.ename.diogo.martins.survival.Characters.Character;

public class BattleScreen implements Screen, GestureListener {
	private static String TAG= "BATTLE_SCREEN";
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private TweenManager tweenManager;
	
	private BattleController battleController;
	private InputMultiplexer inputMultiplexer;
	private GestureDetector gestureDetector;
	
	private ExtendedSprite battle;
	private ExtendedSprite backGround;
	
	private ExtendedSprite enemy;
	private ExtendedSprite player;
	private ExtendedSprite dButton;
	private ExtendedSprite bar;
	
	private ExtendedSprite pHealthBar;
	private ExtendedSprite pArmorBar;
	private ExtendedSprite pHealthBar_bg;
	
	private ExtendedSprite eArmorBar;
	private ExtendedSprite eHealthBar;
	private ExtendedSprite eHealthBar_bg;
	
	private ExtendedSprite shield;
	
	private float resizeRatio;
	private Vector3 camTarget;
	
	private HashMap<Target,ExtendedSprite> targets;
	private HashMap<Target, Tween> tweens;
	
	
	public BattleScreen(BattleController battle,InputMultiplexer multiplexer) {
		// TODO Auto-generated constructor stub
		battleController=battle;
		inputMultiplexer=multiplexer;
		gestureDetector=new GestureDetector(this);
		tweenManager = new TweenManager();
		camTarget=null;
		targets=new HashMap<Target,ExtendedSprite>();
		tweens=new HashMap<Target, Tween>();
		
		resize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		drawStage();
		batch = new SpriteBatch();
	}
	/*-------------------------------------------------------*/
	/*					 	 Screen						     */
	/*-------------------------------------------------------*/
	@Override
	public void render(float delta) {
		
		battleController.update();
		
		// TODO Auto-generated method stub
//		Gdx.gl.glClearColor(0,0.5f,1,1);
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		
		batch.begin();
		
		backGround.draw(batch);
		battle.draw(batch);
		enemy.draw(batch);
		player.draw(batch);
		shield.draw(batch);
		bar.draw(batch);
		pHealthBar_bg.draw(batch);
		pHealthBar.draw(batch);
		pArmorBar.draw(batch);
		eHealthBar_bg.draw(batch);
		eHealthBar.draw(batch);
		eArmorBar.draw(batch);
		for(Target t : battleController.targets){
			if(t.state==Target.State.PLAYING && !targets.containsKey(t)){
				drawTarget(t);
				targets.get(t).draw(batch);
			}
			else if (t.state==Target.State.PLAYING)
				targets.get(t).draw(batch);
		}
		if(!Gdx.input.isTouched())
			dButton.setColor(Color.WHITE);
		dButton.draw(batch);
		batch.end();

		update();
		tweenManager.update(Gdx.graphics.getDeltaTime());		
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log(TAG, "resizing");
		//The target resolution height for which the graphics were developed for  
		final float TARGET_WIDTH=520;//768;//1280;
		//final float orTILE_SIZE=12;
		//The ratio between the real height and the target
		resizeRatio=width/TARGET_WIDTH;
		//Resizes the tileSize to fit with the new height

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
		// TODO Auto-generated method stub
		inputMultiplexer.addProcessor(gestureDetector);
		Gdx.graphics.setContinuousRendering(true);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		inputMultiplexer.removeProcessor(gestureDetector);
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	
	/*-------------------------------------------------------*/
	/*						GestureListener					 */
	/*-------------------------------------------------------*/

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		if(battleController.state==BattleController.State.ATTACKING){
			Vector3 touch = new Vector3(x,y,0);
			camera.unproject(touch);
			for(Target t : battleController.targets){
				if(t.state==Target.State.PLAYING){
					Vector3 tPos = targets.get(t).getPositionOfCenter();
					if(touch.dst(tPos)< targets.get(t).getVirtualWidth()/2){
						battleController.popTarget(t);
						tweens.get(t).kill();
						Tween.to(enemy, ExtendedSpriteAccessor.COLOR, .1f)
						.target(1,0,0)
						.repeatYoyo(1, 0)									
						.ease(Linear.INOUT)
						.start(tweenManager)
						.setCallback(new TweenCallback(){
							@Override
							public void onEvent(int type, BaseTween<?> source) {
								if(type == TweenCallback.COMPLETE)
									enemy.setColor(Color.WHITE);								
							}							
						});
						
						break;
					}
				}
			}
		}		
		else if(battleController.state==BattleController.State.DEFENDING){
			Vector3 touch = new Vector3(x,y,0);
			boolean hit=false;
			camera.unproject(touch);			
			if(touch.dst(dButton.getPositionOfCenter())<=dButton.getVirtualWidth()/2){			
				dButton.setColor(new Color(1f,0.4f,0.4f,1));
				Gdx.app.log(TAG, "TOUCHING");
				for(Target t : battleController.targets){
					if(t.state==Target.State.PLAYING){
						Vector3 tPos= targets.get(t).getPositionOfCenter();
						if(dButton.boundingBox().contains(tPos.x, tPos.y)){
							dButton.setColor(new Color(0.4f,0.4f,1,1));
							//Gdx.app.log(TAG, "log");
							hit=true;
							Tween.to(shield, ExtendedSpriteAccessor.ALPHA, .1f)
									.target(1)
									.repeatYoyo(1, 0)									
									.ease(Linear.INOUT)
									.start(tweenManager);
							//battleController.popTarget(t);
							t.pop();
							tweens.get(t).kill();
							break;
						}
					}
				}
				if(!hit){
					for(Target t : battleController.targets){
						if(t.state==Target.State.PLAYING){
							battleController.popTarget(t);
							tweens.get(t).kill();
						}
					}
				}
					
			}
		}
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}	

	private void drawStage(){
		drawCharacters();
		drawHealthBars();
		Sprite temp;
		
		TextureRegion t_Bar = TextureManager.getAtlas("sides").findRegion("bar");
		TextureRegion t_Btn = TextureManager.getAtlas("sides").findRegion("button");
		TextureRegion t_Battle = TextureManager.getAtlas("sides").findRegion("battle");
		TextureRegion t_bg = TextureManager.getAtlas("sides").findRegion("plain-001");
		
		temp=new Sprite(t_Btn);
		temp.setScale(resizeRatio);
		dButton= new ExtendedSprite(temp);
		float x = player.getPositionOfCenter().x;
		float y = -camera.viewportHeight/2 + 2*dButton.getVirtualHeight()/3;
		dButton.centerOnPosition(x, y);
		
		temp=new Sprite(t_Bar);
		temp.setScale(resizeRatio);
		bar= new ExtendedSprite(temp);
		bar.centerOnPosition(x, y);
		
		temp=new Sprite(t_bg);
		temp.setScale(resizeRatio);
		backGround=new ExtendedSprite(temp);
		backGround.centerOnPosition(camera.position.x,camera.position.y-camera.viewportHeight/2.5f);
		
		temp=new Sprite(t_Battle);
		temp.setScale(resizeRatio);
		battle=new ExtendedSprite(temp);
		battle.centerOnPosition(camera.position.x,camera.position.y);
		battle.setColor(1, 1, 1, 0);
		Tween.to(battle, ExtendedSpriteAccessor.ALPHA, .4f)
		.target(1)
		.ease(Quad.IN)
		.setCallback(new TweenCallback() {			
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if(type == TweenCallback.COMPLETE)
					Tween.to(battle, ExtendedSpriteAccessor.ALPHA, .9f)
					.target(0)					
					.delay(1)
					.start(tweenManager);
				
			}
		})
		.start(tweenManager);
	}
	
	private void drawCharacters(){
		Sprite temp;
		
		TextureRegion t = TextureManager.getAtlas("sides").findRegion(battleController.player.getCode());		
		temp= new Sprite(t);		
		temp.setScale(resizeRatio);
		//temp.setOrigin(0, 0);
		player= new ExtendedSprite(temp);
		//player.centerOnPosition(-768*resizeRatio, 0);
		player.centerOnPosition(-camera.viewportWidth, 0);
		
		t = TextureManager.getAtlas("sides").findRegion("shield");
		temp= new Sprite(t);
		temp.setScale(resizeRatio);
		shield= new ExtendedSprite(temp);
		shield.centerOnPosition(-camera.viewportWidth, 0);
		shield.setColor(new Color(1,1,1,0));
		
		t = TextureManager.getAtlas("sides").findRegion(battleController.enemy.getCode());
		temp=new Sprite(t);
		temp.setScale(resizeRatio);
		//temp.setOrigin(0, 0);
		enemy= new ExtendedSprite(temp);
		enemy.centerOnPosition(camera.viewportWidth, 0);
	}
	
	private void drawHealthBars(){
		Sprite temp;
		TextureRegion t_hBarBG= TextureManager.getAtlas("sides").findRegion("healthBar-bg");
		TextureRegion t_hBar= TextureManager.getAtlas("sides").findRegion("healthBar");
		TextureRegion t_aBar= TextureManager.getAtlas("sides").findRegion("armorBar");
		
		temp=new Sprite(t_hBarBG);
		temp.setScale(resizeRatio);
		pHealthBar_bg = new ExtendedSprite(temp);
		float x=player.getPositionOfCenter().x;
		float y= camera.viewportHeight/2-pHealthBar_bg.getVirtualHeight(); //player.getVirtualPosition().y-pHealthBar_bg.getVirtualHeight();
		pHealthBar_bg.centerOnPosition(x, y);
		
		temp=new Sprite(temp);
		eHealthBar_bg = new ExtendedSprite(temp);
		x=enemy.getPositionOfCenter().x;
		//y=enemy.getVirtualPosition().y-pHealthBar_bg.getVirtualHeight();
		eHealthBar_bg.centerOnPosition(x, y);
		
		temp=new Sprite(t_hBar);
		temp.setScale(resizeRatio);
		pHealthBar=new ExtendedSprite(temp);		
		x=pHealthBar_bg.getPositionOfCenter().x+(pHealthBar_bg.getVirtualWidth()-pHealthBar.getVirtualWidth())/2;
		y=pHealthBar_bg.getPositionOfCenter().y;
		pHealthBar.centerOnPosition(x, y);
		
		temp=new Sprite(temp);
		eHealthBar=new ExtendedSprite(temp);		
		x=eHealthBar_bg.getPositionOfCenter().x+(eHealthBar_bg.getVirtualWidth()-eHealthBar.getVirtualWidth())/2;
		y=eHealthBar_bg.getPositionOfCenter().y;
		eHealthBar.centerOnPosition(x, y);
		
		temp=new Sprite(t_aBar);
		temp.setScale(resizeRatio);
		pArmorBar=new ExtendedSprite(temp);
		pArmorBar.centerOnPosition(pHealthBar.getPositionOfCenterV2());
		
		temp=new Sprite(temp);
		eArmorBar=new ExtendedSprite(temp);
		eArmorBar.centerOnPosition(eHealthBar.getPositionOfCenterV2());
	}
	
	private void update(){
		if(battleController.state==BattleController.State.TRANSITIONING)
			transition();
		else if(battleController.state==BattleController.State.ENDING)
			end();
		updateHealthBars();
		if(battleController.playerDamaged)
			Tween.to(player, ExtendedSpriteAccessor.COLOR, .1f)
			.target(1,0,0)
			.repeatYoyo(1, 0)									
			.ease(Linear.INOUT)
			.start(tweenManager);
	}
	
	private void updateHealthBars(){
		Character p=battleController.getPlayer();
		float pCent=p.getHealthPoints()/(float)p.getBaseHealth();
		pHealthBar.setWidth(pHealthBar.getOriginalWidth()*pCent);
		if(p.getArmor()!=null){
			pCent=p.getArmor().getArmorValue()/(float)p.getArmor().getBaseArmor();
			pArmorBar.setWidth(pArmorBar.getOriginalWidth()*pCent);
		}else pArmorBar.setWidth(0);
		
		Character e=battleController.getEnemy();
		pCent=e.getHealthPoints()/(float)e.getBaseHealth();
		eHealthBar.setWidth(eHealthBar.getOriginalWidth()*pCent);
		if(e.getArmor()!=null){
			pCent=e.getArmor().getArmorValue()/(float)e.getArmor().getBaseArmor();
			eArmorBar.setWidth(eArmorBar.getOriginalWidth()*pCent);
		}else eArmorBar.setWidth(0);
	}
	
	boolean endDone=false;
	private void end(){
		if(!endDone){
		ExtendedSprite e;
		ExtendedSprite eBar;
		ExtendedSprite eBar_bg;
			if(battleController.getPlayerHealth()<=0){
				e=player;
				eBar=pHealthBar;
				eBar_bg=pHealthBar_bg;
			}
			else{
				e=enemy;
				eBar=eHealthBar;
				eBar_bg=eHealthBar_bg;
			}
			Tween.to(e, ExtendedSpriteAccessor.ALPHA, BattleController.END_DELAY)
				.target(0)
				.start(tweenManager);
			Tween.to(eBar, ExtendedSpriteAccessor.ALPHA, BattleController.END_DELAY)
			.target(0)
			.start(tweenManager);
			Tween.to(eBar_bg, ExtendedSpriteAccessor.ALPHA, BattleController.END_DELAY)
			.target(0)
			.start(tweenManager);
			endDone=true;
		}			
	}
	
	private void drawTarget(Target t){
		String region;
		
		if(!battleController.atkPhase)
			region="aTarget";
		else 
			region="dTarget";//TODO: dTaarget
		TextureRegion texure = TextureManager.getAtlas("sides").findRegion(region);
		Sprite s= new Sprite(texure);
		s.setScale(resizeRatio);
		//s.setOrigin(0, 0);
		
		ExtendedSprite e; 
		if(battleController.state==BattleController.State.ATTACKING)
			e= drawAttackTarget(t,s);			
		else
			e=drawDefenseTarget(t,s);
		targets.put(t, e);
	}
	
	private ExtendedSprite drawAttackTarget(Target t, Sprite s){
		ExtendedSprite e=new ExtendedSprite(s);
		float spawnX, spawnY, spawnWidth, spawnHeight;
		
		//Calcula a largura e comprimento onde o alvo pode aparecer na personagem
		if(t.isFixed){
			spawnX= enemy.getVirtualPosition().x;
			spawnY= enemy.getVirtualPosition().y;
			spawnWidth= enemy.getVirtualWidth(); 			
			spawnHeight= enemy.getVirtualHeight();
		}
		else{
			spawnX= camera.viewportWidth/2-e.getVirtualWidth()/2;
			spawnY= -camera.viewportHeight/2+e.getVirtualHeight()/2;
			spawnWidth= 1; 			
			spawnHeight= enemy.getVirtualPosition().y-spawnY;
		}			
		
		int x=(int)(spawnX+Math.random()*spawnWidth);
		int y=(int)(spawnY+Math.random()*spawnHeight);
		
		e.centerOnPosition(x, y);
		//Gdx.app.log(TAG, "targetPosition: " + e.getPosition());
		
		Tween tween= Tween.to(e, ExtendedSpriteAccessor.ROTATION, 2)
			.target(360)
			.ease(Linear.INOUT)
			.repeat(-1, 0)
			.start(tweenManager);
		tweens.put(t, tween);
		
		if(!t.isFixed){
			 tween= Tween.to(e, ExtendedSpriteAccessor.POSITION_X, t.duration)
				.target(camera.viewportWidth*2)
				.ease(Linear.INOUT)
				.start(tweenManager);
			 tweens.put(t, tween);
		}
		
		
		
		
		return e;
	}
	
	private ExtendedSprite drawDefenseTarget(Target t, Sprite s){
		ExtendedSprite e=new ExtendedSprite(s);
		
		float x= bar.getVirtualPosition().x;
		float y=bar.getPositionOfCenter().y;
		if(Math.random()<.5f)
			x+=bar.getVirtualWidth();
		
		
		e.centerOnPosition(x, y);
		
		Tween tween= Tween.to(e, ExtendedSpriteAccessor.POSITION_X_CENTER, t.duration)
			.target(dButton.getPositionOfCenter().x)
			.ease(Quad.OUT)
			.start(tweenManager);
		tweens.put(t, tween);
		return e;
	}
	
	private void transition(){
		
		if(targets.size()>0){
			targets.clear();	//			
			tweenManager.killAll();
			player.setColor(Color.WHITE);
			enemy.setColor(Color.WHITE);
			shield.setColor(new Color(1,1,1,0));
		}
		if(camTarget==null){
			Gdx.app.log(TAG, "Setting Tween");
			setCamTarget();
			Tween.to(camera, CameraAccessor.POSITION_X, BattleController.TRANSITION_DURATION)
				.target(camTarget.x)
				.ease(Cubic.INOUT)
				.start(tweenManager);				
		}
		if(tweenManager.getRunningTweensCount()==0){
			Gdx.app.log(TAG, "They're dead! the tweens are dead!");
			camTarget=null;
		}
	}
	
	private void setCamTarget(){
		if(battleController.atkPhase){
			camTarget=enemy.getPositionOfCenter();			
		}
		else
			camTarget=player.getPositionOfCenter();
	}
}
