package qwertzite.aviation.core.explosion.network;

import java.util.List;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import qwertzite.aviation.core.explosion.DummyExplosion;
import qwertzite.aviation.core.network.AbstractPacket;
import qwertzite.aviation.core.network.PacketToClient;

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
			List<BlockPos> affectedBlockPositionsIn, Vec3d motion) {
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
	public void fromBytes(ByteBuf buf) {
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

	@Override
	public void toBytes(ByteBuf buf) {
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
	public IMessage handleClientSide(EntityPlayer player) {
//		PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
		DummyExplosion explosion = new DummyExplosion(player.getEntityWorld(), (Entity) null, this.posX, this.posY,
				this.posZ, this.strength, this.affectedBlockPositions);
		explosion.doExplosionB(true);
		player.motionX += (double) this.motionX;
		player.motionY += (double) this.motionY;
		player.motionZ += (double) this.motionZ;
		return null;
	}

}
