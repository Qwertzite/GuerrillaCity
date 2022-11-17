package qwertzite.guerrillacity.core.common.explosion;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import qwertzite.guerrillacity.core.network.AbstractPacket;
import qwertzite.guerrillacity.core.network.PacketToClient;

public class PacketDummyExplosion extends AbstractPacket implements PacketToClient {

	private double posX;
	private double posY;
	private double posZ;
	private float strength;
	private List<BlockPos> affectedBlockPositions;
	private float motionX;
	private float motionY;
	private float motionZ;
	
	public PacketDummyExplosion() {}

	public PacketDummyExplosion(double xIn, double yIn, double zIn, float strengthIn,
			List<BlockPos> affectedBlockPositionsIn, Vec3 motion) {
		this.posX = xIn;
		this.posY = yIn;
		this.posZ = zIn;
		this.strength = strengthIn;
		this.affectedBlockPositions = Lists.newArrayList(affectedBlockPositionsIn);

		if (motion != null) {
			this.motionX = (float) motion.x;
			this.motionY = (float) motion.y;
			this.motionZ = (float) motion.z;
		}
	}

	@Override
	public AbstractPacket handleClientSide(Player player) {
		DummyExplosion explosion = new DummyExplosion(player.getLevel(), (Entity) null, this.posX, this.posY,
				this.posZ, this.strength, this.affectedBlockPositions);
		explosion.finalizeExplosion(true);
		player.setDeltaMovement(motionX, motionY, motionZ);
		return null;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeFloat((float) this.posX);
		buf.writeFloat((float) this.posY);
		buf.writeFloat((float) this.posZ);
		buf.writeFloat(this.strength);
		buf.writeInt(this.affectedBlockPositions.size());
		int i = (int) this.posX;
		int j = (int) this.posY;
		int k = (int) this.posZ;

		for (BlockPos blockpos : this.affectedBlockPositions) {
			int l = blockpos.getX() - i;
			int i1 = blockpos.getY() - j;
			int j1 = blockpos.getZ() - k;
			buf.writeByte(l);
			buf.writeByte(i1);
			buf.writeByte(j1);
		}

		buf.writeFloat(this.motionX);
		buf.writeFloat(this.motionY);
		buf.writeFloat(this.motionZ);
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		this.posX = (double) buf.readFloat();
		this.posY = (double) buf.readFloat();
		this.posZ = (double) buf.readFloat();
		this.strength = buf.readFloat();
		int i = buf.readInt();
		this.affectedBlockPositions = Lists.<BlockPos>newArrayListWithCapacity(i);
		int j = (int) this.posX;
		int k = (int) this.posY;
		int l = (int) this.posZ;

		for (int i1 = 0; i1 < i; ++i1) {
			int j1 = buf.readByte() + j;
			int k1 = buf.readByte() + k;
			int l1 = buf.readByte() + l;
			this.affectedBlockPositions.add(new BlockPos(j1, k1, l1));
		}

		this.motionX = buf.readFloat();
		this.motionY = buf.readFloat();
		this.motionZ = buf.readFloat();
	}

}
