/**
 * 
 */
package de.findus.cydonia.building;

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
		
		int top = Integer.MAX_VALUE;
		int bottom = 0;
		int left = Integer.MAX_VALUE;
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
		
		ComponentGroupEntity group = new ComponentGroupEntity();
		
		// TODO: Maße setzen und Komponenten kopieren.
	}

	private boolean isConnected(BuilderEntity builder) {
		// TODO: implement
		return true;
	}

}
