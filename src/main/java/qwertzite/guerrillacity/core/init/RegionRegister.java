package qwertzite.guerrillacity.core.init;

import java.util.HashSet;
import java.util.Set;

import terrablender.api.Region;
import terrablender.api.Regions;

public class RegionRegister {
	
	private static final Set<Region> ENTRIES = new HashSet<>();
	
	public static Region register(Region region) {
		RegionRegister.ENTRIES.add(region);
		return region;
	}
	
	public static void enqueueToFmlCommonSetupEvent() {
		for (Region region : ENTRIES) {
			Regions.register(region);
		}
	}
	
}
