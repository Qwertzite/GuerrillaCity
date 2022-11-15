package qwertzite.guerrillacity.core.module;

import net.minecraftforge.eventbus.api.IEventBus;

public abstract class GcModuleBase {
	
	public GcModuleBase() {}
	
	public abstract void init(IEventBus bus);
	public void postInit() {}
}
