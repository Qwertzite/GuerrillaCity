package qwertzite.guerrillacity.combat.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.core.network.AbstractPacket;
import qwertzite.guerrillacity.core.network.PacketToServer;

public class Mortar120mmCtrlPacket extends AbstractPacket implements PacketToServer {
	
	private int entityId;
	private int elev;
	private int yaw;
	private boolean yawCoarse;
	
	public Mortar120mmCtrlPacket() {}
	
	public Mortar120mmCtrlPacket(int entityId, int elev, int yaw, boolean yawCoarse) {
		this.entityId = entityId;
		this.elev = elev;
		this.yaw = yaw;
		this.yawCoarse = yawCoarse;
	}
	
	@Override
	public void decode(FriendlyByteBuf buf) {
		this.entityId = buf.readInt();
		this.elev = buf.readByte();
		this.yaw = buf.readByte();
		this.yawCoarse = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.entityId);
		buf.writeByte(this.elev);
		buf.writeByte(this.yaw);
		buf.writeBoolean(this.yawCoarse);
	}

	@Override
	public AbstractPacket handleServerSide(Player player, Context ctx) {
		Entity e = player.getLevel().getEntity(this.entityId);
		if (e instanceof Mortar120mmEntity mortar) {
			mortar.processInput(this.elev, this.yaw, this.yawCoarse);
		}
		return null;
	}

}
