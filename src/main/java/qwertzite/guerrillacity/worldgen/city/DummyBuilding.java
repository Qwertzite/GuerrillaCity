package qwertzite.guerrillacity.worldgen.city;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DummyBuilding extends BuildingEntry {

	private int y;
	
	public DummyBuilding(BoundingBox circumBox, int y) {
		super(circumBox);
		this.y = y;
	}

	public void generate(Map<BlockPos, BlockState> context) {
		BoundingBox bb = this.getCircumBox();
		for (int x = bb.minX(); x <= bb.maxX(); x++) {
			for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
				context.put(new BlockPos(x, this.y, z), Blocks.GOLD_BLOCK.defaultBlockState());
			}
		}
	}
}
