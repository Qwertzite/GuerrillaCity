package qwertzite.guerrillacity.combat.entity;

import com.mojang.datafixers.DataFixer;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import qwertzite.guerrillacity.core.util.math.GcMath;



public class Mortar120mmShellEntity extends Projectile {
	public static final float[] INITIAL_VEL = new float[] { 10.0f / 5, 16.0f / 5, 22.0f / 5, 27.0f / 5, 32.0f / 5 };

	public static final float AIR_DRAG = 0.01415f / 5; // 1/t
	public static final float GRAVITY = 9.8f / 10 / 25;// * 0.1f / (5*5); // b/t^2

	public Entity ignoreEntity;
	private float hitpoint = 5.0f;

	public Mortar120mmShellEntity(EntityType<? extends Mortar120mmShellEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	public Mortar120mmShellEntity(EntityType<? extends Mortar120mmShellEntity> pEntityType, double x, double y, double z, Level pLevel) {
		this(pEntityType, pLevel);
		this.setPos(x, y, z);
	}

	public Mortar120mmShellEntity(EntityType<? extends Mortar120mmShellEntity> pEntityType, LivingEntity pShooter, Level pLevel) {
		this(pEntityType, pShooter.getX(), pShooter.getEyeY() - (double)0.1F, pShooter.getZ(), pLevel);
		this.setOwner(pShooter);
	}

	protected void entityInit() {
	}
	
	/**
	 * Checks if the entity is in range to render.
	 */
	@Override
	public boolean shouldRenderAtSqrDistance(double pDistance) {
		double d0 = this.getBoundingBox().getSize() * 10.0D;
		if (Double.isNaN(d0)) { d0 = 1.0D; }
		d0 *= 64.0D * getViewScale();
		return pDistance < d0 * d0;
	}
	@Override protected void defineSynchedData() {}

	public void shoot(Entity shooter, float pitchMill, float yawMill, int charge) {
		if (charge < 0) {
			charge = 0;
		} else if (charge >= Mortar120mmShellEntity.INITIAL_VEL.length) {
			charge = Mortar120mmShellEntity.INITIAL_VEL.length - 1;
		}
		float f = -Mth.sin(yawMill / 1000.0f) * Mth.cos(pitchMill / 1000.0f);
		float f1 = Mth.sin(pitchMill / 1000.0f);
		float f2 = Mth.cos(yawMill / 1000.0f) * Mth.cos(pitchMill / 1000.0f);
		this.shoot((double) f, (double) f1, (double) f2, Mortar120mmShellEntity.INITIAL_VEL[charge], 60); // 60 is inaccuracy (mil)
		var shooterVel = shooter.getDeltaMovement();
		this.setDeltaMovement(this.getDeltaMovement().add(shooterVel.x, shooter.isOnGround() ? 0.0f : shooterVel.y, shooterVel.z));
		this.ignoreEntity = shooter;
	}
	
	/**
	 * {@inheritDoc} <br>
	 * Difference between super {@link #shoot(double, double, double, float, float)} is
	 * bullet spread.
	 */
	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		Vec3 vec3 = new Vec3(x, y, z).normalize().add(
				this.random.triangle(0.0d, 0.6745d * inaccuracy / 1000),
				this.random.triangle(0.0d, 0.6745d * inaccuracy / 1000),
				this.random.triangle(0.0d, 0.6745d * inaccuracy / 1000))
				.scale(velocity);
		this.setDeltaMovement(vec3);
		this.setupRotation();
	}

	@Override
	public void tick() {
		super.tick();
		Vec3 velocity = this.getDeltaMovement();
		if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
			this.setupRotation();
		}
		
		Vec3 position = this.position();
		Vec3 nextPosition = position.add(velocity);
		HitResult hitresult = this.level.clip(new ClipContext(position, nextPosition, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
		
		if (hitresult.getType() != HitResult.Type.MISS) {
			nextPosition = hitresult.getLocation();
		}
		
		EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(this.level, this, position, nextPosition, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
		if (entityhitresult != null) {
			hitresult = entityhitresult;
		}
		
		if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
			this.onHit(hitresult);
			this.hasImpulse = true;
		}
		
		this.setupRotation();
		this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
		this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
		
		if (this.ignoreEntity != null) { // ignore Entity のBBと交わらなくなったらクリア
//			if (flag) {
//				this.ignoreTime = 2;
//			} else if (this.ignoreTime-- <= 0) {
//				this.ignoreEntity = null;
//			}
		}
		
		float f1 = 1 - AIR_DRAG;
		float f2 = this.getGravityVelocity();
		if (this.isInWater()) {
			for (int j = 0; j < 4; ++j) {
				Vec3 particlePos = position.subtract(velocity.scale(0.25d));
				this.level.addParticle(ParticleTypes.BUBBLE,
						particlePos.x(), particlePos.y(), particlePos.z(),
						velocity.x, velocity.y, velocity.z);
			}
			f1 = this.getWaterInertia();
		}
		
		Vec3 nextVelocity = velocity.scale(f1); // やるなら方向をいじるようにしたほうが良い気がする
//		Vec3 nextVelocity = velocity.scale(f1).multiply(this.random.triangle(1.0, 0.1), this.random.triangle(1.0, 0.1), this.random.triangle(1.0, 0.1));
		if (!this.isNoGravity()) {
			nextVelocity = nextVelocity.subtract(0.0d, f2, 0.0d);
		}
		this.setDeltaMovement(nextVelocity);

		this.setPos(nextPosition);
		if (!this.level.isClientSide && this.getY() <= this.level.getMinBuildHeight() - 100) {
			this.discard();
		}
		this.checkInsideBlocks();
	}

	protected float getGravityVelocity() {
		return GRAVITY;
	}

	@Override
	protected void onHit(HitResult result) {
		if (result.getType() == HitResult.Type.MISS) return;
		if (!this.level.isClientSide) {
			this.remove(Entity.RemovalReason.KILLED);
			Vec3 vec = result.getLocation();
			this.getLevel().explode(this.getOwner(), vec.x, vec.y, vec.z, 5.0f, BlockInteraction.DESTROY);
		}
	}

	public static void registerFixesThrowable(DataFixer fixer, String name) {
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
	}
	
	/**
	 * Called when the entity is attacked.
	 */
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) return false;
		this.hitpoint -= amount;
		if (this.hitpoint <= 0) {
			if (!this.level.isClientSide) {
				this.remove(Entity.RemovalReason.KILLED);
				this.getLevel().explode(this, this.xo, this.yo, this.zo, 5.0f, BlockInteraction.DESTROY);
			}
			return true;
		}
		return false;
	}
	
	protected float getWaterInertia() {
		return 0.6F;
	}
	
	private void setupRotation() {
		Vec3 vec3 = this.getDeltaMovement();
		double horizontal = vec3.horizontalDistance();
		this.setYRot((float)(Math.atan2(vec3.x, vec3.z) * GcMath.RAD2DEG));
		this.setXRot((float)(Mth.atan2(vec3.y, horizontal) * GcMath.RAD2DEG));
		this.yRotO = this.getYRot();
		this.xRotO = this.getXRot();
	}
}
