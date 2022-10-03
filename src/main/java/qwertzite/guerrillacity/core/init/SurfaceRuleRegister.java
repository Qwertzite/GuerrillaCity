package qwertzite.guerrillacity.core.init;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import qwertzite.guerrillacity.GuerrillaCityCore;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;

public record SurfaceRuleRegister(RuleCategory ruleCategory, RuleSource ruleSource) {
	private static final Set<SurfaceRuleRegister> ENTRIES = new HashSet<>();
	
	public static RuleSource register(RuleCategory ruleCategory, RuleSource ruleSource) {
		SurfaceRuleRegister.ENTRIES.add(new SurfaceRuleRegister(ruleCategory, ruleSource));
		return ruleSource;
	}
	
	public static void enqueueToFmlCommonSetupEvent() {
		for (SurfaceRuleRegister ruleEntry : ENTRIES) {
			SurfaceRuleManager.addSurfaceRules(ruleEntry.ruleCategory(), GuerrillaCityCore.MODID, ruleEntry.ruleSource());
		}
	}
	
//	private final RuleCategory ruleCategory;
//	private final RuleSource ruleSource;
	
//	public SurfaceRuleRegister(RuleCategory ruleCategory, RuleSource ruleSource) {
//		this.ruleCategory = ruleCategory;
//		this.ruleSource = ruleSource;
//	}
}
