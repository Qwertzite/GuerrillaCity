package qwertzite.aviation.core.explosion;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * explosionBでもOK
 * @author Qwertzite
 * 
 * 2019/11/06
 */
public class TorchExplosion extends AcExplosion {
	
	private final int torchNum;
	private final boolean destroyGrass;
	
	@SideOnly(Side.CLIENT)
	public TorchExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
		this(worldIn, entityIn, x, y, z, size, 0, true, affectedPositions);
	}

	@SideOnly(Side.CLIENT)
	public TorchExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, int torch, boolean damagesTerrain, List<BlockPos> affectedPositions) {
		super(worldIn, entityIn, x, y, z, size, false, damagesTerrain, affectedPositions);
		this.torchNum = torch;
		this.destroyGrass = true;
	}

	public TorchExplosion(World worldIn, Entity entityIn, double x, double y, double z, float size, int torch, boolean damagesTerrain) {
		super(worldIn, entityIn, x, y, z, size, false, damagesTerrain);
		this.torchNum = torch;
		this.destroyGrass = true;
	}
	
	/**
	 * ダメージ ，吹き飛び量など，すべてのエンティティに掛かる力に関係する係数
	 * @return
	 */
	@Override
	public double damageBaseFactor() {
		return 0.20d;
	}
	
	@Override
	public void postDetonation() {
		List<BlockPos> affected = this.getAffectedBlockPositions();
		for (int t = 0; !affected.isEmpty() && (t < torchNum || torchNum < 0);) {
			BlockPos pos = affected.remove(this.getWorld().rand.nextInt(affected.size()));
			if (this.getWorld().getBlockState(pos).getMaterial() == Material.AIR
					&& ((BlockTorch) Blocks.TORCH).canPlaceBlockAt(this.getWorld(), pos)) {
				this.placeTorch(pos);
				t++;
			}
		}
	}
	
	public void destroyAndDropBlocks(boolean remote) {
		if (this.getDamagesTerrain()) {
			super.destroyAndDropBlocks(remote);
		} else if (this.destroyGrass) {
			for (BlockPos blockpos : this.getAffectedBlockPositions()) {
				IBlockState iblockstate = this.getWorld().getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				if (block != Blocks.TALLGRASS) { continue; }
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

				if (iblockstate.getMaterial() != Material.AIR) {
					if (block.canDropFromExplosion(this)) {
						block.dropBlockAsItemWithChance(this.getWorld(), blockpos, this.getWorld().getBlockState(blockpos), 1.0F / this.getSize(), 0);
					}
					block.onBlockExploded(this.getWorld(), blockpos, this);
				}
			}
		}
	}
	
	private void placeTorch(BlockPos pos) {
		IBlockState state = this.getStateForPlacement(this.getWorld(), pos, EnumFacing.UP);
		this.getWorld().setBlockState(pos, state);
	}
	
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing) {
		if (this.canPlaceAt(worldIn, pos, facing)) {
			return Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, facing);
		} else {
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				if (this.canPlaceAt(worldIn, pos, enumfacing)) {
					return Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, enumfacing);
				}
			}
			return Blocks.TORCH.getDefaultState();
		}
	}
	
	private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		IBlockState iblockstate = worldIn.getBlockState(blockpos);
		Block block = iblockstate.getBlock();
		BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, blockpos, facing);

		if (facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos)) {
			return true;
		} else if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
			return !Dummy.isExceptBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID;
		} else {
			return false;
		}
	}
	
	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		return state.getBlock().canPlaceTorchOnTop(state, worldIn, pos);
	}
	
	private static abstract class Dummy extends Block {

		private Dummy(Material materialIn) { super(materialIn); }
		
		public static boolean isExceptBlockForAttachWithPiston(Block attachBlock) {
			return Block.isExceptBlockForAttachWithPiston(attachBlock);
		}
	}
}
