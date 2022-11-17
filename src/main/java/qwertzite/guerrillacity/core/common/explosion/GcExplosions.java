package qwertzite.guerrillacity.core.common.explosion;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import qwertzite.guerrillacity.core.network.GcNetwork;



public class GcExplosions {
	
//	/**
//	 * Creates torches.
//	 * Equivalent to {@link World#newExplosion(Entity, double, double, double, float, boolean, boolean)}
//	 * @param world
//	 * @param entityIn
//	 * @param x
//	 * @param y
//	 * @param z
//	 * @param strength
//	 * @param torch Number of torch. negative value means no limit.
//	 * @param isSmoking
//	 * @return
//	 */
//	public static Explosion torchExplosion(Level world, @Nullable Entity entityIn, double x, double y, double z, float strength, int torch, boolean isSmoking) {
//		boolean remote = world.isClientSide();
//		TorchExplosion explosion = new TorchExplosion(world, entityIn, x, y, z, strength, torch, isSmoking);
//		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
//		explosion.doExplosionA();
//		explosion.doExplosionB(remote);
//
//		if (!remote) {
//			if (!isSmoking) { explosion.clearAffectedBlockPositions(); }
//
//			for (EntityPlayer entityplayer : world.playerEntities) {
//				if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) {
//					((EntityPlayerMP) entityplayer).connection // explosionBが変わっていないなら，SPacketExplosionでもよい
//							.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(),
//									(Vec3d) explosion.getPlayerKnockbackMap().get(entityplayer)));
//				}
//			}
//		}
//		return explosion;
//	}
	
	/**
	 * An explosion which does not destroy blocks, damage entities, nor knock back.
	 * @param world
	 * @param entityIn
	 * @param x
	 * @param y
	 * @param z
	 * @param strength
	 * @return
	 */
	public static Explosion dummyExplosion(Level world, @Nullable Entity entityIn, double x, double y, double z, float strength) {
		boolean remote = world.isClientSide();
		DummyExplosion explosion = new DummyExplosion(world, entityIn, x, y, z, strength);
		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
		explosion.explode();
		explosion.finalizeExplosion(remote);

		if (!remote) {
			ServerLevel level = (ServerLevel) world;
			for (ServerPlayer serverplayer : level.players()) {
				if (serverplayer.distanceToSqr(x, y, z) < 4096.0D) {
					GcNetwork.sendTo(serverplayer, new PacketDummyExplosion(x, y, z, strength, explosion.getToBlow(), null));
				}
			}
		}
		return explosion;
	}
	
	public static void init() {
		GcNetwork.registerPacket(PacketDummyExplosion.class);
	}
}
