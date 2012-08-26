/**
 * 
 */
package de.findus.cydonia.building;

/**
 * @author Findus
 *
 */
public class BuilderBPO {
	
	public void build(BuilderEntity builder) {
		for(int y=0;y<3;y++) {
			for(int x=0;x<3;x++) {
				if(builder.getComponent(x, y) == null) {
					continue;
				}
				
				// TODO: baumsuche auf zusammenhang
				
				
			}
		}
	}

}
