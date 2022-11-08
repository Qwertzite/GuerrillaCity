package qwertzite.guerrillacity.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.construction.GcConstructionModule;

public class GcCreativeTab {
	
	public static final CreativeModeTab GC_CREATIVE_TAB = new CreativeModeTab(GuerrillaCityCore.MODID) {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(GcConstructionModule.GREEN_GABION.get());
		}
	};
}
