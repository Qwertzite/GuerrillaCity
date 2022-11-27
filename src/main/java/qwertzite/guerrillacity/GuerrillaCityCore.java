package qwertzite.guerrillacity;

import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import qwertzite.guerrillacity.combat.GcCombatModule;
import qwertzite.guerrillacity.construction.GcConstructionModule;
import qwertzite.guerrillacity.core.BootstrapClientSide;
import qwertzite.guerrillacity.core.BootstrapCommon;
import qwertzite.guerrillacity.core.BootstrapServerSide;
import qwertzite.guerrillacity.core.common.GcCommon;
import qwertzite.guerrillacity.core.common.GcKeyBindings;
import qwertzite.guerrillacity.core.common.explosion.GcExplosions;
import qwertzite.guerrillacity.core.datagen.GcBiomeTagsProvider;
import qwertzite.guerrillacity.core.datagen.GcBlockStateProvider;
import qwertzite.guerrillacity.core.datagen.GcBlockTagsProvider;
import qwertzite.guerrillacity.core.datagen.GcLangLocale;
import qwertzite.guerrillacity.core.datagen.GcLanguageProvider;
import qwertzite.guerrillacity.core.datagen.GcLootTableProvider;
import qwertzite.guerrillacity.core.datagen.GcRecipeProvider;
import qwertzite.guerrillacity.core.init.BiomeRegister;
import qwertzite.guerrillacity.core.init.BlockRegister;
import qwertzite.guerrillacity.core.init.CommandRegister;
import qwertzite.guerrillacity.core.init.ConfigRegister;
import qwertzite.guerrillacity.core.init.EntityRegister;
import qwertzite.guerrillacity.core.init.ItemRegister;
import qwertzite.guerrillacity.core.init.KeyBindingRegister;
import qwertzite.guerrillacity.core.init.RegionRegister;
import qwertzite.guerrillacity.core.init.SurfaceRuleRegister;
import qwertzite.guerrillacity.core.module.GcModuleBase;
import qwertzite.guerrillacity.core.network.GcNetwork;
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
		
		// ...
		
		this.init();
	}
	
	private void init() {
		ModLoadingContext.get().registerConfig(Type.COMMON, ConfigRegister.getConfig());
		GcNetwork.init();
		new GcCommon();
		GcKeyBindings.init();
		GcExplosions.init();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::onFmlCommonRegistryEvent);
		bus.register(this);
		bus.addListener(this::onResourceGeneration);
		bus.addListener((ModConfigEvent.Loading event) -> this.postInit());
		
		MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
		
		this.registerModModule(new GcWorldGenModule());
		this.registerModModule(new GcConstructionModule());
		this.registerModModule(new GcCombatModule());
		
		BiomeRegister.initialise(bus);
		ItemRegister.initialise(bus);
		BlockRegister.initialise(bus);
		EntityRegister.initialise(bus);
		CommandRegister.initialise(bus);
		
		for (var module : this.modules) module.init(bus);
		
		BuildingLoader.loadResources();
	}
	
	private void postInit() {
		for (var module : this.modules) module.postInit();
	}
	
	private void registerModModule(GcModuleBase module) {
		this.modules.add(module);
	}
	
	public void onResourceGeneration(GatherDataEvent event) {
		var generator = event.getGenerator();
		var existingFileHelper = event.getExistingFileHelper();
		generator.addProvider(true, new GcBiomeTagsProvider(generator, existingFileHelper));
		generator.addProvider(true, ItemRegister.getModelProvider(generator, existingFileHelper));
		generator.addProvider(true, new GcBlockStateProvider(generator, existingFileHelper));
		generator.addProvider(true, new GcLootTableProvider(generator));
		generator.addProvider(true, new GcRecipeProvider(generator));
		generator.addProvider(true, new GcBlockTagsProvider(generator, existingFileHelper));
		for (GcLangLocale locale : GcLangLocale.values()) {
			generator.addProvider(true, new GcLanguageProvider(generator, locale));
		}
	}
	
	public void onFmlCommonRegistryEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			RegionRegister.enqueueToFmlCommonSetupEvent();
			SurfaceRuleRegister.enqueueToFmlCommonSetupEvent();
		});
	}
	
	public void onServerStarting(RegisterCommandsEvent event) {
		CommandRegister.onRegisterCommand(event);
	}
	
	@SubscribeEvent
	public void onRendererRegisterEvent(EntityRenderersEvent.RegisterRenderers event) {
		EntityRegister.registerRenderer(event);
	}
	
	@SubscribeEvent
	public void onLayerRegisterEvent(EntityRenderersEvent.RegisterLayerDefinitions event) {
		EntityRegister.registerLayer(event);
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void registerBindings(RegisterKeyMappingsEvent event) {
		for (var register : KeyBindingRegister.ENTRY) {
			event.register(register.getMapping());
		}
	}
}
