package qwertzite.aviation.core.explosion;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import qwertzite.aviation.core.explosion.network.PacketDummyExplosion;
import qwertzite.aviation.core.network.AcNetwork;

public class AcExplosions {
	
	/**
	 * Creates torches.
	 * Equivalent to {@link World#newExplosion(Entity, double, double, double, float, boolean, boolean)}
	 * @param world
	 * @param entityIn
	 * @param x
	 * @param y
	 * @param z
	 * @param strength
	 * @param torch Number of torch. negative value means no limit.
	 * @param isSmoking
	 * @return
	 */
	public static Explosion torchExplosion(World world, @Nullable Entity entityIn, double x, double y, double z, float strength, int torch, boolean isSmoking) {
		boolean remote = world.isRemote;
		TorchExplosion explosion = new TorchExplosion(world, entityIn, x, y, z, strength, torch, isSmoking);
		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
		explosion.doExplosionA();
		explosion.doExplosionB(remote);

		if (!remote) {
			if (!isSmoking) { explosion.clearAffectedBlockPositions(); }

			for (EntityPlayer entityplayer : world.playerEntities) {
				if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) {
					((EntityPlayerMP) entityplayer).connection // explosionBが変わっていないなら，SPacketExplosionでもよい
							.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(),
									(Vec3d) explosion.getPlayerKnockbackMap().get(entityplayer)));
				}
			}
		}
		return explosion;
	}
	
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
	public static Explosion dummyExplosion(World world, @Nullable Entity entityIn, double x, double y, double z, float strength, boolean smoking) {
		boolean remote = world.isRemote;
		DummyExplosion explosion = new DummyExplosion(world, entityIn, x, y, z, strength, smoking);
		if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return explosion;
		explosion.doExplosionA();
		explosion.doExplosionB(remote);

		if (!remote) {
			if (!smoking) { explosion.clearAffectedBlockPositions(); }
			for (EntityPlayer entityplayer : world.playerEntities) {
				if (entityplayer.getDistanceSq(x, y, z) < 4096.0D) {
					AcNetwork.getNetworkHandler().sendTo(new PacketDummyExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(),
									(Vec3d) explosion.getPlayerKnockbackMap().get(entityplayer)), (EntityPlayerMP) entityplayer);
				}
			}
		}
		return explosion;
	}
	
	public static void onPreInit() {
		AcNetwork.registerPacket(PacketDummyExplosion.class);
	}
}
