package qwertzite.guerrillacity.core.network;

import net.minecraftforge.network.simple.SimpleChannel;

public class PacketDispatcher {
    private final SimpleChannel networkHandler;
    private final AbstractPacket packet;

    private PacketDispatcher(AbstractPacket packet) {
        this.networkHandler = GcNetwork.getNetworkHandler();
        this.packet = packet;
    }
    
    public static PacketDispatcher packet(AbstractPacket packet) {
        return new PacketDispatcher(packet);
    }

    public void sendToServer() {
        this.networkHandler.sendToServer(packet);
    }
}
