package qwertzite.guerrillacity.worldgen.city;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import qwertzite.guerrillacity.construction.GcConstructionModule;
import qwertzite.guerrillacity.core.util.math.Rectangle;

public class RoadElement {
	
	private final BlockPos pos;
	private final Rectangle boundingBox;
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
			this.boundingBox = new Rectangle(
					pos.getX(), pos.getZ() - 3,
					4, roadWidth + 6);
		} else if (axis == Axis.Z) {
			this.boundingBox = new Rectangle(
					pos.getX() - 3 , pos.getZ(),
					roadWidth + 6, 4);
		} else {
			assert(false);
			this.boundingBox = null;
		}
		
		this.axis = axis;
		this.roadWidth = roadWidth;
	}
	
	public Rectangle getCircumBox() { return this.boundingBox; }
	
	public void generateRoadBase(CityGenContext ctx) {
		BlockState base = GcConstructionModule.WATER_BOUND_GRAVEL.get().defaultBlockState();
		BlockState cobble = Blocks.COBBLESTONE.defaultBlockState();
		BlockState dirt = Blocks.DIRT.defaultBlockState();
		
		ctx.pushMatrix();
		this.setupTranslation(ctx);
		
		int minx = -3;
		int maxx = this.roadWidth + 3;
		int minz = 0;
		int maxz = 4; // exclusive
		for (int z = minz; z < maxz; z++) {
			for (int x = minx; x < maxx; x++) {
				ctx.setBlockState(new BlockPos(x, -5, z), base);
				ctx.setBlockState(new BlockPos(x, -4, z), base);
				ctx.setBlockState(new BlockPos(x, -3, z), cobble);
			}
			ctx.setBlockState(new BlockPos(minx  , -2, z), dirt);
			ctx.setBlockState(new BlockPos(maxx-1, -2, z), dirt);
		}
		ctx.popMatrix();
	}
	
	public void generateRoadBody(CityGenContext ctx) {
		BlockState body = Blocks.SMOOTH_STONE.defaultBlockState();
		
		ctx.pushMatrix();
		this.setupTranslation(ctx);
		
		int minx = -2;
		int maxx = this.roadWidth + 2;
		int minz = 0;
		int maxz = 4; // exclusive
		for (int z = minz; z < maxz; z++) {
			for (int x = minx; x < maxx; x++) {
				ctx.setBlockState(new BlockPos(x, -2, z), body);
			}
			ctx.setBlockState(new BlockPos(minx+1, -1, z), body);
			ctx.setBlockState(new BlockPos(maxx-2, -1, z), body);
		}
		ctx.popMatrix();
	}
	
	public void generateRoadSurface(CityGenContext ctx) {
		BlockState surface = GcConstructionModule.TARMAC.get().defaultBlockState();
		
		ctx.pushMatrix();
		this.setupTranslation(ctx);
		
		int minx = 0;
		int maxx = this.roadWidth;
		int minz = 0;
		int maxz = 4; // exclusive
		for (int z = minz; z < maxz; z++) {
			for (int x = minx; x < maxx; x++) {
				ctx.setBlockState(new BlockPos(x, -1, z), surface);
			}
		}
		ctx.popMatrix();
	}
	
	private void setupTranslation(CityGenContext ctx) {
		ctx.translate(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		if (this.axis == Axis.X) {
			ctx.rotate(Rotation.COUNTERCLOCKWISE_90);
			ctx.mirror();
		}
	}
}
