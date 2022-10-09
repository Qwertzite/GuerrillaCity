package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BuildingEntry {
	
	private BoundingBox circumBox;
	
	public BuildingEntry(BoundingBox circumBox) {
		this.circumBox = circumBox;
	}
	
	/**
	 * All consisting blocks must be within this BoudingBox, including embankment blocks and surrounding air blocks to ensure spacing between other biomes.
	 * @return
	 */
	public BoundingBox getCircumBox() {
		return this.circumBox;
	}
	
}
