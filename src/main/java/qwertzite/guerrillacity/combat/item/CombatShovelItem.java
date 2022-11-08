package qwertzite.guerrillacity.combat.item;

import java.util.Set;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import qwertzite.guerrillacity.core.util.McUtil;

public class CombatShovelItem extends DiggerItem {
	public static final Set<ToolAction> DEFAULT_COMBAT_SHOVEL_ACTIONS = McUtil.toolActionSetOf(ToolActions.SHOVEL_DIG);
	
	private final TagKey<Block> blocks;
	
	public CombatShovelItem(Tier pTier, float pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
		super(pAttackDamageModifier, pAttackSpeedModifier, pTier, BlockTags.MINEABLE_WITH_SHOVEL, pProperties);
		this.blocks = BlockTags.MINEABLE_WITH_SHOVEL;
	}
	
	@Override
	public float getDestroySpeed(ItemStack pStack, BlockState pState) {
		return pState.is(this.blocks) ? this.speed * 0.7f : 1.0F; // slower than other tools.
	}
	
	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		return InteractionResult.PASS;
	}
	
	@Override
	public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
		return DEFAULT_COMBAT_SHOVEL_ACTIONS.contains(toolAction);
	}
}
