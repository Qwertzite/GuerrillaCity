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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.server.ServerStartingEvent;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.command.CommandOption;

public class CommandRegister {
	
	private static final Set<CommandRegister> ENTRIES = new HashSet<>();
	private static final Map<String, Map<String, CommandRegister>> GC_COMMANDS = new HashMap<>();
	
	private static void registerHelpCommand(LiteralArgumentBuilder<CommandSourceStack> root) {
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
				}).build(cache);
			}
		}
		
		LiteralArgumentBuilder<CommandSourceStack> help = Commands.literal("help");
		cache.validate(help);
		root.then(help);
	}
	
	private static void printCommandUsage(CommandContext<CommandSourceStack> ctx, CommandRegister entry) {
		String space = "    "; //" \u200B \u200B \u200B ";
		StringBuilder s = new StringBuilder();
		s.append("/gc %s %s\n".formatted(entry.moduleName, entry.name));
		s.append(space + entry.usage);
		s.append("\n");
		if (!entry.options.isEmpty()) {
			s.append("\n");
			s.append(space + "Options:");
			int nameLength = 0;
			for (var option : entry.options.values()) {
				if (nameLength < option.getLongName().length()) nameLength = option.getLongName().length();
				s.append(("\n%s[ %s ] (%s) : %s").formatted(space, option.getLongName(), option.getTypeName(), option.getDescription()));
			}
		} else s.append("\n    This command has no options.");
		
		ctx.getSource().sendSuccess(Component.literal(s.toString()), true);
	}
	
	public static void onServerStarting(ServerStartingEvent evt) {
		
		LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("gc");
		registerHelpCommand(root);
		
		var cache = new ModuleCache();
		ENTRIES.forEach(e -> e.build(cache));
		cache.validate(root);
		
		CommandDispatcher<CommandSourceStack> dispatcher = evt.getServer().getCommands().getDispatcher();
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
	private final Map<String, CommandOption<?>> options;
	private String usage = "";
	
	private CommandRegister(String moduleName, String name, ToIntFunction<CommandContext<CommandSourceStack>> command) {
		this.moduleName = moduleName;
		this.name = name;
		this.commandBody = command;
		this.options = new HashMap<>();
	}
	
	public CommandRegister setPermissionLevel(int level) {
		this.permission = level;
		return this;
	}
	
	public CommandRegister addOption(CommandOption<?> option) {
		String optionName = option.getName();
		if (options.containsKey(optionName)) ModLog.warn("Found duplicate option {} for command gc/{}/{}", optionName, name, moduleName);
		this.options.put(optionName, option);
		return this;
	}
	
	public CommandRegister setUsageString(String usage) {
		this.usage = usage;
		return this;
	}
	
	private void build(ModuleCache cache) {
		ModLog.info("Added command {}.{}.{}", "gc", this.moduleName, this.name);
		
		Command<CommandSourceStack> command = this::executeCommand;
		
		List<Tuple<LiteralArgumentBuilder<CommandSourceStack>, RequiredArgumentBuilder<CommandSourceStack, ?>>> optionNodes = new LinkedList<>();
		List<CommandOption<?>> optionList = this.options.values().stream().sorted((e1, e2) -> -e1.getName().compareToIgnoreCase(e2.getName())).toList();
		for (int i = 0; i < optionList.size(); i++) {
			CommandOption<?> option = optionList.get(i);
			
			RequiredArgumentBuilder<CommandSourceStack, ?> value = Commands.argument(option.getName(), option.getType());
			value.executes(command);
			for (var nextNode : optionNodes) {
				value.then(nextNode.getA());
			}
			
			LiteralArgumentBuilder<CommandSourceStack> flag = Commands.literal(option.getLongName());
			flag.then(value);
			
			optionNodes.add(new Tuple<>(flag, value));
		}
		
		LiteralArgumentBuilder<CommandSourceStack> cmdBase = Commands.literal(this.name); // ここで，get usage を overrideすることで何とかなるかもしれない
		if (this.permission >= 0) cmdBase.requires(ctx -> ctx.hasPermission(permission));
		cmdBase.executes(command);
		for (var nextNode : optionNodes) {
			cmdBase.then(nextNode.getA());
		}
		
		cache.getModuleCommand(moduleName).then(cmdBase);
	}
	
	private int executeCommand(CommandContext<CommandSourceStack> ctx) {
		Collection<CommandOption<?>> undesignated = new HashSet<>(this.options.values());
		for (var node : ctx.getNodes()) {
			String nodeName = node.getNode().getName();
			if (this.options.containsKey(nodeName)) {
				CommandOption<?> option = this.options.get(nodeName);
				option.acceptValue(ctx);
				undesignated.remove(option);
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
