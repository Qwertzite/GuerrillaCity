package qwertzite.guerrillacity.worldgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import qwertzite.guerrillacity.core.util.GcConsts;

public class CitySurfaceRules {
	
	private static final SurfaceRules.RuleSource AIR = SurfaceRules.state(Blocks.AIR.defaultBlockState());
	private static final SurfaceRules.RuleSource GRASS = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
	private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
	private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());
	
	private static final SurfaceRules.ConditionSource SURFACE_HEIGHT = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(GcConsts.GROUND_HEIGHT), 0);
	private static final SurfaceRules.ConditionSource IS_SUPERFICIAL_LAYER = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(GcConsts.GROUND_HEIGHT -1), 0);
	
	public static SurfaceRules.RuleSource makeRules() {
		
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(SURFACE_HEIGHT, AIR), // above ground height
				SurfaceRules.ifTrue(IS_SUPERFICIAL_LAYER, GRASS), // if this is the top most layer of city biome.
				SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), DIRT),
				STONE
		);
	}
}
