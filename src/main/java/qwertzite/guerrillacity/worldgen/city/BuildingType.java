package qwertzite.guerrillacity.worldgen.city;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.util.McUtil;

public class BuildingType {
	private final String typeName;
	private final int width;
	private final int length;
	private final MarginSettings margin;
	private final int baseWeight;
	
	public BuildingType(String name, int width, int length, MarginSettings margin, int weight) {
		this.typeName = name;
		this.width = width;
		this.length = length;
		this.margin = margin;
		this.baseWeight = weight;
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
		Random rand = new Random(seed);
		int len = this.length - 1;
		int wid = this.width - 1;
		int xSize =  - dir.getStepX() * len - dir.getStepZ() * wid;
		int zSize =    dir.getStepX() * wid - dir.getStepZ() * len;
		
		BoundingBox bb = McUtil.boundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + xSize, pos.getY(), pos.getZ() + zSize);
		return new DummyBuilding(bb, pos.getY(), ITEM_BY_DYE.get(rand.nextInt(16)), pos, dir);
	}
	
	private static final Map<Integer, BlockState> ITEM_BY_DYE = Util.make(new HashMap<>(), (p_29841_) -> {
		p_29841_.put(DyeColor.WHITE.getId(), Blocks.WHITE_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.ORANGE.getId(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.MAGENTA.getId(), Blocks.MAGENTA_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.LIGHT_BLUE.getId(), Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.YELLOW.getId(), Blocks.YELLOW_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.LIME.getId(), Blocks.LIME_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.PINK.getId(), Blocks.PINK_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.GRAY.getId(), Blocks.GRAY_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.LIGHT_GRAY.getId(), Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.CYAN.getId(), Blocks.CYAN_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.PURPLE.getId(), Blocks.PURPLE_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.BLUE.getId(), Blocks.BLUE_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.BROWN.getId(), Blocks.BROWN_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.GREEN.getId(), Blocks.GREEN_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.RED.getId(), Blocks.RED_STAINED_GLASS.defaultBlockState());
		p_29841_.put(DyeColor.BLACK.getId(), Blocks.BLACK_STAINED_GLASS.defaultBlockState());
	});
	
	public static record MarginSettings(int negveSideMinMargin, int negveSideMaxMargin, int posveSideMinMargin, int posveSideMaxMargin) {
	}
}
