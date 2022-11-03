package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import qwertzite.guerrillacity.core.util.math.Rectangle;

public class DummyBuilding extends CityElement {

	private int y;
	private BlockState state;
	private BlockPos centre;
	private Direction direction;
	
	public DummyBuilding(Rectangle circumBox, int y) {
		this(circumBox, y, Blocks.GRAY_STAINED_GLASS.defaultBlockState());
	}
	public DummyBuilding(Rectangle circumBox, int y, BlockState state) {
		super(circumBox);
		this.y = y;
		this.state = state;
	}
	public DummyBuilding(Rectangle circumBox, int y, BlockState state, BlockPos centre, Direction dir) {
		this(circumBox, y, state);
		this.centre = centre;
		this.direction = dir;
	}
	
	@Override
	public void generate(CityGenContext context) {
		Rectangle bb = this.getCircumBox();
		for (int x = bb.getMinX(); x <= bb.getMaxX(); x++) {
			for (int z = bb.getMinY(); z <= bb.getMaxY(); z++) {
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
