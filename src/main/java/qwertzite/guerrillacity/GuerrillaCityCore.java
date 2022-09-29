package qwertzite.guerrillacity;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;
import qwertzite.guerrillacity.core.BootstrapClientSide;
import qwertzite.guerrillacity.core.BootstrapCommon;
import qwertzite.guerrillacity.core.BootstrapServerSide;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import qwertzite.guerrillacity.worldgen.GcWorldGenModule;

@Mod(GuerrillaCityCore.MODID)
public class GuerrillaCityCore {
	public static final String MODID = "guerrillacity2";
	public static final String VERSION = "2.0.0-alpha";
	public static final String LOG_BASE_NAME ="GC";
	
	public static GuerrillaCityCore INSTANCE;
	
	private final BootstrapCommon bootstrap;
	
	private final List<GcModuleBase> modules = new LinkedList<>();
	
	public GuerrillaCityCore() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = this;
		
		bootstrap = DistExecutor.safeRunForDist(
				() -> BootstrapClientSide::new,
				() -> BootstrapServerSide::new);
		
		this.init();
		
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}
	
	private void init() {
		this.registerModModule(new GcWorldGenModule());
		
		// ...
		
	}
	
	private void registerModModule(GcModuleBase module) {
		this.modules.add(module);
	}
	
	/**
	 * Calling registry methods of the bootstrap.
	 * @param event
	 */
	@SubscribeEvent
	public void register(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BIOMES, this::onBiomeRegistry);
		
		// ...
	}
	
	private void onBiomeRegistry(RegisterHelper<Biome> helper) {
		for (GcModuleBase module : this.modules) {
			this.bootstrap.initBiomes(helper, module);
		}
	}
	
	// ...
	
	
}
