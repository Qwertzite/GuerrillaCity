package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import qwertzite.guerrillacity.GuerrillaCityCore;

public class EntityRegister<T extends Entity> {
	
	private static final Set<EntityRegister<?>> ENTRY = new HashSet<>();
	private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registry.ENTITY_TYPE_REGISTRY, GuerrillaCityCore.MODID);
	public static void initialise(IEventBus bus) { REGISTER.register(bus); }
	
	public static ResourceKey<EntityType<?>> registryKey(String name) {
		return ResourceKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation(GuerrillaCityCore.MODID, name));
	}
	
	public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
		for (var entry : ENTRY) {
			renderer(event, entry);
		}
	}
	
	private static <T extends Entity> void renderer(EntityRenderersEvent.RegisterRenderers event, EntityRegister<T> register) {
		EntityType<? extends T> entity = register.getEntityType();
		EntityRendererProvider<T> renderer = register.getRenderer();
		event.registerEntityRenderer(entity, renderer);
	}
	
	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (var entry : ENTRY) {
			for (var layer : entry.getLayers().entrySet()) {
				event.registerLayerDefinition(layer.getKey(), layer.getValue());
			}
		}
	}
	
	// ==== ====
	
	public static <T extends Entity> EntityRegister<T> $(ResourceKey<EntityType<?>> registryKey, Supplier<? extends EntityType<T>>  entityType, EntityRendererProvider<T> renderer) {
		return new EntityRegister<>(registryKey, entityType, renderer);
	}
	
	private final ResourceKey<EntityType<?>> registryKey;
	private final Supplier<? extends EntityType<T>>  entityType;
	private final EntityRendererProvider<T> renderer;
	private final Map<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers = new HashMap<>();
	
	private RegistryObject<EntityType<T>> registryObj;
	
	public EntityRegister(ResourceKey<EntityType<?>> registryKey, Supplier<? extends EntityType<T>>  entityType, EntityRendererProvider<T> renderer) {
		this.registryKey = registryKey;
		this.entityType = entityType;
		this.renderer = renderer;
		ENTRY.add(this);
	}
	
	public EntityRegister<T> addModelLayer(ModelLayerLocation name, Supplier<LayerDefinition> layer) {
		this.modelLayers.put(name, layer);
		return this;
	}
	
	public RegistryObject<EntityType<T>> build() {
		return this.registryObj = REGISTER.register(this.registryKey.location().getPath(), entityType);
	}
	
	public ResourceKey<EntityType<?>> getRegistryKey() { return this.registryKey; }
	public RegistryObject<EntityType<T>> getRegObj() { return this.registryObj; }
	public EntityType<T> getEntityType() { return this.registryObj.get(); }
	public EntityRendererProvider<T> getRenderer() { return this.renderer; }
	public Map<ModelLayerLocation, Supplier<LayerDefinition>> getLayers() { return this.modelLayers; }
}
