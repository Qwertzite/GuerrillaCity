package qwertzite.guerrillacity.core.datagen;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import qwertzite.guerrillacity.core.init.BlockRegister;

public class GcLootTblBlockDrop extends BlockLoot {
	
	private final Set<BlockRegister> entries;
	private final Set<Block> knownBlocks = new HashSet<>();
	
	public GcLootTblBlockDrop(Set<BlockRegister> entry) {
		this.entries = entry;
	}
	
	@Override
	protected void addTables() {
		for (BlockRegister register : entries) {
			System.out.println(register.getRegistryObject().get());
			dropSelf(register.getRegistryObject().get());
		}
	}
	
	@Override
	protected void add(Block block, LootTable.Builder builder) {
		super.add(block, builder);
		knownBlocks.add(block);
	}
	
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return this.knownBlocks;
	}
}
