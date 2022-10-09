package qwertzite.guerrillacity.worldgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import qwertzite.guerrillacity.core.util.GcConsts;

public class CitySurfaceRules {
	
	private static final SurfaceRules.RuleSource AIR = SurfaceRules.state(Blocks.AIR.defaultBlockState());
	private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.COPPER_BLOCK.defaultBlockState());
	private static final SurfaceRules.ConditionSource SURFACE_HEIGHT = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(GcConsts.GROUND_HEIGHT), 0);
	
	public static SurfaceRules.RuleSource makeRules() {
		
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(SURFACE_HEIGHT, AIR), // above ground height
				STONE
		);
	}
	
}
