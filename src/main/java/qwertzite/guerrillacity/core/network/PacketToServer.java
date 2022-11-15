package qwertzite.guerrillacity.core.network;

import net.minecraft.world.entity.player.Player;

public interface PacketToServer {
	public AbstractPacket handleServerSide(Player player);
}
