package qwertzite.guerrillacity.core.init;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.config.IConfigSpec;

/**
 * Must be registered prior to initialisation.
 * @author Qwertzite
 * @date 2022/11/12
 * @param <T>
 */
public class ConfigRegister<T> {
	
	private static Map<String, List<ConfigRegister<?>>> ENTRY = new HashMap<>();
	
	public static IConfigSpec<?> getConfig() {
		Pair<GcConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(GcConfig::new);
		return pair.getRight();
	}
	
	public static ConfigRegister<Integer> intConfig(String module, String name, int defaultValue, int min, int max, String comment) {
		return new ConfigRegister<>(module, builder -> builder.defineInRange(name, defaultValue, min, max), comment);
	}
	
	private final String comment;
	private final Function<ForgeConfigSpec.Builder, ConfigValue<T>> valueProvider;
	
	private ConfigValue<T> configValue;
	
	private ConfigRegister(String module, Function<ForgeConfigSpec.Builder, ConfigValue<T>> valueProvider, String comment) {
		this.comment = comment;
		this.valueProvider = valueProvider;
		ENTRY.computeIfAbsent(module, k -> new LinkedList<>()).add(this);
	}
	
	public T getValue() {
		return this.configValue.get();
	}
	
	private void build(ForgeConfigSpec.Builder builder) {
		this.configValue = this.valueProvider.apply(builder.comment(this.comment));
	}
	
	private static class GcConfig {
		public GcConfig(ForgeConfigSpec.Builder builder) {
			for (var module : ENTRY.entrySet()) {
				String moduleId = module.getKey();
				builder.push(moduleId);
				for (var register : module.getValue()) {
					register.build(builder);
				}
				builder.pop();
			}
		}
	}
}
