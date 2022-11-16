package qwertzite.aviation.core.explosion;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DummyExplosion extends AcExplosion {

	@SideOnly(Side.CLIENT)
	public DummyExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
		super(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
	}
	
	@SideOnly(Side.CLIENT)
	public DummyExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean smoking, List<BlockPos> affectedPositions) {
		super(worldIn, entityIn, x, y, z, size, false, smoking, affectedPositions);
	}

	public DummyExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean smoking) {
		super(worldIn, entityIn, x, y, z, size, false, smoking);
	}
	
	@Override
	public void attackEntities() {
		float diameter = this.getSize() * 2.0F;
		int k1 = MathHelper.floor(this.getX() - (double) diameter - 1.0D);
		int l1 = MathHelper.floor(this.getX() + (double) diameter + 1.0D);
		int i2 = MathHelper.floor(this.getY() - (double) diameter - 1.0D);
		int i1 = MathHelper.floor(this.getY() + (double) diameter + 1.0D);
		int j2 = MathHelper.floor(this.getZ() - (double) diameter - 1.0D);
		int j1 = MathHelper.floor(this.getZ() + (double) diameter + 1.0D);
		List<Entity> list = this.getWorld().getEntitiesWithinAABBExcludingEntity(this.getExploder(),
				new AxisAlignedBB((double) k1, (double) i2, (double) j2, (double) l1, (double) i1, (double) j1));
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.getWorld(), this, list, diameter);
	}
	
	@Override
	public void destroyAndDropBlocks(boolean remote) {
		if (this.getDamagesTerrain()) {
			for (BlockPos blockpos : this.getAffectedBlockPositions()) {
//				IBlockState iblockstate = this.getWorld().getBlockState(blockpos);
//				Block block = iblockstate.getBlock();

				if (remote) {
					double d0 = (double) ((float) blockpos.getX() + this.getWorld().rand.nextFloat());
					double d1 = (double) ((float) blockpos.getY() + this.getWorld().rand.nextFloat());
					double d2 = (double) ((float) blockpos.getZ() + this.getWorld().rand.nextFloat());
					double d3 = d0 - this.getX();
					double d4 = d1 - this.getY();
					double d5 = d2 - this.getZ();
					double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 = d3 / d6;
					d4 = d4 / d6;
					d5 = d5 / d6;
					double d7 = 0.5D / (d6 / (double) this.getSize() + 0.1D);
					d7 = d7 * (double) (this.getWorld().rand.nextFloat() * this.getWorld().rand.nextFloat() + 0.3F);
					d3 = d3 * d7;
					d4 = d4 * d7;
					d5 = d5 * d7;
					this.getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.getX()) / 2.0D,
							(d1 + this.getY()) / 2.0D, (d2 + this.getZ()) / 2.0D, d3, d4, d5);
					this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
				}

//				if (iblockstate.getMaterial() != Material.AIR) {
//					if (block.canDropFromExplosion(this)) {
//						block.dropBlockAsItemWithChance(this.getWorld(), blockpos, this.getWorld().getBlockState(blockpos), 1.0F / this.getSize(), 0);
//					}
//					block.onBlockExploded(this.getWorld(), blockpos, this);
//				}
			}
		}
	}
	
}
