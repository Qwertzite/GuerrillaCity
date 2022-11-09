package qwertzite.guerrillacity.worldgen;

import java.util.Set;

import com.mojang.brigadier.Command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.core.command.CommandOption;
import qwertzite.guerrillacity.core.init.CommandRegister;
import qwertzite.guerrillacity.core.util.math.Rectangle;
import qwertzite.guerrillacity.worldgen.city.CityStructureProvider;
import qwertzite.guerrillacity.worldgen.city.CityWard;
import qwertzite.guerrillacity.worldgen.city.WardPos;

public class GcGenCommand {
	
	public static void registerCommand() {
		registerGenCommand();
		registerClearWardCacheCommand();
	}
	
	private static void registerGenCommand() {
		CommandOption<Long> seed = CommandOption.longArg("seed")
				.setDefaultValue(ctx -> 0L);
		CommandOption<BlockPos> pos = CommandOption.blockPos("pos")
				.setDefaultValue(ctx -> new BlockPos(WorldCoordinates.current().getPosition(ctx.getSource())));
		CommandRegister.$("gen", "ward", ctx -> {
			generate(pos.getValue(), ctx.getSource(), seed.getValue());
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2).addOption(seed).addOption(pos);
	}
	
	private static void generate(BlockPos pos, CommandSourceStack source, long seed) {
		ModLog.info("generating city ward with seed %s...", seed);
		try {
			WardPos wardPos = WardPos.contains(pos);
			CityWard ward = new CityWard(wardPos.getBaseBlockPos(pos.getY()), seed);
			Rectangle boundingBox = wardPos.getWardBoundingRectangle();
			ward.beginInitialisation(Set.of(boundingBox), Set.of());
			ModLog.info("initialised city ward");
			ServerLevel level = source.getLevel();
			
			var stateMap = ward.computeBlockStateForBoudingBox(level, boundingBox);
			ModLog.info("generated buildings");
			for (var e : stateMap.entrySet()) {
				level.setBlock(e.getKey(), e.getValue(), 3);
			}
			
			System.out.println("pos=%s, bb=%s".formatted(pos, boundingBox));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println("generated city ward at %s".formatted(pos));
	}
	
	private static void registerClearWardCacheCommand() {
		CommandRegister.$("gen", "clear_cache", ctx -> {
			CityStructureProvider.clearCache();
			return Command.SINGLE_SUCCESS;
		}).setPermissionLevel(2);
	}
}
