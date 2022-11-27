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
import java.util.Set;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.Minecraft;
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
		this.initialiseSettings();
	}
	
	public void initialiseSettings() {
		this.area = null;
		this.resetExcluededBlocks();
	}
	
	public void setTargetArea(BoundingBox bb) {
		this.area = bb;
	}
	public BoundingBox getTargetArea() {
		return this.area;
	}
	
	/**
	 * 
	 * @param block block type to ignore on export.
	 * @return true if the specified block was not already ignored.
	 */
	public boolean excludeBlock(Block block) {
		return this.exclude.add(block);
	}
	
	public boolean includeBlock(Block block) {
		return this.exclude.remove(block);
	}
	
	public Set<Block> getExcludedBlocks() {
		return this.exclude;
	}
	
	public void resetExcluededBlocks() {
		this.exclude.clear();
		this.exclude.add(Blocks.AIR);
	}
	
	// ==== actual exportation process ====
	
	public String exportStructure(String name) {
		
		if (this.area == null) { return "Export target area not set!"; }
		
		int separator = name.lastIndexOf("#");
		String fileName = separator < 0 ? name : name.substring(0, separator);
		String scriptName = separator < 0 ? "main" : fileName.substring(separator);
		
		@SuppressWarnings("resource")
		File outputFolder = new File(Minecraft.getInstance().gameDirectory, "buildings");
		outputFolder.mkdir();
		Path buildingFilePath = outputFolder.toPath().resolve(name + ".json");
		
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
			return sb.toString();
		}
		return null;
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
