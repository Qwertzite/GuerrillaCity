package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class DummyBuilding extends CityElement {

	private int y;
	private BlockState state;
	private BlockPos centre;
	private Direction direction;
	
	public DummyBuilding(BoundingBox circumBox, int y) {
		this(circumBox, y, Blocks.GRAY_STAINED_GLASS.defaultBlockState());
	}
	public DummyBuilding(BoundingBox circumBox, int y, BlockState state) {
		super(circumBox);
		this.y = y;
		this.state = state;
	}
	public DummyBuilding(BoundingBox circumBox, int y, BlockState state, BlockPos centre, Direction dir) {
		this(circumBox, y, state);
		this.centre = centre;
		this.direction = dir;
	}
	
	@Override
	public void generate(CityGenContext context) {
		BoundingBox bb = this.getCircumBox();
		for (int x = bb.minX(); x <= bb.maxX(); x++) {
			for (int z = bb.minZ(); z <= bb.maxZ(); z++) {
				context.setBlockState(new BlockPos(x, this.y, z), state);
			}
		}
		if (this.centre != null) {
			BlockState state = Blocks.REDSTONE_BLOCK.defaultBlockState();
			context.setBlockState(centre, state);
			context.setBlockState(centre.relative(direction, -1), state);
			context.setBlockState(centre.relative(direction, -2), state);
			context.setBlockState(centre.relative(direction.getCounterClockWise(), -1), state);
		}
	}
}
