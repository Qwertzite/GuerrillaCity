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
		showExportSettings();
		initExportSettings();
		exportComponent(); // export building <name>
		createBuildingPropertyStub();
		
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
				.setDescription("Specified block will be ignored on exporting buildgins.");
		
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
		.setUsageString("Specifies block to ignore on exportation.");
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
	
	private static void showExportSettings() {
		CommandRegister.$(GROUP_NAME, "show_export_settings", ctx -> {
			StringBuilder sb = new StringBuilder();
			sb.append(" ==== Current Export Settings ====\n");
			sb.append(chatExportSetting());
			ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.setUsageString("Show building export settings.");
	}
	
	private static void initExportSettings() {
		CommandRegister.$(GROUP_NAME, "init_export_settings", ctx -> {
			ScriptWriter.$().initialiseSettings();
			StringBuilder sb = new StringBuilder();
			sb.append("Reset export settings. Current settings are as follows.\n");
			sb.append(chatExportSetting());
			ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.setUsageString("Initialise export settings.");
	}
	
	private static void exportComponent() {
		CommandArgument<String> fileNameArg = CommandArgument.string("component_name")
				.setDefaultValue(ctx -> null)
				.setDescription("format: file_name#script_name. Substring after last \"#\" will be treated as script name. script_name can be ommitted.");
		CommandArgument<Boolean> flagOverwrite = CommandArgument.flag("overwrite")
				.setDescription("Overwrites old descriptions in specified file or script.");
		
		CommandRegister.$(GROUP_NAME, "export", ctx -> {
			String buildingName = fileNameArg.getValue();
			System.out.println("overwrite=" + flagOverwrite.getValue() + ",name=" + buildingName);
			String errMsg = ScriptWriter.$().exportStructure(buildingName);
			if (errMsg != null) {
				ctx.getSource().sendFailure(Component.literal(errMsg));
				return 0;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Exported building script \"%s\" using following settings.\n".formatted(buildingName));
			sb.append("Target area: %s\n".formatted(ScriptWriter.$().getTargetArea()));
			sb.append("Following blocks were ignored.\n");
			for (var b : ScriptWriter.$().getExcludedBlocks()) {
				sb.append("    - %s\n".formatted(b));
			}
			ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.addPositionalArguments(fileNameArg)
		.addOption(flagOverwrite)
		.setUsageString("Export building script.");
	}
	
	private static String chatExportSetting() {
		StringBuilder sb = new StringBuilder();
		sb.append((ScriptWriter.$().getTargetArea() == null ?  "Export area not set" : "Target Area: " + ScriptWriter.$().getTargetArea()) + "\n");
		sb.append(chatIgnoreStat());
		return sb.toString();
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
	
	private static void createBuildingPropertyStub() {
		CommandArgument<String> fileNameArg = CommandArgument.string("building_name")
				.setDefaultValue(ctx -> null)
				.setDescription("Building name. whole string will be used as building file name.");
		CommandArgument<Boolean> flagOverwrite = CommandArgument.flag("overwrite")
				.setDescription("Overwrites old descriptions in specified file or script.");
		
		CommandRegister.$(GROUP_NAME, "gen_building_property", ctx -> {
//			ScriptWriter.$().initialiseSettings(); TODO
			System.out.println("overwrite old desriptions. " + flagOverwrite.getValue());
			StringBuilder sb = new StringBuilder();
			sb.append("Created building properties stub. Correct building size, selection weight and padding settings.\n");
			ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2)
		.addPositionalArguments(fileNameArg)
		.addOption(flagOverwrite)
		.setUsageString("Set building property stub. Modify values as needed.");
	}
}
