package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class RoadElement {
	
	private final BlockPos pos;
	private final BoundingBox boundingBox;
	private final Axis axis;
	private final int roadWidth;
	
	/**
	 * 
	 * @param pos いちばん小さなX, Zかつ地面のすぐ上のブロックのy座標
	 * @param axis the direction of road.
	 * @param roadWidth
	 */
	public RoadElement(BlockPos pos, Axis axis, int roadWidth) {
		this.pos = pos;
		if (axis == Axis.X) {
			this.boundingBox = new BoundingBox(
					pos.getX()    , pos.getY() - 5, pos.getZ() - 3,
					pos.getX() + 3, pos.getY() - 1, pos.getZ() + roadWidth + 2);
		} else if (axis == Axis.Z) {
			this.boundingBox = new BoundingBox(
					pos.getX() - 3            , pos.getY() - 5, pos.getZ(),
					pos.getX() + roadWidth + 2, pos.getY() - 1, pos.getZ() + 3);
		} else {
			assert(false);
			this.boundingBox = null;
		}
		
		this.axis = axis;
		this.roadWidth = roadWidth;
	}
	
	public BoundingBox getCircumBox() { return this.boundingBox; }
	
	public void generateRoadBase(CityGenContext ctx) {
		BlockState base = Blocks.GRAVEL.defaultBlockState(); // XXX: use water-bound gravel instead.
		BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
		BlockState dirt = Blocks.DIRT.defaultBlockState();
		
		int miny = this.boundingBox.minY();
		int maxx = this.boundingBox.maxX();
		int minz = this.boundingBox.minZ();
		int maxz = this.boundingBox.maxZ();
		for (int x = this.boundingBox.minX(); x <= maxx; x++) {
			for (int z = minz; z <= maxz; z++) {
				for (int y = miny; y <= miny + 1; y++) {
					ctx.setBlockState(new BlockPos(x, y, z), base);
				}
				ctx.setBlockState(new BlockPos(x, miny + 2, z), cobble);
				ctx.setBlockState(new BlockPos(x, miny + 3, z), dirt);
			}
		}
	}
	
	public void generateRoadBody(CityGenContext ctx) {
		BlockState body = Blocks.SMOOTH_STONE.defaultBlockState();
		int minx = this.boundingBox.minX() + (this.axis == Axis.Z ?  1 : 0);
		int maxx = this.boundingBox.maxX() + (this.axis == Axis.Z ? -1 : 0);
		int minz = this.boundingBox.minZ() + (this.axis == Axis.Z ?  0 : 1);
		int maxz = this.boundingBox.maxZ() + (this.axis == Axis.Z ?  0 :-1);
		int ypos = this.boundingBox.minY() + 3;
		for (int x = minx; x <= maxx; x++) {
			for (int z = minz; z <= maxz; z++) {
				ctx.setBlockState(new BlockPos(x, ypos, z), body);
			}
		}
		if (this.axis == Axis.Z) {
			int x = this.pos.getX() - 1;
			int y = this.pos.getY() - 1;
			for (int z = this.pos.getZ(); z < this.pos.getZ() + 4; z++) {
				ctx.setBlockState(new BlockPos(x, y, z), body);
			}
			x = this.pos.getX() + this.roadWidth;
			for (int z = this.pos.getZ(); z < this.pos.getZ() + 4; z++) {
				ctx.setBlockState(new BlockPos(x, y, z), body);
			}
		} else {
			int z = this.pos.getZ() - 1;
			int y = this.pos.getY() - 1;
			for (int x = this.pos.getX(); x < this.pos.getX() + 4; x++) {
				ctx.setBlockState(new BlockPos(x, y, z), body);
			}
			z = this.pos.getZ() + this.roadWidth;
			for (int x = this.pos.getX(); x < this.pos.getX() + 4; x++) {
				ctx.setBlockState(new BlockPos(x, y, z), body);
			}
		}
	}
	
	public void generateRoadSurface(CityGenContext ctx) {
		BlockState surface = Blocks.DEEPSLATE.defaultBlockState();
		int minx = this.pos.getX();
		int maxx = this.pos.getX() + (this.axis == Axis.Z ? this.roadWidth : 4);
		int minz = this.pos.getZ();
		int maxz = this.pos.getZ() + (this.axis != Axis.Z ? this.roadWidth : 4);
		int ypos = this.pos.getY() - 1;
		for (int x = minx; x < maxx; x++) {
			for (int z = minz; z < maxz; z++) {
				ctx.setBlockState(new BlockPos(x, ypos, z), surface);
			}
		}
	}

}
