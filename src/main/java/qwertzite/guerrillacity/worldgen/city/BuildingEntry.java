package qwertzite.guerrillacity.worldgen.city;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
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
	
	public void generate(Map<BlockPos, BlockState> context) {
		
	}

	
}
