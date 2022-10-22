package qwertzite.guerrillacity;

import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import qwertzite.guerrillacity.construction.GcConstructionModule;
import qwertzite.guerrillacity.core.BootstrapClientSide;
import qwertzite.guerrillacity.core.BootstrapCommon;
import qwertzite.guerrillacity.core.BootstrapServerSide;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.RegionRegister;
import qwertzite.guerrillacity.core.init.SurfaceRuleRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import qwertzite.guerrillacity.worldgen.GcBiomeTagsProvider;
import qwertzite.guerrillacity.worldgen.GcWorldGenModule;
import qwertzite.guerrillacity.worldgen.city.BuildingLoader;

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
		BuildingLoader.loadResources();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::onFmlCommonRegistryEvent);
		bus.register(this);
		bus.addListener(this::onResourceGeneration);
		
		BiomeRegister.initialise(bus);
		ItemRegister.initialise(bus);
		
		this.registerModModule(new GcWorldGenModule(bus));
		this.registerModModule(new GcConstructionModule());
		
		// ...
		
		MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
	}
	
	private void registerModModule(GcModuleBase module) {
		this.modules.add(module);
	}
	
	public void onResourceGeneration(GatherDataEvent event) {
		var generator = event.getGenerator();
		var existingFileHelper = event.getExistingFileHelper();
		generator.addProvider(true, new GcBiomeTagsProvider(generator, existingFileHelper));
		generator.addProvider(true, ItemRegister.getModelProvider(generator, existingFileHelper));
	}
	// ...
	
	public void onFmlCommonRegistryEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			RegionRegister.enqueueToFmlCommonSetupEvent();
			SurfaceRuleRegister.enqueueToFmlCommonSetupEvent();
		});
	}
	
	public void onServerStarting(ServerStartingEvent event) {
		for(var module : this.modules) {
			module.onServerStarting(event);
		}
	}

}
