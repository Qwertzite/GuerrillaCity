package qwertzite.guerrillacity.core.command;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

public class CommandOption<T> {
	
	protected final String name;
	protected final String shortName;
	protected final String typeName;
	protected String description = "Not available";
	
	protected final ArgumentType<?> type;
	
	protected final BiFunction<CommandContext<CommandSourceStack>, String, T> argParser;
	protected Function<CommandContext<CommandSourceStack>, T> defaultValueProvider;
	
	protected T value;
	
	public static CommandOption<BlockPos> blockPos(String name) {
		return new CommandOption<>(name, "block pos",
				BlockPosArgument.blockPos(),
				(t, u) -> {
					try {
						return BlockPosArgument.getLoadedBlockPos(t, u);
					} catch (CommandSyntaxException e) {
						throw new RuntimeException(e);
					}
				});
	}
	
	public static CommandOption<Long> longArg(String name) {
		return new CommandOption<>(name, "long", LongArgumentType.longArg(),
				LongArgumentType::getLong);
	}
	
	public static CommandOption<String> string(String name) {
		return new CommandOption<>(name, "string", StringArgumentType.string(),
				StringArgumentType::getString);
	}
	
	public CommandOption(String name, String typeName, ArgumentType<?> type, BiFunction<CommandContext<CommandSourceStack>, String, T> argParser) {
		this(null, name, typeName, type, argParser);
	}
	
	public CommandOption(String shortName, String name, String typeName, ArgumentType<?> type, BiFunction<CommandContext<CommandSourceStack>, String, T> argParser) {
		this.name = name;
		this.shortName = shortName;
		this.typeName = typeName;
		this.type = type;
		this.argParser = argParser;
	}
	
	public CommandOption<T> setDefaultValue(Function<CommandContext<CommandSourceStack>, T> defaultValueProvider) {
		this.defaultValueProvider = defaultValueProvider;
		return this;
	}
	
	public CommandOption<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public void clear() { this.value = null; }
	public void acceptValue(CommandContext<CommandSourceStack> ctx) {
		this.value = argParser.apply(ctx, this.getName());
	}
	public void applyDefault(CommandContext<CommandSourceStack> ctx) {
		if (this.hasDefaultValue()) this.value = this.defaultValueProvider.apply(ctx);
	}
	
	public T getValue() { return this.value; }
	public boolean hasValue() { return this.value != null; }
	
	public String getLongName() { return "--" + name; }
	public boolean hasShortName() { return this.shortName != null; }
	public String getShortName() { assert(shortName != null); return "-" + shortName; }
	public String getName() { return name; }
	public ArgumentType<?> getType() { return type; }
	public boolean hasDefaultValue() { return this.defaultValueProvider != null; }
	
	public String getTypeName() { return this.typeName; }
	public String getDescription() { return this.description; }
}