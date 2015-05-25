package utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ExtendedSprite {
	private final float originalWidth;
	private Vector2 position;
	private Sprite parent;
	private float rotation;
	private boolean invalid;
	private Color color;
	
	
	public ExtendedSprite(Sprite sprite){		
		position=new Vector2(sprite.getX(),sprite.getY());
		parent=sprite;
		color=sprite.getColor();
		invalid=true;
		originalWidth=sprite.getWidth();
	}
	
	public boolean invalid(){return invalid;}
		

	public void setPosition(float x, float y){
		position=new Vector2(x,y);	
		CenterSprite(parent);	
		invalid=true;
	}
	
	public void setRotation(float angle){
		rotation=angle;
		parent.setRotation(angle);
		
	}
	
	public float getRotation(){
		return rotation;
	}
	
	public void CenterSprite(Sprite s){
		Rectangle pRect=parent.getBoundingRectangle();
		Rectangle sRect=s.getBoundingRectangle();
		s.setPosition(position.x+(pRect.width/2-sRect.width/2),
					  position.y+( pRect.height/2-sRect.height/2));
	}
	
	public void centerOnPosition(float x, float y){
		x=	(x - parent.getWidth()/2) ;
		y=	(y - parent.getHeight()/2) ;
		setPosition(x,y);
	}
	
	public void  centerOnPosition(Vector2 v){
		centerOnPosition(v.x,v.y);
	}
	
	public Vector3 getPositionOfCenter(){
		Vector2 center=Vector2.Zero;
		parent.getBoundingRectangle().getCenter(center);
		return new Vector3(center.x,center.y,0);
		//return new Vector3(position.x+parent.getWidth()/2,position.y+parent.getHeight()/2, 0);
	}
	
	public Vector2 getPositionOfCenterV2(){
		Vector2 center=Vector2.Zero;
		parent.getBoundingRectangle().getCenter(center);
		return new Vector2(center.x,center.y);
		//return new Vector3(position.x+parent.getWidth()/2,position.y+parent.getHeight()/2, 0);
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public float getVirtualWidth(){
		return parent.getBoundingRectangle().getWidth();
	}
	
	public float getWidth(){
		return parent.getWidth();
	}
	
	public float getVirtualHeight(){
		return parent.getBoundingRectangle().getHeight();
	}
	
	public float getHeight(){
		return parent.getHeight();
	}
	
	public Vector3 getVirtualPosition(){
		Rectangle r= parent.getBoundingRectangle();
		return new Vector3(r.getX(),r.getY(),0);
	}
	
	public void draw(SpriteBatch batch){
//		if(invalid){
//			parent.setColor(batch.getColor());
			Color bColor=batch.getColor();
			Color fColor=new Color(color.r * bColor.r, color.g*bColor.g, color.b * bColor.b, color.a);
			parent.setColor(fColor);
			parent.draw(batch);			
			
			invalid=false;
//		}
	}
	
	public void drawDebug(ShapeRenderer r){
			//r.rect(s.getX(), s.getY(), s.getWidth(), s.getHeight());
		Rectangle rect=parent.getBoundingRectangle();
		r.rect(rect.x, rect.y, rect.width, rect.height);
		r.circle(parent.getOriginX(), parent.getOriginY(), 2);
		invalid=false;
	}
	
	public Rectangle boundingBox(){
		return parent.getBoundingRectangle();
	}
	
	
	
	public void setWidth(float w){
		parent.setSize(w, parent.getHeight());
	}
	
	public float getOriginalWidth(){
		return originalWidth;
	}
	
	public float getOriginalVirtualWidth(){
		return originalWidth*parent.getScaleX();
	}
	
	public void setColor(Color c){
		color=c;
	}
	
	public void setColor(float r, float g, float b, float a){
		if(a==-1)
			color =new Color(r,g,b,color.a);
		else
			color =new Color(r,g,b,a);
	}
	
	public Color getColor(){
		return color;
	}
	
	public void reset(){
		invalid=true;
		color=Color.WHITE;
	}
	
	public void invalidate(){
		invalid=true;
	}
}
