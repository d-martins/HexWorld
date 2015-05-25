package utils;

import com.badlogic.gdx.graphics.OrthographicCamera;

import aurelienribon.tweenengine.TweenAccessor;

public class CameraAccessor implements TweenAccessor<OrthographicCamera> {

	public static final int POSITION_X = 1;
	public static final int POSITION_Y = 2;
	public static final int POSITION_XY = 3;
	
	@Override
	public int getValues(OrthographicCamera target, int tweenType,
			float[] returnValues) {
		switch(tweenType){
			case POSITION_X: returnValues[0]=target.position.x; return 1;
			case POSITION_Y: returnValues[0]=target.position.y; return 1;
			case POSITION_XY:
				returnValues[0]=target.position.x;
				returnValues[1]=target.position.y;
				return 2;
			default: assert false; return -1;						
		}
	}

	@Override
	public void setValues(OrthographicCamera target, int tweenType,
			float[] newValues) {
		 switch (tweenType) {
	         case POSITION_X: 
	        	 target.position.set(newValues[0],target.position.y,target.position.z); target.update(); break;
	         case POSITION_Y:
	        	 target.position.set(target.position.x,newValues[0],target.position.z); target.update();break;
	         case POSITION_XY:
	        	 target.position.set(newValues[0],newValues[1],target.position.z); target.update();break;
         default: assert false; break;
     }

		
	}

}
