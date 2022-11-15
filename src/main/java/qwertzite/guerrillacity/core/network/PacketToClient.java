package qwertzite.guerrillacity.core.network;

import net.minecraft.world.entity.player.Player;

public interface PacketToClient {
	public AbstractPacket handleClientSide(Player player);

}
