package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import qwertzite.guerrillacity.core.util.math.Rectangle;

public class BuildingType {
	private final String typeName;
	private final int width;
	private final int length;
	private final MarginSettings margin;
	private final int baseWeight;
	
	private final BlockState blockState;
	
	public BuildingType(String name, int width, int length, MarginSettings margin, int weight, BlockState state) {
		this.typeName = name;
		this.width = width;
		this.length = length;
		this.margin = margin;
		this.baseWeight = (weight + 1)*width*length;
		this.blockState = state;
	}
	
	public String getTypeName() { return this.typeName; }
	public int getWidth () { return this.width; }
	public int getLength() { return this.length; }
	public MarginSettings getMarginRestriction() { return this.margin; }
	public int getWeight() { return this.baseWeight; }
	
	/**
	 * 
	 * @param pos
	 * @param dir front side of new building.
	 * @param seed
	 * @return
	 */
	public CityElement getBuildingInstance(BlockPos pos, Direction dir, long seed) {
		int len = this.length - 1;
		int wid = this.width - 1;
		int xSize =  - dir.getStepX() * len - dir.getStepZ() * wid;
		int zSize =    dir.getStepX() * wid - dir.getStepZ() * len;
		
		Rectangle bb = Rectangle.area(pos.getX(), pos.getZ(), pos.getX() + xSize, pos.getZ() + zSize);
		return new DummyBuilding(bb, pos.getY(), blockState, pos, dir);
	}
	
	public static record MarginSettings(int negveSideMinMargin, int negveSideMaxMargin, int posveSideMinMargin, int posveSideMaxMargin) {
	}
}
