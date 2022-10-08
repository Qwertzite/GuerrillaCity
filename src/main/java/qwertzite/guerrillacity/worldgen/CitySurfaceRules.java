package qwertzite.guerrillacity.worldgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class CitySurfaceRules {
	
	private static final SurfaceRules.RuleSource AIR = SurfaceRules.state(Blocks.AIR.defaultBlockState());
	private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.COPPER_BLOCK.defaultBlockState());
	private static final SurfaceRules.ConditionSource GROUND_HEIGHT = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
	
	public static SurfaceRules.RuleSource makeRules() {
		
		return SurfaceRules.sequence(
				SurfaceRules.ifTrue(GROUND_HEIGHT, AIR), // above ground height
				STONE
		);
	}
	
}
