package qwertzite.guerrillacity.worldgen.command;

import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import qwertzite.guerrillacity.core.ModLog;
import qwertzite.guerrillacity.worldgen.city.CityWard;
import qwertzite.guerrillacity.worldgen.city.WardPos;

public class GcGenCommand {

	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		
//		LiteralArgumentBuilder<CommandSourceStack> position = Commands.literal("-p").then(Commands.argument("pos", BlockPosArgument.blockPos()));
//		LiteralArgumentBuilder<CommandSourceStack> groundHeight = Commands.literal("-gh");
		
		LiteralArgumentBuilder<CommandSourceStack> cmdGenWard = Commands.literal("ward")
				.requires(ctx -> ctx.hasPermission(2));
		cmdGenWard.executes(ctx -> {
			Vec3 vec3 = WorldCoordinates.current().getPosition(ctx.getSource());
			generate(new BlockPos(vec3), ctx.getSource());
			return Command.SINGLE_SUCCESS;
		});
//		cmdGenWard.then(
//				Commands.literal("-p")
//				.then(
//						Commands.argument("pos", BlockPosArgument.blockPos())
//						.executes(ctx -> {
//							System.out.println(String.format("**** exec command **** pos=%s", BlockPosArgument.getLoadedBlockPos(ctx, "pos")));
//							return Command.SINGLE_SUCCESS;
//						})));
		
		LiteralArgumentBuilder<CommandSourceStack> cmdGen = Commands.literal("gen").then(cmdGenWard);
		LiteralArgumentBuilder<CommandSourceStack> cmdRoot = Commands.literal("gc").then(cmdGen);
		dispatcher.register(cmdRoot);
	}
	
	private static void generate(BlockPos pos, CommandSourceStack source) {
		System.out.println("generating city ward...");
		try {
			WardPos wardPos = WardPos.contains(pos);
			CityWard ward = new CityWard(wardPos.getBaseBlockPos(pos.getY()), 0);
			BoundingBox boundingBox = wardPos.getWardBoundingBox(source.getLevel());
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

//		var literalcommandnode = Commands
//				.literal("teleport")
//				.requires((p_139039_) -> {return p_139039_.hasPermission(2);})
//				.then(
//						Commands.argument("location", Vec3Argument.vec3())
//						.executes((p_139051_) -> {
//							return teleportToPos(
//									p_139051_.getSource(),
//									Collections.singleton(p_139051_.getSource().getEntityOrException()),
//									p_139051_.getSource().getLevel(),Vec3Argument.getCoordinates(p_139051_, "location"),
//									WorldCoordinates.current(), (TeleportCommand.LookAt) null);}))
//				.then(
//						Commands.argument("destination", EntityArgument.entity())
//						.executes((p_139049_) -> {
//							return teleportToEntity(
//									p_139049_.getSource(),
//									Collections.singleton(p_139049_.getSource().getEntityOrException()),
//									EntityArgument.getEntity(p_139049_, "destination"));}))
//				.then(
//						Commands.argument("targets", EntityArgument.entities())
//						.then(
//								Commands.argument("location", Vec3Argument.vec3())
//								.executes((p_139047_) -> {
//									return teleportToPos(
//											p_139047_.getSource(),
//											EntityArgument.getEntities(p_139047_, "targets"),
//											p_139047_.getSource().getLevel(),
//											Vec3Argument.getCoordinates(p_139047_, "location"),
//											(Coordinates) null,
//											(TeleportCommand.LookAt) null);})
//								.then(
//										Commands.argument("rotation", RotationArgument.rotation())
//										.executes((p_139045_) -> {
//											return teleportToPos(
//													p_139045_.getSource(),
//													EntityArgument.getEntities(p_139045_, "targets"),
//													p_139045_.getSource().getLevel(),
//													Vec3Argument.getCoordinates(p_139045_, "location"),
//													RotationArgument.getRotation(p_139045_, "rotation"),
//													(TeleportCommand.LookAt) null);}))
//								.then(
//										Commands.literal("facing")
//										.then(
//												Commands.literal("entity")
//												.then(
//														Commands.argument("facingEntity", EntityArgument.entity())
//														.executes((p_139043_) -> {
//															return teleportToPos(
//																	p_139043_.getSource(),
//																	EntityArgument.getEntities(p_139043_, "targets"),
//																	p_139043_.getSource().getLevel(),
//																	Vec3Argument.getCoordinates(p_139043_, "location"),
//																	(Coordinates) null,
//																	new TeleportCommand.LookAt(EntityArgument.getEntity(p_139043_, "facingEntity"), EntityAnchorArgument.Anchor.FEET));})
//														.then(
//																Commands.argument("facingAnchor", EntityAnchorArgument.anchor()).executes((p_139041_) -> {
//					return teleportToPos(p_139041_.getSource(), EntityArgument.getEntities(p_139041_, "targets"), p_139041_.getSource().getLevel(),
//							Vec3Argument.getCoordinates(p_139041_, "location"), (Coordinates) null, new TeleportCommand.LookAt(
//									EntityArgument.getEntity(p_139041_, "facingEntity"), EntityAnchorArgument.getAnchor(p_139041_, "facingAnchor")));
//				})))).then(Commands.argument("facingLocation", Vec3Argument.vec3()).executes((p_139037_) -> {
//					return teleportToPos(p_139037_.getSource(), EntityArgument.getEntities(p_139037_, "targets"), p_139037_.getSource().getLevel(),
//							Vec3Argument.getCoordinates(p_139037_, "location"), (Coordinates) null,
//							new TeleportCommand.LookAt(Vec3Argument.getVec3(p_139037_, "facingLocation")));
//				})))).then(Commands.argument("destination", EntityArgument.entity()).executes((p_139011_) -> {
//					return teleportToEntity(p_139011_.getSource(), EntityArgument.getEntities(p_139011_, "targets"),
//							EntityArgument.getEntity(p_139011_, "destination"));
//				}))));
//		p_139009_.register(Commands.literal("tp").requires((p_139013_) -> {
//			return p_139013_.hasPermission(2);
//		}).redirect(literalcommandnode));
	   
		
//		var v = dispatcher.register(
//				Commands.literal("time").requires((p_139076_) -> { return p_139076_.hasPermission(2); })
//				.then(Commands.literal("set")
//						.then(Commands.literal("day").executes((ctx) -> { return setTime(ctx.getSource(), 1000); }))
//						.then(Commands.literal("noon").executes((ctx) -> { return setTime(ctx.getSource(), 6000); }))
//						.then(Commands.literal("night").executes((ctx) -> { return setTime(ctx.getSource(), 13000); }))
//						.then(Commands.literal("midnight").executes((ctx) -> { return setTime(ctx.getSource(), 18000); }))
//						.then(Commands.argument("time", TimeArgument.time()).executes((ctx) -> { return setTime(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "time")); })))
//				.then(Commands.literal("add")
//						.then(Commands.argument("time", TimeArgument.time()).executes((p_139091_) -> addTime(p_139091_.getSource(), IntegerArgumentType.getInteger(p_139091_, "time")) )))
//				.then(Commands.literal("query").then(Commands.literal("daytime").executes((p_139086_) -> { return queryTime(p_139086_.getSource(), getDayTime(p_139086_.getSource().getLevel())); }))
//						.then(Commands.literal("gametime").executes((p_139081_) -> { return queryTime(p_139081_.getSource(), (int) (p_139081_.getSource().getLevel().getGameTime() % 2147483647L)); }))
//						.then(Commands.literal("day").executes((p_139074_) -> { return queryTime(p_139074_.getSource(), (int) (p_139074_.getSource().getLevel().getDayTime() / 24000L % 2147483647L)); }))));

}
