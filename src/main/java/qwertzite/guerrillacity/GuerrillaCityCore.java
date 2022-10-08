package qwertzite.guerrillacity;

import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import qwertzite.guerrillacity.core.BootstrapClientSide;
import qwertzite.guerrillacity.core.BootstrapCommon;
import qwertzite.guerrillacity.core.BootstrapServerSide;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.RegionRegister;
import qwertzite.guerrillacity.core.init.SurfaceRuleRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import qwertzite.guerrillacity.worldgen.GcWorldGenModule;

@Mod(GuerrillaCityCore.MODID)
public class GuerrillaCityCore {
	public static final String MODID = "guerrillacity2";
	public static final String VERSION = "2.0.0-alpha";
	public static final String LOG_BASE_NAME ="GC";
	
	public static GuerrillaCityCore INSTANCE;
	
	@SuppressWarnings("unused")
	private final BootstrapCommon bootstrap;
	
	private final List<GcModuleBase> modules = new LinkedList<>();
	
	public GuerrillaCityCore() {
		if (INSTANCE != null) throw new IllegalStateException();
		INSTANCE = this;
		
		bootstrap = DistExecutor.safeRunForDist(
				() -> BootstrapClientSide::new,
				() -> BootstrapServerSide::new);
		
		this.init();
		
	}
	
	private void init() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.register(this);
		BiomeRegister.initialise(bus);
		
		this.registerModModule(new GcWorldGenModule(bus));
		
		// ...
	}
	
	private void registerModModule(GcModuleBase module) {
		this.modules.add(module);
	}
	
	// ...
	
	@SubscribeEvent
	public void onFmlCommonRegistruyEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			RegionRegister.enqueueToFmlCommonSetupEvent();
			SurfaceRuleRegister.enqueueToFmlCommonSetupEvent();
		});
	}
	

}
