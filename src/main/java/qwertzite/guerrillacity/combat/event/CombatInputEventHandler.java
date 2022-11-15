package qwertzite.guerrillacity.combat.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.core.common.GcKeyBindings;

public class CombatInputEventHandler {

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		KeyMapping kUp = GcKeyBindings.CURSOR_UP.get();
		KeyMapping kDn = GcKeyBindings.CURSOR_DOWN.get();
		KeyMapping kLf = GcKeyBindings.CURSOR_LEFT.get();
		KeyMapping kRi = GcKeyBindings.CURSOR_RIGHT.get();
		
		int elev = (kUp.isDown() ? -1 : 0) + (kDn.isDown() ? 1 : 0);
		int yaw = (kLf.isDown() ? -1 : 0) + (kRi.isDown() ? 1 : 0);
		
		if (elev != 0 || yaw != 0) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.hitResult.getType() == HitResult.Type.ENTITY) {
				Entity e = ((EntityHitResult) mc.hitResult).getEntity();
				if (e instanceof Mortar120mmEntity mortar) {
					mortar.processInput(elev, yaw, mc.options.keyShift.isDown());
					System.out.println("Handled! %d %d %s".formatted(elev, yaw, mc.options.keyShift.isDown()));
//					PacketDispatcher.packet(new PacketMortar120M120Ctrl(e.getEntityId(), elev, yaw, mc.gameSettings.keyBindSneak.isKeyDown())).sendToServer();;
					// TODO: send packet to server
				}
			}
			

		}
		
	}
	
}
