package qwertzite.guerrillacity.worldgen;

import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils.Continentalness;
import terrablender.api.ParameterUtils.Depth;
import terrablender.api.ParameterUtils.Erosion;
import terrablender.api.ParameterUtils.Humidity;
import terrablender.api.ParameterUtils.Temperature;
import terrablender.api.ParameterUtils.Weirdness;
import terrablender.api.Region;
import terrablender.api.RegionType;

public class CityRegion extends Region {

	public CityRegion(ResourceLocation name, int weight) {
		super(name, RegionType.OVERWORLD, weight);
	}
	
	@Override
	public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
		this.addBiome(mapper,
				Temperature.WARM.parameter(),
				Humidity.HUMID.parameter(), 
				Continentalness.span(Continentalness.COAST, Continentalness.INLAND), 
				Erosion.EROSION_6.parameter(), // the larger the smoother
				Weirdness.MID_SLICE_NORMAL_ASCENDING.parameter(), // VALLEY
				Depth.SURFACE.parameter(),
				0.0f, GcWorldGenModule.KEY_CITY_BIOME);
	}
	
}
