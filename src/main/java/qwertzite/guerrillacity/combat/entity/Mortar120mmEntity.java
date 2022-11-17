package qwertzite.guerrillacity.combat.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import qwertzite.guerrillacity.combat.GcCombatModule;
import qwertzite.guerrillacity.core.common.explosion.GcExplosions;
import qwertzite.guerrillacity.core.util.math.GcMath;

public class Mortar120mmEntity extends Entity {
	public static final float GRAVITY = 9.8f *0.1f / (5*5);

	public static final int ELEVATION_MIN = 710;
	public static final int ELEVATION_MAX = 1450;
	public static final int ELEVATION_STP = 5;
	public static final int AZIMUTH_RANGE = 145;
	public static final int AZIMUTH_COARSE = 50;
	public static final int AZIMUTH_FINE = 5;
	public static final int AZIMUTH_MAX = 6280;
	public static final int FIRING_INTERVAL = 23;
	private static final int MAX_DAMAGE = 40;
	
	private static final EntityDataAccessor<Integer> ELEVATION = SynchedEntityData.defineId(Mortar120mmEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BASE_YAW = SynchedEntityData.defineId(Mortar120mmEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FINE_YAW = SynchedEntityData.defineId(Mortar120mmEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> FIRING_STATUS = SynchedEntityData.defineId(Mortar120mmEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(Mortar120mmEntity.class, EntityDataSerializers.FLOAT);
	
//	private ItemStack nextShell;
	private int elevMovement;
	private int yawMovement;
	private boolean coarseMovement;
	private float damage;
	
	public Mortar120mmEntity(EntityType<? extends Mortar120mmEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		this.blocksBuilding = true;
	}

	public Mortar120mmEntity(Level pLevel, double pX, double pY, double pZ) {
		this(GcCombatModule.MORTAR_120MM_ENTITY.get(), pLevel);
		this.setPos(pX, pY, pZ);
		this.xo = pX;
		this.yo = pY;
		this.zo = pZ;
	}
	
	@Override
	protected void defineSynchedData() {
		this.entityData.define(ELEVATION, ELEVATION_MAX);
		this.entityData.define(BASE_YAW, Integer.valueOf(0));
		this.entityData.define(FINE_YAW, Integer.valueOf(0));
		this.entityData.define(FIRING_STATUS, Integer.valueOf(0));
		this.entityData.define(DAMAGE, Float.valueOf(0));
	}
	
	@Override
	protected void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putInt("Elevation", this.getElevation());
		pCompound.putInt("BaseYaw", this.getBaseYaw());
		pCompound.putInt("FineYaw", this.getFineYaw());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		this.setElevation(compound.getInt("Elevation"));
		this.setBaseYaw(compound.getInt("BaseYaw"));
		this.setFineYaw(compound.getInt("FineYaw"));
		this.xRotO = this.getElevation() / 1000.0f * GcMath.RAD2DEG;
		this.yRotO = (this.getBaseYaw() + this.getFineYaw()) / 1000.0f * GcMath.RAD2DEG;
	}
	
	/** Returns {@code true} if this entity should push and be pushed by other entities when colliding. */
	@Override public boolean isPushable() { return true; }
	
	/**
	 * Returns true if other Entities should be prevented from moving through this
	 * Entity.
	 */
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	public boolean isPickable() { // cannot be interacted if this is set to false.
		return !this.isRemoved();
	}
	
	@Override
	public ItemStack getPickResult() {
		return new ItemStack(GcCombatModule.MORTAR_120MM.get());
	}
	
	/**
	 * Called when the entity is attacked.
	 */
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (this.isInvulnerableTo(pSource)) return false;
		if (!this.level.isClientSide && !this.isRemoved()) {
			this.setDamage((int) (this.getDamage() + pAmount * 10));
			this.markHurt();
			this.gameEvent(GameEvent.ENTITY_DAMAGE, pSource.getEntity());
			boolean flag = pSource.getEntity() instanceof Player && ((Player) pSource.getEntity()).getAbilities().instabuild;
			if (flag || this.getDamage() > MAX_DAMAGE) {
				if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
					this.dropDestroyedRemaining();
				}
				this.discard();
			}
			return true;
		} else {
			return true;
		}
	}
	
	protected void dropDestroyedRemaining() {
//		this.spawnAtLocation(this.getDropItem());
		// TODO: drop item
	}
	
	@Override
	protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
		if (this.isPassenger()) return;
		if (!pOnGround) return;
		
		if (this.fallDistance <= 3.0f) {
			this.resetFallDistance();
			return;
		}

		this.causeFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
		if (!this.level.isClientSide && !this.isRemoved()) {
			this.kill();
			if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
				this.dropDestroyedRemaining();
			}
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void tick() {
		this.xOld = this.xo;
		this.yOld = this.yo;
		this.zOld = this.zo;
		super.tick();
		this.updateMotion();
		this.move(MoverType.SELF, this.getDeltaMovement());
		
		int step = this.getFiringStatus();
		if (step > 0) {
			if (step == 3) {
				if (!this.getLevel().isClientSide()) {
					float elevation = this.getElevation() / 1000.0f;
					int yawMil = this.getBaseYaw() + this.getFineYaw();
					float yaw = yawMil / 1000.0f;
					float fpx = 53.0f/32.0f * Mth.cos(elevation);
					float fpy = 4.0f/32.0f + 53.0f/32.0f * Mth.sin(elevation);
					float fpz = fpx * Mth.cos(yaw);
					fpx =  -fpx * Mth.sin(yaw);
					fpx += this.xo;
					fpy += this.yo;
					fpz += this.zo;
					Mortar120mmShellEntity entity = new Mortar120mmShellEntity(GcCombatModule.MORTAR_120MM_SHELL_ENTITY.get(), fpx, fpy, fpz, this.getLevel());
					entity.shoot(this, this.getElevation(), yawMil, 0);
					this.level.addFreshEntity(entity);
					GcExplosions.dummyExplosion(this.getLevel(), this, (float) fpx, (float) fpy, (float) fpz, 1.0f);
				}
//				this.nextShell = ItemStack.EMPTY;
			}
			step--;
			this.setFiringStatus(step);
		}
		
		if (this.elevMovement != 0) {
			int elev = this.getElevation();
			if (this.elevMovement > 0) {
				elev += ELEVATION_STP;
				if (elev > ELEVATION_MAX) { elev = ELEVATION_MAX; }
			} else {
				elev -= ELEVATION_STP;
				if (elev < ELEVATION_MIN) { elev = ELEVATION_MIN; }
			}
			this.setElevation(elev);
			this.yRotO = -elev / 1000 / GcMath.RAD2DEG;
			this.elevMovement = 0;
		}
		if (this.yawMovement != 0) {
			if (this.coarseMovement) {
				int yaw = this.getBaseYaw();
				if (this.yawMovement > 0) {
					yaw += AZIMUTH_COARSE;
					if (yaw > AZIMUTH_MAX) { yaw %= AZIMUTH_MAX; }
				} else {
					yaw -= AZIMUTH_COARSE;
					if (yaw < 0) { yaw = yaw % AZIMUTH_MAX + AZIMUTH_MAX; }
				}
				this.setBaseYaw(yaw);
				this.yRotO = yaw / 1000.0f * GcMath.RAD2DEG;
			} else {
				int yaw = this.getFineYaw();
				if (this.yawMovement > 0) {
					yaw += AZIMUTH_FINE;
					if (yaw > AZIMUTH_RANGE) { yaw = AZIMUTH_RANGE; }
				} else {
					yaw -= AZIMUTH_FINE;
					if (yaw < -AZIMUTH_RANGE) { yaw = -AZIMUTH_RANGE; }
				}
				this.setFineYaw(yaw);
			}
			this.yawMovement = 0;
		}
		
		if (this.damage > 0) { this.damage -= 0.1; }
	}

	private void updateMotion() {
		Vec3 movement = this.getDeltaMovement();
		if (!this.isNoGravity()) movement.subtract(0.0d, GRAVITY, 0.0d);
		if (this.onGround) {
			movement = movement.multiply(0.1d, 1.0d, 0.1d);
		}
		this.setDeltaMovement(movement);
	}
	
	public void processInput(int elev, int yaw, boolean coarse) {
		this.elevMovement = elev;
		this.yawMovement = yaw;
		this.coarseMovement = coarse;
	}
	
	/**
	 * Applies the given player interaction to this Entity.
	 */
	@Override
	public InteractionResult interactAt(Player player, Vec3 pVec, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		Item item = stack.getItem();
		if (this.getFiringStatus() == 0 && item == GcCombatModule.MORTAR_SHELL_120MM_HE.get()) {
			if (!player.getAbilities().instabuild) { stack.shrink(1); }
			if (!this.level.isClientSide) {
				this.setFiringStatus(FIRING_INTERVAL);
			}
//			this.nextShell = stack.copy();
			player.awardStat(Stats.ITEM_USED.get(item));
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}
	
	@Override
	public Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
	
	public void setElevation(int elevation) { this.entityData.set(ELEVATION, elevation); }
	public int getElevation() { return this.entityData.get(ELEVATION); }
	public void setBaseYaw(int baseYaw) { this.entityData.set(BASE_YAW, baseYaw); }
	public int getBaseYaw() { return this.entityData.get(BASE_YAW); }
	public void setFineYaw(int fineYaw) { this.entityData.set(FINE_YAW, fineYaw); }
	public int getFineYaw() { return this.entityData.get(FINE_YAW); }
	public void setFiringStatus(int status) { this.entityData.set(FIRING_STATUS, status); }
	public int getFiringStatus() { return this.entityData.get(FIRING_STATUS); }
	public void setDamage(float pDamageTaken) { this.entityData.set(DAMAGE, pDamageTaken); }
	public float getDamage() { return this.entityData.get(DAMAGE); }
}
