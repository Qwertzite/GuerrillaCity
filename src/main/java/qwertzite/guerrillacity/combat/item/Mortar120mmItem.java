package qwertzite.guerrillacity.combat.item;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import qwertzite.guerrillacity.combat.GcCombatModule;

public class Mortar120mmItem extends Item {
	
	public Mortar120mmItem(Item.Properties property) {
		super(property);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		Level level = pContext.getLevel();
		if (!(level instanceof ServerLevel)) return InteractionResult.SUCCESS;
		
		ItemStack itemstack = pContext.getItemInHand();
		BlockPos blockpos = pContext.getClickedPos();
		Direction direction = pContext.getClickedFace();
		BlockState blockstate = level.getBlockState(blockpos);

		BlockPos blockpos1;
		if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
			blockpos1 = blockpos;
		} else {
			blockpos1 = blockpos.relative(direction); // If targeted block has collision, then place the entity next to the block.
		}

		Entity entity = GcCombatModule.MORTAR_120MM_ENTITY.get().spawn((ServerLevel)level, itemstack, pContext.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, false, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
		if (entity != null) {
			itemstack.shrink(1);
			level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
		}
		return InteractionResult.CONSUME;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		if (!(pLevel instanceof ServerLevel)) return InteractionResultHolder.success(itemstack);
		
		HitResult hitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
		if (hitresult.getType() != HitResult.Type.BLOCK) return InteractionResultHolder.pass(itemstack);
		
		BlockHitResult blockhitresult = (BlockHitResult)hitresult;
		BlockPos blockpos = blockhitresult.getBlockPos();
		if (!(pLevel.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) return InteractionResultHolder.pass(itemstack);
		
		if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
			Entity entity = GcCombatModule.MORTAR_120MM_ENTITY.get().spawn((ServerLevel)pLevel, itemstack, pPlayer, blockpos, MobSpawnType.SPAWN_EGG, false, false);
			if (entity == null) return InteractionResultHolder.pass(itemstack);
			if (!pPlayer.getAbilities().instabuild) { itemstack.shrink(1); }

			pPlayer.awardStat(Stats.ITEM_USED.get(this));
			pLevel.gameEvent(pPlayer, GameEvent.ENTITY_PLACE, entity.position());
			return InteractionResultHolder.consume(itemstack);
		}
		return InteractionResultHolder.fail(itemstack);
	}
	
//	/**
//	 * Called when the equipped item is right clicked.
//	 */
//	@Override
//	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
//		ItemStack itemstack = playerIn.getHeldItem(handIn);
//		if (worldIn.isRemote) {
//			return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
//		} else {
//			RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
//
//			if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
//				BlockPos blockpos = raytraceresult.getBlockPos();
//
//				if ((worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)) {
//					return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
//				} else if (worldIn.isBlockModifiable(playerIn, blockpos)
//						&& playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {
//					Entity120mmMortarM120 entity = new Entity120mmMortarM120(worldIn);
//
//					entity.setLocationAndAngles((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 1.5D,
//							(double) blockpos.getZ() + 0.5D, playerIn.rotationYaw,
//							0.0F);
//					int yawMil = (int) (playerIn.rotationYaw * AcMathHelper.DEG2RAD * 1000) % Entity120mmMortarM120.AZIMUTH_MAX;
//					yawMil -= yawMil % 5;
//					if (yawMil < 0) { yawMil += Entity120mmMortarM120.AZIMUTH_MAX; }
//					entity.setBaseYaw(yawMil);
//					worldIn.spawnEntity(entity);
//
////					if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {
////						entity.setCustomNameTag(itemstack.getDisplayName());
////					}
//
//					if (!playerIn.capabilities.isCreativeMode) {
//						itemstack.shrink(1);
//					}
//
//					playerIn.addStat(StatList.getObjectUseStats(this));
//					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
//				} else {
//					return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
//				}
//			} else {
//				return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
//			}
//		}
//	}
	
}
