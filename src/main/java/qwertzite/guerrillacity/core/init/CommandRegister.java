package qwertzite.guerrillacity.core.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.command.CommandArgument;
import qwertzite.guerrillacity.core.command.GcStringArgument;
import qwertzite.guerrillacity.core.command.GcStringArgumentTypeInfo;

public class CommandRegister {
	
	public static void initialise(IEventBus bus) {
		final var REGISTRY = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, GuerrillaCityCore.MODID);
		REGISTRY.register(bus);
		
		// custom argument type
		REGISTRY.register("gc_string", () -> new GcStringArgumentTypeInfo());
		ArgumentTypeInfos.registerByClass(GcStringArgument.class, new GcStringArgumentTypeInfo());
		
	}
	
	private static final Set<CommandRegister> ENTRIES = new HashSet<>();
	private static final Map<String, Map<String, CommandRegister>> GC_COMMANDS = new HashMap<>();
	
	private static void registerHelpCommand(LiteralArgumentBuilder<CommandSourceStack> root, CommandBuildContext buildContext) {
		var cache = new ModuleCache();
		
		for (var moduleIter = GC_COMMANDS.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).iterator(); moduleIter.hasNext();) {
			var module = moduleIter.next();
			var moduleName = module.getKey();
			
			for (var iter = module.getValue().entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).iterator(); iter.hasNext();) {
				var cmdEntry = iter.next();
				var cmdName = cmdEntry.getKey();
				var command = cmdEntry.getValue();
				
				new CommandRegister(moduleName, cmdName, ctx -> {
					printCommandUsage(ctx, command);
					return Command.SINGLE_SUCCESS;
				}).build(cache, buildContext);
			}
		}
		
		LiteralArgumentBuilder<CommandSourceStack> help = Commands.literal("help");
		cache.validate(help);
		root.then(help);
	}
	
	private static void printCommandUsage(CommandContext<CommandSourceStack> ctx, CommandRegister entry) {
		String space = "    "; //" \u200B \u200B \u200B ";
		StringBuilder s = new StringBuilder();
		s.append("/gc %s %s".formatted(entry.moduleName, entry.name));
		for (var arg : entry.positionalArgs) { s.append(" <%s>".formatted(arg.getName())); }
		s.append("\n");
		s.append(space + entry.usage);
		s.append("\n");
		if (!entry.positionalArgs.isEmpty()) {
			s.append("\n");
			s.append(space + "Arguments:");
			for (var arg : entry.positionalArgs) {
				s.append("\n%s< %s : %s > : %s".formatted(space, arg.getName(), arg.getTypeName(), arg.getDescription()));
			}
		}
		
		if (!entry.optionalArgs.isEmpty()) {
			s.append("\n");
			s.append(space + "Options:");
			for (var option : entry.optionalArgs.values()) {
				if (option.hasVariable()) {
					s.append(("\n%s[ %s : %s ] : %s").formatted(space, option.getLongName(), option.getTypeName(), option.getDescription()));
				} else {
					s.append(("\n%s[ %s ] : %s").formatted(space, option.getLongName(), option.getDescription()));
				}
			}
		} else s.append("\n    This command has no options.");
		s.append("\n");
		
		ctx.getSource().sendSuccess(Component.literal(s.toString()), true);
	}
	
	public static void onRegisterCommand(RegisterCommandsEvent evt) {
		CommandBuildContext cbc = evt.getBuildContext();
		
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("gc");
		registerHelpCommand(root, cbc);
		
		var cache = new ModuleCache();
		ENTRIES.forEach(e -> e.build(cache, cbc));
		cache.validate(root);
		
		CommandDispatcher<CommandSourceStack> dispatcher = evt.getDispatcher();
		dispatcher.register(root);
	}
	
	public static CommandRegister $(String moduleName, String commandName, ToIntFunction<CommandContext<CommandSourceStack>> command) {
		CommandRegister register = new CommandRegister(moduleName, commandName, command);
		ENTRIES.add(register);
		GC_COMMANDS.computeIfAbsent(moduleName, str -> new HashMap<>()).put(commandName, register);
		return register;
	}
	
	private final String moduleName;
	private final String name;
	private final ToIntFunction<CommandContext<CommandSourceStack>> commandBody;
	private int permission = -1;
	private final Map<String, CommandArgument<?>> arguments = new HashMap<>();
	private final List<CommandArgument<?>> positionalArgs = new LinkedList<>();
	private final Map<String, CommandArgument<?>> optionalArgs = new HashMap<>();
	private String usage = "";
	
	private CommandRegister(String moduleName, String name, ToIntFunction<CommandContext<CommandSourceStack>> command) {
		this.moduleName = moduleName;
		this.name = name;
		this.commandBody = command;
	}
	
	public CommandRegister setPermissionLevel(int level) {
		this.permission = level;
		return this;
	}
	
	public CommandRegister addPositionalArguments(CommandArgument<?>...options) {
		for (var option : options) {
			String optionName = option.getName();
			this.positionalArgs.add(option);
			if (arguments.containsKey(optionName)) ModLog.warn("Found duplicate argument name %s for command gc/%s/%s. Ignoring old option.", optionName, name, moduleName);
			this.arguments.put(optionName, option);
		}
		return this;
	}
	
	public CommandRegister addOption(CommandArgument<?> option) {
		String optionName = option.getName();
		this.optionalArgs.put(optionName, option);
		if (arguments.containsKey(optionName)) ModLog.warn("Found duplicate option %s for command gc/%s/%s. Old option replaced.", optionName, name, moduleName);
		if (option.hasVariable()) this.arguments.put(optionName, option);
		else this.arguments.put(option.getLongName(), option);
		return this;
	}
	
	public CommandRegister setUsageString(String usage) {
		this.usage = usage;
		return this;
	}
	
	private void build(ModuleCache cache, CommandBuildContext buildContext) {
		ModLog.debug("Added command %s.%s.%s", "gc", this.moduleName, this.name);
		
		Command<CommandSourceStack> command = this::executeCommand;
		
		List<Tuple<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>> optionNodes = new LinkedList<>();
		List<CommandArgument<?>> optionList = this.optionalArgs.values().stream().sorted((e1, e2) -> -e1.getName().compareToIgnoreCase(e2.getName())).toList();
		for (int i = 0; i < optionList.size(); i++) {
			
			CommandArgument<?> option = optionList.get(i);
			
			if (option.hasVariable()) {
				RequiredArgumentBuilder<CommandSourceStack, ?> value = Commands.argument(option.getName(), option.getType(buildContext));
				value.executes(command);
				for (var nextNode : optionNodes) {
					value.then(nextNode.getA());
				}
				LiteralArgumentBuilder<CommandSourceStack> flag = Commands.literal(option.getLongName());
				flag.then(value);
				optionNodes.add(new Tuple<>(flag, value));
			} else {
				LiteralArgumentBuilder<CommandSourceStack> flag = Commands.literal(option.getLongName());
				flag.executes(command);
				for (var nextNode : optionNodes) {
					flag.then(nextNode.getA());
				}
				optionNodes.add(new Tuple<>(flag, flag));
			}
			
		}
		
		LiteralArgumentBuilder<CommandSourceStack> cmdBase;
		if (this.positionalArgs.isEmpty()) {
			cmdBase = Commands.literal(this.name);
			cmdBase.executes(command);
			for (var nextNode : optionNodes) {
				cmdBase.then(nextNode.getA());
			}
		} else {
			CommandArgument<?> lastArg = this.positionalArgs.get(this.positionalArgs.size()-1);
			RequiredArgumentBuilder<CommandSourceStack, ?> lastValue = Commands.argument(lastArg.getName(), lastArg.getType(buildContext));
			lastValue.executes(command);
			for (var nextNode : optionNodes) {
				lastValue.then(nextNode.getA());
			}
			
			for (int i = this.positionalArgs.size() - 2; i >= 0; i--) {
				lastArg = this.positionalArgs.get(i);
				var value = Commands.argument(lastArg.getName(), lastArg.getType(buildContext));
				value.then(lastValue);
				lastValue = value;
			}
			
			cmdBase = Commands.literal(this.name); // ここで，get usage を overrideすることで何とかなるかもしれない
			cmdBase.then(lastValue);
		}
		if (this.permission >= 0) cmdBase.requires(ctx -> ctx.hasPermission(permission));
		
		cache.getModuleCommand(moduleName).then(cmdBase);
	}
	
	private int executeCommand(CommandContext<CommandSourceStack> ctx) {
		Collection<CommandArgument<?>> undesignated = new HashSet<>(this.arguments.values());
		for (var node : ctx.getNodes()) {
			for (var s : this.arguments.keySet()) System.out.println(s); // DEBUG
			System.out.println(node + " -> " + node.getNode().getName()); // DEBUG
			String nodeName = node.getNode().getName();
			if (this.arguments.containsKey(nodeName)) {
				CommandArgument<?> option = this.arguments.get(nodeName);
				option.acceptValue(ctx);
				undesignated.remove(option);
				System.out.println("option node = " + nodeName); // DEBUG
			}
		}
		for (var option : undesignated) {
			option.applyDefault(ctx);
		}
		return this.commandBody.applyAsInt(ctx);
	}
	
	private static class ModuleCache {
		
		private Map<String, LiteralArgumentBuilder<CommandSourceStack>> moduleCache = new HashMap<>();
		
		public LiteralArgumentBuilder<CommandSourceStack> getModuleCommand(String moduleName) {
			return moduleCache.computeIfAbsent(moduleName, name -> Commands.literal(name));
		}
		
		public void validate(LiteralArgumentBuilder<CommandSourceStack> root) {
			for (var module : moduleCache.values()) {
				root.then(module);
			}
		}
	}
}
