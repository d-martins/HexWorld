package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.ename.diogo.martins.survival.Maps.Game_Map;
import com.ename.diogo.martins.survival.Maps.Tile;

public class A_Star {
	@SuppressWarnings("unused")
	private static String TAG="A*";
	
	
	public static ArrayList<Tile> getPathToTile(Tile origin, Tile target, Game_Map map){
		ArrayList<Tile> neighbours=map.getTileNeighours(origin);
		ArrayList<Tile> occupied= new  ArrayList<Tile>(neighbours);		
		//Removes all the tiles arround the origin with enemies on them
		for(int i=0; i< neighbours.size();i++){
			Tile t=neighbours.get(i);
			if(t.getCharacters().size()>0 && t!=target){
				neighbours.remove(i);
				i--;
			}
		}
		occupied.removeAll(neighbours);
		Gdx.app.log(TAG, "occupied: " +occupied.size());
		//long startTime=System.nanoTime();
		Comparator<Node> c=new Comparator<Node>(){
			@Override
			public int compare(Node o1, Node o2) {
				if(o1.getF()<o2.getF())
					return -1;
				else if(o1.getF()>o2.getF())
					return 1;
				return 0;
			}			
		};
		
		PriorityQueue<Node> open=new PriorityQueue<Node>(1,c);
		PriorityQueue<Node> closed=new PriorityQueue<Node>(1,c);
		HashMap<Tile,Node> nodes=new HashMap<Tile,Node>(); 
		
		for(Map.Entry<Vector2, Tile> entry: map.getTiles().entrySet()){
			Tile t=entry.getValue();
			if(!occupied.contains(t)){
				Node node=new Node(t);
				int dist=HexMath.HexDistance(t.getPosition(), target.getPosition());
				node.h=dist;			
				nodes.put(t, node);
			}
		}
		open.add(nodes.get(origin));
		Node current= new Node(null);
		Node end = null;
		
		while(current.tile != target){
			Gdx.app.log(TAG, "open: " +open.size());
			if(open.size()==0)break;
			current = open.remove();
			closed.add(current);	
			//Gdx.app.log(TAG, "closed.size: " + closed.size()+ " closed" + closed.contains(current));
			/*String parent="null";
			if(current.cameFrom!= null)
				parent=""+current.cameFrom.tile.getPosition();*/
			
			//Gdx.app.log(TAG, "current:" + current.tile.getPosition() + " h:"+ current.h + " f:"+ current.getF() + " parent:" + parent);
			if(current.tile == target){
				end=current;
				//Gdx.app.log(TAG, "breaking" + end.cameFrom.cameFrom.cameFrom.cameFrom.tile.getPosition());
			}	
			for(Tile t :map.getTileNeighours(current.tile)){
				if(nodes.containsKey(t)){
					Node neighbour=nodes.get(t);				
					int cost=current.g + neighbour.tile.getBaseTile().getMoveCost();
					//Gdx.app.log(TAG, "cost:" + cost + " tile: "+ neighbour.tile.getPosition()+" h: "+ neighbour.h);
					if(open.contains(neighbour) && cost < neighbour.g){
						//Gdx.app.log(TAG, "unOpening: "+ neighbour.tile.getPosition());
						open.remove(neighbour);
					}
					if(closed.contains(neighbour) && cost < neighbour.g){
						closed.remove(neighbour);
						//Gdx.app.log(TAG, "unclosing: "+ neighbour.tile.getPosition());
					}
					if(!open.contains(neighbour) && !closed.contains(neighbour)){
						neighbour.g=cost;
						open.add(neighbour);
						neighbour.cameFrom=current;
						//Gdx.app.log(TAG, "reparenting: "+ neighbour.tile.getPosition());
					}
					//Gdx.app.log(TAG, "f(): " + neighbour.getF());
				}
			}
		}
		//Gdx.app.log(TAG, "target: " + target.getPosition());
		//Gdx.app.log(TAG, "closed top: "+ closed.remove().tile.getPosition());//.getF()
		//Gdx.app.log(TAG, "open top: "+ open.remove().tile.getPosition());//.getF()		
		ArrayList<Tile> results= new ArrayList<Tile>();
		if(end==null)
			return results;		
		while(end.cameFrom !=null){
			//Gdx.app.log(TAG, "path: "+ end.tile.getPosition());
			results.add(end.tile);
			end=end.cameFrom;
		}
		//long endTime=System.nanoTime();
		//Gdx.app.log(TAG, "a* took: " + ((endTime-startTime)/1e6));
		return results;
	}
	
	private static class Node{
		Tile tile;
		int h; //heuristic
		int g; //distance along path
		Node cameFrom;
		
		public Node(Tile t){
			tile=t;
			h=0;
			g=0;
			cameFrom=null;
		}
		
		public int getF(){return g+h;}
		
		
	}

}
