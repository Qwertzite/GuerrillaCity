package qwertzite.guerrillacity.worldgen.script;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import qwertzite.guerrillacity.core.ModLog;

public class ScriptWriter {
	
	public static final ScriptWriter INSTANCE = new ScriptWriter();
	
	public static ScriptWriter $() { return INSTANCE; }
	
	
	private BoundingBox area = null;
	private Set<Block> exclude =  new HashSet<>();
	
	private ScriptWriter() {
		this.initialiseSettings(null);
	}
	
	public void initialiseSettings(CommandContext<CommandSourceStack> ctx) {
		this.area = null;
		this.resetExcluededBlocks();
		StringBuilder sb = new StringBuilder();
		sb.append("Reset export settings. Current settings are as follows.\n");
		sb.append(chatExportSetting());
		if (ctx != null) ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
	}
	
	public void setTargetArea(CommandContext<CommandSourceStack> ctx, BoundingBox bb) {
		this.area = bb;
		ctx.getSource().sendSuccess(Component.literal("Set export area to %s".formatted(bb)), true);
	}
	
	public void resetTargetArea(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(Component.literal("Reset export area".formatted()), true);
	}
	
	public BoundingBox getTargetArea() {
		return this.area;
	}
	
	/**
	 * 
	 * @param block block type to ignore on export.
	 * @return true if the specified block was not already ignored.
	 */
	public int excludeBlock(CommandContext<CommandSourceStack> ctx, Block block) {
		var flag = this.exclude.add(block);
		if (flag) {
			ctx.getSource().sendSuccess(Component.literal("Set %s to ignore".formatted(block)), true);
			ctx.getSource().sendSuccess(Component.literal(chatIgnoreStat()), true);
			return Command.SINGLE_SUCCESS;
		} else {
			ctx.getSource().sendFailure(Component.literal("Specified %s was already ignored.".formatted(block)));
			ctx.getSource().sendFailure(Component.literal(chatIgnoreStat()));
			return 0;
		}
	}
	
	public int includeBlock(CommandContext<CommandSourceStack> ctx, Block block) {
		if (this.exclude.remove(block)) {
			ctx.getSource().sendSuccess(Component.literal("Set %s to include".formatted(block)), true);
			ctx.getSource().sendSuccess(Component.literal(chatIgnoreStat()), true);
			return Command.SINGLE_SUCCESS;
		} else {
			ctx.getSource().sendFailure(Component.literal("Specified %s was already included.".formatted(block)));
			ctx.getSource().sendFailure(Component.literal(chatIgnoreStat()));
			return 0;
		}
	}
	
	public Set<Block> getExcludedBlocks() {
		return this.exclude;
	}
	
	public void resetExcluededBlocks() {
		this.exclude.clear();
		this.exclude.add(Blocks.AIR);
	}
	
	public void chatCurrentSetting(CommandContext<CommandSourceStack> ctx) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ==== Current Export Settings ====\n");
		sb.append(chatExportSetting());
		ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
	}
	
	// ==== util ====
	
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
	
	// ==== actual exportation process ====
	
	public int exportStructure(CommandContext<CommandSourceStack> ctx, String name, boolean overwrite) {
		List<String> errorMessage = new LinkedList<>();
		if (this.area == null) { errorMessage.add("Export target area not set! Use command \"gc bldg set_export_area\" to set target area."); }
		
		int separator = name.lastIndexOf("#");
		String fileName = separator < 0 ? name : name.substring(0, separator);
		String scriptName = separator < 0 ? "main" : fileName.substring(separator);
		@SuppressWarnings("resource")
		File outputFolder = new File(Minecraft.getInstance().gameDirectory, "buildings");
		outputFolder.mkdir();
		Path buildingFilePath = outputFolder.toPath().resolve(name + ".json");
		
		// TODO: check overwrite
		
		if (errorMessage.size() > 0) {
			for (var msg : errorMessage) {
				ctx.getSource().sendFailure(Component.literal(msg));
			}
			return 0;
		}
		
		
		// TODO: use actual data
		JsonObject json = new JsonObject();
		json.addProperty("msg", "test");
		try {
			writeJsonToFile(json, buildingFilePath);
		} catch (IOException e) {
			ModLog.error("Caught an exception while exporting a building.", e);
			StringBuilder sb = new StringBuilder();
			sb.append("Caught an exception while exporting a building.\n");
			sb.append("Details: %s".formatted(e.getMessage()));
			sb.append("Read log for more details.");
			ctx.getSource().sendFailure(Component.literal(sb.toString()));
			return 0;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Exported building script \"%s\" using following settings.\n".formatted(name));
		sb.append("Target area: %s\n".formatted(ScriptWriter.$().getTargetArea()));
		sb.append("Following blocks were ignored.\n");
		for (var b : ScriptWriter.$().getExcludedBlocks()) {
			sb.append("    - %s\n".formatted(b));
		}
		ctx.getSource().sendSuccess(Component.literal(sb.toString()), true);
		return Command.SINGLE_SUCCESS;
	}

	private void writeJsonToFile(JsonElement json, Path path) throws IOException {
		ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha256(), bytearrayoutputstream);
		Writer writer = new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8);
		JsonWriter jsonwriter = new JsonWriter(writer);
//		jsonwriter.setSerializeNulls(false);
//		jsonwriter.setIndent("  ");
//		new Gson().toJson(json, jsonwriter);
		GsonHelper.writeValue(jsonwriter, json, null);
		jsonwriter.close();
		Files.createDirectories(path.getParent());
		Files.write(path, bytearrayoutputstream.toByteArray());
	}

}
