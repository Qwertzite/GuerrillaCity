package qwertzite.guerrillacity.worldgen.city;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import qwertzite.guerrillacity.construction.GcConstructionModule;
import qwertzite.guerrillacity.core.util.math.Rectangle;

public class DummyBuilding extends CityElement {

	private int y;
	private BlockState state;
	private BlockPos centre;
	private Direction direction;
	
	private boolean flat;
	private int stories;
	
	public DummyBuilding(Rectangle circumBox, int y) {
		this(circumBox, y, Blocks.GRAY_STAINED_GLASS.defaultBlockState());
	}
	public DummyBuilding(Rectangle circumBox, int y, BlockState state) {
		super(circumBox);
		this.y = y;
		this.state = state;
	}
	public DummyBuilding(Rectangle circumBox, int y, BlockState state, BlockPos centre, Direction dir, long seed) {
		this(circumBox, y, state);
		this.centre = centre;
		this.direction = dir;
		Random rand = new Random(seed);
		this.flat = rand.nextBoolean();
		this.stories = rand.nextInt(1, 5) + rand.nextInt(1, 3);
	}
	
	@Override
	public void generate(CityGenContext context) {
		Rectangle bb = this.getCircumBox();
//		for (int x = bb.getMinX(); x <= bb.getMaxX(); x++) {
//			for (int z = bb.getMinY(); z <= bb.getMaxY(); z++) {
//				for (int y = this.y-1; y <= this.y; y++) {
//					context.setBlockState(new BlockPos(x, y, z), this.state);
//				}
//			}
//		}
		BlockState state = Blocks.REDSTONE_BLOCK.defaultBlockState();
		BlockState concrete = GcConstructionModule.REINFORCED_CONCRETE.get().defaultBlockState();
		context.setBlockState(new BlockPos(bb.getMinX(), this.y+1, bb.getMinY()), concrete);
		context.pushMatrix();
		context.translate(bb.getMinX(), this.y, bb.getMinY());
		context.setBlockState(new BlockPos(bb.getMinX(), 2, bb.getMinY()), state);
		for (int x = 0; x < bb.getXSpan(); x++) {
			for (int z = 0; z < bb.getYSpan(); z++) {
				context.setBlockState(new BlockPos(x, -1, z), concrete);
			}
		}
		for (int s = 0; s < this.stories; s++) {
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < bb.getXSpan()-2; x += 2) {
					context.setBlockState(new BlockPos(                x,     y, 0), concrete);
					context.setBlockState(new BlockPos(bb.getXSpan() - x - 1, y, bb.getYSpan() - 1), concrete);
				}
				for (int z = 0; z < bb.getYSpan()-2; z += 2) {
					context.setBlockState(new BlockPos(bb.getXSpan()-1, y,                 z), concrete);
					context.setBlockState(new BlockPos(              0, y, bb.getYSpan() - z - 1), concrete);
				}
			}
			for (int x = 0; x < bb.getXSpan(); x++) {
				for (int z = 0; z < bb.getYSpan(); z++) {
					context.setBlockState(new BlockPos(x, 4, z), concrete);
				}
			}
			context.translate(0, 5, 0);
		}
		context.popMatrix();
		if (this.centre != null) {
			context.setBlockState(centre, state);
			context.setBlockState(centre.relative(direction, -1), state);
			context.setBlockState(centre.relative(direction, -2), state);
			context.setBlockState(centre.relative(direction.getCounterClockWise(), -1), state);
		}
	}
}
