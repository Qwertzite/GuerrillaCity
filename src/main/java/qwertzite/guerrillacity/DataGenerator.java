package qwertzite.guerrillacity;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import qwertzite.guerrillacity.worldgen.GcBiomeTagsProvider;

public class DataGenerator {
	
	@Mod.EventBusSubscriber(modid = GuerrillaCityCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class Innter {
		@SubscribeEvent
		public static void onDataGatherEvent(GatherDataEvent dataEvent) {
			var generator = dataEvent.getGenerator();
			var existingFileHelper = dataEvent.getExistingFileHelper();
			generator.addProvider(true, new GcBiomeTagsProvider(generator, existingFileHelper));		}
	}

}
