package qwertzite.guerrillacity.core.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class AbstractPacket {
	
	public abstract void encode(FriendlyByteBuf byteBuf);
	public abstract void decode(FriendlyByteBuf byteBuf);
	
	public AbstractPacket onMessage(AbstractPacket message, Context ctx) {
		AbstractPacket reply = null;
		switch (ctx.getDirection()) {
		case LOGIN_TO_CLIENT:
		case PLAY_TO_CLIENT:
			if (message instanceof PacketToClient) {
				Player player = this.getPlayer();
				if (player == null) break;
				reply = ((PacketToClient) message).handleClientSide(player);
				if (reply != null) GcNetwork.sendToServer(reply);
			}
			break;
		case LOGIN_TO_SERVER:
		case PLAY_TO_SERVER:
			if (message instanceof PacketToServer) {
				ServerPlayer player = ctx.getSender();
				reply = ((PacketToServer) message).handleServerSide(player);
				if (reply != null) GcNetwork.sendTo(player, message);
			}
			break;
		default:
		}
		return reply;
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	private Player getPlayer() {
		return Minecraft.getInstance().player;
	}
}
