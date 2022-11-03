package qwertzite.guerrillacity.worldgen.city;

import qwertzite.guerrillacity.core.util.math.Rectangle;

public class CityElement {
	
	private Rectangle circumBox;
	
	public CityElement(Rectangle circumBox) {
		this.circumBox = circumBox;
	}
	
	/**
	 * All consisting blocks must be within this BoudingBox, including embankment blocks and surrounding air blocks to ensure spacing between other biomes.
	 * @return
	 */
	public Rectangle getCircumBox() {
		return this.circumBox;
	}
	
	public void generate(CityGenContext context) {
		
	}
	
	
}
