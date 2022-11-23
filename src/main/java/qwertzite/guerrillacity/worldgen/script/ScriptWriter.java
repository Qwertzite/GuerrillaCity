package qwertzite.guerrillacity.worldgen.script;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ScriptWriter {
	
	public static final ScriptWriter INSTANCE = new ScriptWriter();
	
	public static ScriptWriter $() { return INSTANCE; }
	
	
	private BoundingBox area = null;
	private Set<Block> exclude =  new HashSet<>();
	
	private ScriptWriter() {
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
	public boolean excluedeBlock(Block block) {
		return this.exclude.add(block);
	}
	
	public Set<Block> getExcludedBlocks() {
		return this.exclude;
	}
	
	public void resetExcluededBlocks() {
		this.exclude.clear();
		this.exclude.add(Blocks.AIR);
	}
	
	
}
