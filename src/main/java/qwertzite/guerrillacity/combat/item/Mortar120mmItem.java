package qwertzite.guerrillacity.combat.item;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import qwertzite.guerrillacity.combat.GcCombatModule;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.core.util.math.GcMath;

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
			int yawMill = Math.round(Mth.wrapDegrees(pContext.getRotation()) * GcMath.DEG2RAD * 100)*10;
			((Mortar120mmEntity) entity).setBaseYaw(yawMill);
			itemstack.shrink(1);
			level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
			level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1.5F, 0.8F);
			level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.5F, 0.4F);
		}
		return InteractionResult.CONSUME;
	}
}
