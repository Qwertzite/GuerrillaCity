package qwertzite.guerrillacity.core.common.explosion;

import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;



/**
 * SPacketExplosionで大丈夫かは，
 * {@link NetHandlerPlayClient#handleExplosion(net.minecraft.network.play.server.SPacketExplosion)}を確認すること．
 * 具体的には，explosionBと player movementに関係する
 * @author Qwertzite
 * 
 * 2019/11/06
 */
public abstract class AcExplosion extends Explosion {

	@OnlyIn(Dist.CLIENT)
	public AcExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
		this(worldIn, entityIn, x, y, z, size, false, true, affectedPositions);
	}

	@OnlyIn(Dist.CLIENT)
	public AcExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, boolean causesFire, boolean damagesTerrain, List<BlockPos> affectedPositions) {
		super(worldIn, entityIn, x, y, z, size, causesFire, damagesTerrain, affectedPositions);
	}

	public AcExplosion(Level worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
		super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
	}
	
	/**
	 * Does the first part of the explosion (destroy blocks)
	 */
	@Override
	public void explode() {
		this.computeAffectedBlocks();
		this.attackEntities();
	}
	
	public void computeAffectedBlocks() {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		for (int j = 0; j < 16; ++j) {
			for (int k = 0; k < 16; ++k) {
				for (int l = 0; l < 16; ++l) {
					if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
						double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
						double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
						double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 = d0 / d3;
						d1 = d1 / d3;
						d2 = d2 / d3;
						
						double d4 = this.getX();
						double d6 = this.getY();
						double d8 = this.getZ();

						for (float f = this.getSize() * (0.7F + this.getWorld().rand.nextFloat() * 0.6F);
								f > 0.0F; f -= 0.22500001F) {
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							BlockState iblockstate = this.getWorld().getBlockState(blockpos);

							if (iblockstate.getMaterial() != Material.AIR) {
								float f2 = this.getExploder() != null
										? this.getExploder().getExplosionResistance(this, this.getWorld(), blockpos, iblockstate)
										: iblockstate.getBlock().getExplosionResistance(this.getWorld(), blockpos, (Entity) null,
												this);
								f -= (f2 + 0.3F) * 0.3F;
							}

							if (f > 0.0F && (this.getExploder() == null || this.getExploder().canExplosionDestroyBlock(this,
									this.getWorld(), blockpos, iblockstate, f))) {
								set.add(blockpos);
							}

							d4 += d0 * 0.30000001192092896D;
							d6 += d1 * 0.30000001192092896D;
							d8 += d2 * 0.30000001192092896D;
						}
					}
				}
			}
		}
		this.getAffectedBlockPositions().addAll(set);
	}
	
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
		Vec3d vec3d = new Vec3d(this.getX(), this.getY(), this.getZ());

		for (int k2 = 0; k2 < list.size(); ++k2) {
			Entity entity = list.get(k2);

			if (!entity.isImmuneToExplosions()) {
				// 無次元化した距離(0~1) dimension-less distance
				double bareDist = entity.getDistance(this.getX(), this.getY(), this.getZ()) / (double) diameter;

				if (bareDist <= 1.0D) {
					double d5 = entity.posX - this.getX();
					double d7 = entity.posY + (double) entity.getEyeHeight() - this.getY();
					double d9 = entity.posZ - this.getZ();
					double d13 = (double) MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

					if (d13 != 0.0D) {
						d5 = d5 / d13;
						d7 = d7 / d13;
						d9 = d9 / d13;
						double d14 = (double) this.getWorld().getBlockDensity(vec3d, entity.getEntityBoundingBox());
						double d10 = (1.0D - bareDist) * d14 * this.damageBaseFactor();
						entity.attackEntityFrom(DamageSource.causeExplosionDamage(this),
								(float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) diameter * this.damageBaseFactor() + 1.0D)));
						double d11 = d10;

						if (entity instanceof EntityLivingBase) {
							d11 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, d10);
						}

						entity.motionX += d5 * d11;
						entity.motionY += d7 * d11;
						entity.motionZ += d9 * d11;

						if (entity instanceof EntityPlayer) {
							EntityPlayer entityplayer = (EntityPlayer) entity;

							if (!entityplayer.isSpectator()
									&& (!entityplayer.isCreative() || !entityplayer.capabilities.isFlying)) {
								this.getPlayerKnockbackMap().put(entityplayer, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * ダメージ ，吹き飛び量など，すべてのエンティティに掛かる力に関係する係数
	 * @return
	 */
	public double damageBaseFactor() {
		return 1.0d;
	}
	
	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	@Override
	public void doExplosionB(boolean remote) {
		this.playSound();
		this.spawnParticles();
		this.destroyAndDropBlocks(remote);
		this.postDetonation();
	}
	
	public void playSound() {
		this.getWorld().playSound((EntityPlayer) null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE,
				SoundCategory.BLOCKS, 4.0F,
				(1.0F + (this.getWorld().rand.nextFloat() - this.getWorld().rand.nextFloat()) * 0.2F) * 0.7F);
	}
	
	public void spawnParticles() {
		if (this.getSize() >= 2.0F && this.getDamagesTerrain()) {
			this.getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
		} else {
			this.getWorld().spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.getX(), this.getY(), this.getZ(), 1.0D, 0.0D, 0.0D);
		}
	}
	
	public void destroyAndDropBlocks(boolean remote) {
		if (this.getDamagesTerrain()) {
			for (BlockPos blockpos : this.getAffectedBlockPositions()) {
				IBlockState iblockstate = this.getWorld().getBlockState(blockpos);
				Block block = iblockstate.getBlock();

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
	
	public void postDetonation() {
		if (this.getCausesFire()) {
			for (BlockPos blockpos1 : this.getAffectedBlockPositions()) {
				if (this.getWorld().getBlockState(blockpos1).getMaterial() == Material.AIR
						&& this.getWorld().getBlockState(blockpos1.down()).isFullBlock() && this.getRandom().nextInt(3) == 0) {
					this.getWorld().setBlockState(blockpos1, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}
	
	public float getSize() { return AcReflections.getSize(this); }
	public Level getWorld() { return AcReflections.getWorld(this); }
	public Entity getExploder() { return AcReflections.getExploder(this); }
	public double getX() { return AcReflections.getX(this); }
	public double getY() { return AcReflections.getY(this); }
	public double getZ() { return AcReflections.getZ(this); }
	public boolean getCausesFire() { return AcReflections.getCausesFire(this); }
	public Random getRandom() { return AcReflections.getRandom(this); }
	public boolean getDamagesTerrain() { return AcReflections.getDamagesTerrain(this); }
}
