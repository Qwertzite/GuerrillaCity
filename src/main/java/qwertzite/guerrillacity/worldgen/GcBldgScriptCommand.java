package qwertzite.guerrillacity.worldgen;

import com.mojang.brigadier.Command;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import qwertzite.guerrillacity.core.command.CommandArgument;
import qwertzite.guerrillacity.core.init.CommandRegister;
import qwertzite.guerrillacity.core.util.McUtil;
import qwertzite.guerrillacity.worldgen.script.ScriptWriter;

public class GcBldgScriptCommand {
	
	private static final String GROUP_NAME = "bldg";
	
	public static void registerCommands() {
		
		// ==== building exportation ====
		setTargetArea(); // set target area <pos1> <pos2>
		resetTargetArea(); // clear target area
		exclueBlock(); // exclude block <block>
		includeBlock(); // include block <block>
		chatExportSettings();
		initExportSettings();
		exportBuilding(); // export building <name>
		
		// ==== building generation ====
		
		{ // invoke <name> [seed] [pos] [dir]
			
		}
	}
	
	private static void setTargetArea() { // set target area <pos1> <pos2>
		CommandArgument<BlockPos> pos1 = CommandArgument.blockPos("pos1")
				.setDefaultValue(ctx -> new BlockPos(WorldCoordinates.current().getPosition(ctx.getSource())))
				.setDescription("One of the corner blocks of the region to be exported. Inclusive.");
		CommandArgument<BlockPos> pos2 = CommandArgument.blockPos("pos2")
				.setDefaultValue(ctx -> new BlockPos(WorldCoordinates.current().getPosition(ctx.getSource())))
				.setDescription("The corner block which opposes to the first corner. Inclusive.");
		
		CommandRegister.$(GROUP_NAME, "set_export_area", ctx -> {
			BlockPos bp1 = pos1.getValue();
			BlockPos bp2 = pos2.getValue();
			var bb = McUtil.boundingBox(bp1.getX(), bp1.getY(), bp1.getZ(), bp2.getX(), bp2.getY(), bp2.getZ());
			ScriptWriter.$().setTargetArea(bb);
			ctx.getSource().sendSuccess(Component.literal("Set export area to %s".formatted(bb)), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.addPositionalArguments(pos1, pos2)
		.setUsageString("Specify region to export.");
	}
	
	private static void resetTargetArea() {
		CommandRegister.$(GROUP_NAME, "clear_target_area", ctx -> {
			ScriptWriter.$().setTargetArea(null);
			ctx.getSource().sendSuccess(Component.literal("Reset export area".formatted()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.setUsageString("Clear region to export.");
	}
	
	private static void exclueBlock() {
		CommandArgument<BlockInput> block = CommandArgument.blockType("block")
				.setDefaultValue(ctx -> null)
				.setDescription("Block type to be ignored, state will not be considered.");
		
		CommandRegister.$(GROUP_NAME, "exclude_block", ctx -> {
			BlockInput input = block.getValue();
			Block blk = input == null ? null : input.getState().getBlock();
			if (ScriptWriter.$().excludeBlock(blk)) {
				ctx.getSource().sendSuccess(Component.literal("Set %s to ignore".formatted(blk)), true);
			} else {
				ctx.getSource().sendFailure(Component.literal("Specified %s was already ignored.".formatted(blk)));
			}
			ctx.getSource().sendSuccess(Component.literal(chatIgnoreStat()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.addPositionalArguments(block)
		.setUsageString("Add block to ignore on exportation.");
	}
	
	private static void includeBlock() {
		CommandArgument<BlockInput> block = CommandArgument.blockType("block")
				.setDefaultValue(ctx -> null)
				.setDescription("Block type to be included in exported building, state will not be considered.");
		
		CommandRegister.$(GROUP_NAME, "include_block", ctx -> {
			BlockInput input = block.getValue();
			Block blk = input == null ? null : input.getState().getBlock();
			if (ScriptWriter.$().includeBlock(blk)) {
				ctx.getSource().sendSuccess(Component.literal("Set %s to include".formatted(blk)), true);
			} else {
				ctx.getSource().sendFailure(Component.literal("Specified %s was already included.".formatted(blk)));
			}
			ctx.getSource().sendSuccess(Component.literal(chatIgnoreStat()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.addPositionalArguments(block)
		.setUsageString("Set block to be included in exported building file.");
	}
	
	private static String chatIgnoreStat() {
		if (ScriptWriter.$().getExcludedBlocks().isEmpty()) {
			return "Currently there are no block to ignore on export.";
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Following blocks will be ignored when exporting building.\n");
			for (var b : ScriptWriter.$().getExcludedBlocks()) {
				sb.append("    - %s\n".formatted(b));
			}
			return sb.toString();
		}
	}
	
	private static void chatExportSettings() {
		CommandRegister.$(GROUP_NAME, "show_export_settings", ctx -> {
			StringBuilder sb = new StringBuilder();
			sb.append(" ==== Current Export Settings ====\n");
			sb.append((ScriptWriter.$().getTargetArea() == null ?  "Export area not set" : "Target Area: " + ScriptWriter.$().getTargetArea()) + "\n");
			sb.append(chatIgnoreStat());
			ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.setUsageString("Show building export settings.");
	}
	
	private static void initExportSettings() {
		CommandRegister.$(GROUP_NAME, "init_export_settings", ctx -> {
			StringBuilder sb = new StringBuilder();
			ScriptWriter.$().initialiseSettings();
			sb.append("Reset export settings. Current settings are as follows.\n");
			sb.append((ScriptWriter.$().getTargetArea() == null ?  "Export area not set" : "Target Area: " + ScriptWriter.$().getTargetArea()) + "\n");
			sb.append(chatIgnoreStat());
			ctx.getSource().sendSuccess(Component.literal(""), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.setUsageString("Initialise export settings.");
	}
	
	public static void exportBuilding() {
//		CommandArgument<BlockInput> fileNameArg = CommandArgument.blockType("building_name")
//				.setDefaultValue(ctx -> null)
//				.setDescription("Block type to be included in exported building, state will not be considered.");
//		
//		CommandRegister.$(GROUP_NAME, "init_export_settings", ctx -> {
//			StringBuilder sb = new StringBuilder();
//			ScriptWriter.$().initialiseSettings();
//			sb.append("Reset export settings. Current settings are as follows.\n");
//			sb.append((ScriptWriter.$().getTargetArea() == null ?  "Export area not set" : "Target Area: " + ScriptWriter.$().getTargetArea()) + "\n");
//			sb.append(chatIgnoreStat());
//			ctx.getSource().sendSuccess(Component.literal(""), true);
//			return Command.SINGLE_SUCCESS;
//		}).setPermissionLevel(2)
//		.setUsageString("Initialise export settings.");
	}
	// TODO: check usage
}
