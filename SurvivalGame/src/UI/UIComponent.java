package UI;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.ename.diogo.martins.survival.Game_State;

public abstract class UIComponent {

	Table root;
	Skin skin;
	
	public UIComponent(Skin s){
		skin=s;
		root=new Table();
	}
	
	public Table getTable(){
		return root;
	}
	
	public abstract void updateComponent(Game_State gameState);

}
