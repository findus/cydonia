/**
 * 
 */
package de.findus.cydonia.building;

import de.findus.cydonia.component.ComponentEntity;
import de.findus.cydonia.component.ComponentGroupEntity;


/**
 * @author Findus
 *
 */
public class BuilderBPO {
	
	public void build(BuilderEntity builder) {
		if(!this.isConnected(builder) || builder.getComponentList().isEmpty()) {
			return;
		}
		
		int top = (int) builder.getSize().getHeight();
		int bottom = 0;
		int left = (int) builder.getSize().getWidth();
		int right = 0;
		
		for(int x=0; x<builder.getSize().getWidth(); x++) {
			for(int y=0; y<builder.getSize().getHeight(); y++) {
				if(builder.getComponent(x, y) != null) {
					if(y<top) top=y;
					if(y>bottom) bottom=y;
					if(x<left) left=x;
					if(x>right) right=x;
				}
			}
		}
		int width = right-left;
		int height = bottom-top;
		
		ComponentEntity[][] components = new ComponentEntity[width][height];
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				components[x][y] = builder.getComponent(x+left, y+top);
			}
		}
		
		ComponentGroupEntity group = new ComponentGroupEntity();
		group.setComponents(components);
	}

	private boolean isConnected(BuilderEntity builder) {
		// TODO: implement
		return true;
	}

}
