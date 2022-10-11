package qwertzite.guerrillacity.worldgen.city;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CityGenContext {
	
	private BoundingBox genArea;
	private Map<BlockPos, BlockState> resultMap = new HashMap<>();
	
	public CityGenContext(BoundingBox genArea) {
		this.genArea = genArea;
	}
	
	public void setBlockState(BlockPos pos, BlockState state) {
		if (this.genArea.isInside(pos)) {
			this.resultMap.put(pos, state);
		}
	}
	
	
	public Map<BlockPos, BlockState> getStateMap() { return this.resultMap; }
}
