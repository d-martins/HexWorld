package utils;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.ename.diogo.martins.survival.Maps.Game_Map;
import com.ename.diogo.martins.survival.Maps.Tile;

public class HexMath {
	@SuppressWarnings("unused")
	private static String TAG="HEX_MATH";
	
	public static Vector2 AxialToPixelCoordinates(int q, int r,float tileSize){
		float x,y;
		x=3f/2f*tileSize*q;
		y=(float)Math.sqrt(3)*(r+q/2f)*tileSize;
		return new Vector2(x,y);
	}
	
	public static Vector2 PixelToAxialCoordinates(float x, float y, float tileSize){
		float q,r;
		q=2f/3f*x/tileSize;
		r=(float)(1f/3f*Math.sqrt(3)*y-x/3f)/tileSize;
		
		Vector2 coords=RoundToNerestHex(q,r);
		return coords;
	}	
	public static int[] offsetToAxialCoordinates(int q, int r){
		int x,z;
		x=q;
		z=r-(q-(q&1))/2;
		return new int[] {x,z};
	}
		
	public static Vector2 RoundToNerestHex(float q, float r){
		float x,y,z;
			
		x=q; z=r; y=-x-z;
		
		int rx,ry,rz;
		float dif_x,dif_y,dif_z;
		
		rx=Math.round(x);
		ry=Math.round(y);
		rz=Math.round(z);
		
		dif_x = Math.abs(rx - x);
		dif_y = Math.abs(ry - y);
		dif_z = Math.abs(rz - z);
		
		if (dif_x > dif_y && dif_x > dif_z)
			rx = -ry-rz;
		else if (dif_y > dif_z)
		    ry = -rx-rz;
		else
		    rz = -rx-ry;
		
		return new Vector2(rx,rz);		
	}
	
	public static Vector2[] FindHexesOfRing(Vector2 center, int radius){
		int index=0;
		Vector2[] results= new Vector2[6*radius];
		Vector2 Hex=center.cpy().add(HexDirections.SOUTH_WEST.direction.cpy().scl(radius));
		
		for(HexDirections h : HexDirections.values()){
			for(int i=0; i<radius;i++){
				results[index]=Hex;
				Hex=Hex.cpy().add(h.direction);
				index++;
			}
		}
		
		return results;
	}
	
	public static ArrayList<Tile> FindHexesOfRing(Tile center, int radius, Game_Map map){
		ArrayList<Tile> results= new ArrayList<Tile>();
		
		
		Vector2 hex=center.getPosition().cpy().add(HexDirections.SOUTH_WEST.direction.cpy().scl(radius));
		
		for(HexDirections h : HexDirections.values()){
			for(int i=0; i<radius;i++){
				if(map.getTiles().containsKey(hex))
					results.add(map.getTiles().get(hex));
				hex=hex.cpy().add(h.direction);
			}
		}
		
		return results;
	}
	
	public static Vector2[] FindHexesOfLine(Vector2 hexA, Vector2 hexB){
		float distance=HexDistance(hexA,hexB);
		Vector2[] results= new Vector2[(int) (distance+1)];
		
		for(int i=0;i<=distance;i++){			
			Vector2 h=hexA.cpy().scl(1-i/distance).add(hexB.cpy().scl(i/distance));
			results[i]=RoundToNerestHex(h.x, h.y).cpy();
		}
		
		return results;
	}
	
	public static int HexDistance(Vector2 hexA, Vector2 hexB){
		return (int) ((Math.abs(hexA.x-hexB.x) + Math.abs(hexA.y-hexB.y) + Math.abs(hexA.x+hexA.y - hexB.x-hexB.y))/2);
		
	}
	
	public static enum HexDirections{
		NORTH		(1, new Vector2(0,-1)),
		NORTH_EAST	(2, new Vector2(1,-1)),
		SOUTH_EAST	(3, new Vector2(1, 0)),
		SOUTH		(4, new Vector2(0, 1)),
		SOUTH_WEST	(5, new Vector2(-1,1)),
		NORTH_WEST	(6, new Vector2(-1,0));
		
		public Vector2 direction;
		public int index;
		
		HexDirections(int ind, Vector2 dir){
			direction=dir;
			index=ind;
		}
		
		public static HexDirections getDirection(Vector2 v){
			for(HexDirections h : HexDirections.values()){
				if(h.direction== v)
					return h;
			}
			return null;
		}
		
		public static HexDirections[] getAdjacent(HexDirections h){
			switch(h){
			case NORTH:
				return new HexDirections[]{NORTH_EAST,NORTH_WEST};
			case NORTH_EAST:
				return new HexDirections[]{NORTH, SOUTH_EAST};
			case SOUTH_EAST:
				return new HexDirections[]{NORTH_EAST,SOUTH};
			case SOUTH:
				return new HexDirections[]{SOUTH_EAST,SOUTH_WEST};
			case SOUTH_WEST:
				return new HexDirections[]{SOUTH,NORTH_WEST};
			case NORTH_WEST:
				return new HexDirections[]{NORTH,SOUTH_WEST};
				
			}
			
			return null;
		}
	}
}
