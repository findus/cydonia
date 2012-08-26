/**
 * 
 */
package de.findus.cydonia.crafting;

import de.findus.cydonia.component.ComponentDAO;
import de.findus.cydonia.component.ComponentEntity;

/**
 * @author Findus
 *
 */
public class CraftingBPO {
	
	/**
	 * Crafts a new component.
	 * @param archetype the type definition of the new component
	 * @param components the components that will be used to build the new component
	 * @return the id of the new component
	 */
	public long craft(ArchetypeEntity archetype, ComponentEntity... components) {
		ComponentEntity result = new ComponentEntity();
		result.setName(archetype.getName());
		
		int flexibility = 0;
		int resistance = 0;
		for (ComponentEntity com : components) {
			flexibility += com.getFlexibility();
			resistance += com.getResistance();
		}
		flexibility /= components.length;
		resistance /= components.length;
		result.setFlexibility(flexibility);
		result.setResistance(resistance);
		
		return ComponentDAO.getInstance().saveNew(result);
	}

}
