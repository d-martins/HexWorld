package utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

public class ExtendedSpriteAccessor implements TweenAccessor<ExtendedSprite> {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;
	public static final int ROTATION=4;
	public static final int POSITION_X_CENTER = 5;
	public static final int POSITION_Y_CENTER = 6;
	public static final int POSITION_XY_CENTER = 7;
	public static final int COLOR=8;
	public static final int ALPHA=9;
	public static final int ROTATION_ALPHA=10;

	@Override
	public int getValues(ExtendedSprite target, int tweenType,
			float[] returnValues) {
		switch(tweenType){
			case POSITION_X: returnValues[0]=target.getPosition().x; return 1; //.getPositionOfCenter().x; return 1;
			case POSITION_Y: returnValues[0]=target.getPosition().y; return 1; //getPositionOfCenter().y; return 1;
			case POSITION_XY:
				returnValues[0]=target.getPosition().x; //.getPositionOfCenter().x; return 1;
				returnValues[1]=target.getPosition().y; //getPositionOfCenter().y; return 1;
				return 2;
			case ROTATION:
				returnValues[0]=target.getRotation();
				return 1;
			case POSITION_X_CENTER: returnValues[0]=target.getPositionOfCenter().x; return 1;
			case POSITION_Y_CENTER: returnValues[0]=target.getPositionOfCenter().y; return 1;
			case POSITION_XY_CENTER: 
				Vector2 pC=target.getPositionOfCenterV2(); 
				returnValues[0]=pC.x; returnValues[1]=pC.y;  
				return 2;
			case COLOR: Color c= target.getColor(); returnValues[0]=c.r; returnValues[1]=c.g; returnValues[2]=c.b; return 3;
			case ALPHA: returnValues[0]= target.getColor().a; return 1;
			case ROTATION_ALPHA: returnValues[0]=target.getRotation();returnValues[1]=target.getColor().a; return 2; 
			default: assert false; return -1;						
		}
	}

	@Override
	public void setValues(ExtendedSprite target, int tweenType,
			float[] newValues) {
		 switch (tweenType) {
         case POSITION_X: 
        	 target.setPosition(newValues[0],target.getPosition().y); break;
         case POSITION_Y:
        	 target.setPosition(target.getPosition().x,newValues[0]); break;
         case POSITION_XY:
        	 target.setPosition(newValues[0],newValues[1]); break;
         case ROTATION:
        	 target.setRotation(newValues[0]); break;
         case POSITION_X_CENTER:	
        	 target.centerOnPosition(newValues[0], target.getPositionOfCenter().y);break;
         case POSITION_Y_CENTER:
        	 target.centerOnPosition(target.getPositionOfCenter().x, newValues[0]);break;
         case POSITION_XY_CENTER:
        	 target.centerOnPosition(newValues[0], newValues[1]);break;
         case COLOR: target.setColor(new Color(newValues[0],newValues[1],newValues[2],target.getColor().a)); break;
         case ALPHA: Color c= target.getColor(); target.setColor(new Color(c.r,c.g,c.b,newValues[0])); break;
         case ROTATION_ALPHA:
        	 Color cl= target.getColor(); target.setColor(new Color(cl.r,cl.g,cl.b,newValues[1]));
        	 target.setRotation(newValues[0]); break;
         default: assert false; break;
 	
		 }
	}
}
