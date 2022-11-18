package qwertzite.guerrillacity.combat.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.combat.network.Mortar120mmCtrlPacket;
import qwertzite.guerrillacity.core.common.GcKeyBindings;
import qwertzite.guerrillacity.core.network.GcNetwork;

public class MortarEventHandler {

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
//					mortar.processInput(elev, yaw, mc.options.keyShift.isDown());
					GcNetwork.sendToServer(new Mortar120mmCtrlPacket(e.getId(), elev, yaw, mc.options.keyShift.isDown()));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void inidicatorEvent(RenderGuiOverlayEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.hitResult.getType() == HitResult.Type.ENTITY) {
			Entity e = ((EntityHitResult) mc.hitResult).getEntity();
			if (e instanceof Mortar120mmEntity mortar) {
				int width = mc.getWindow().getGuiScaledWidth();
				int height = mc.getWindow().getGuiScaledHeight();
				int x = width / 2;
				int y = height / 2 + 10;
				
				PoseStack pose = event.getPoseStack();
				RenderSystem.enableTexture();
				RenderSystem.setShaderColor(1.0f, 1.0f, 0.0f, 1.0f);
				
				this.drawRightAlignedString(mc, pose, "" + mortar.getElevation(), x,  y, 0xFF00FF00);
				this.drawRightAlignedString(mc, pose, "" + (mortar.getBaseYaw() + mortar.getFineYaw()), x,  y + 10, 0xFF00FF00);
				if (!mc.options.keyShift.isDown()) {
					this.drawCenteredString(mc, pose, "SNEAK TO TRAVERSE BIPOD", x, y + 65, 0xFF00FF00);
				}
				this.drawCenteredString(mc, pose, "ALT + RIGHT-CLICK TO DISMOUNT", x, y + 75, 0xFF00FF00);
				
				RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 1.0f);
				RenderSystem.disableTexture();
				RenderSystem.disableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.lineWidth(2.0F);
				RenderSystem.enableDepthTest();
				
				pose.pushPose();
				pose.translate(width / 2.0f, height / 2.0f + 50, 0.0f);
				this.renderGauge(pose, mortar.getFineYaw());
				pose.popPose();
				
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				RenderSystem.enableTexture();
				RenderSystem.disableBlend();
				RenderSystem.lineWidth(1.0F);
			}
		}
	}
	
	private final float GAUGE_WIDTH = 70.0f;
	private void renderGauge(PoseStack pose, int yaw) {
		Matrix4f mat = pose.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bb = tessellator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bb.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
		bb.vertex(mat, -GAUGE_WIDTH, -5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, -GAUGE_WIDTH,  5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, 0, -10.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, 0,  10.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat,  GAUGE_WIDTH, -5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat,  GAUGE_WIDTH,  5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, -GAUGE_WIDTH, 0.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat,  GAUGE_WIDTH, 0.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		BufferUploader.drawWithShader(bb.end());
		float x = GAUGE_WIDTH * yaw / Mortar120mmEntity.AZIMUTH_RANGE;
		bb.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
		bb.vertex(mat, x - 3.0f, 0.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, x       , 5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, x + 3.0f, 0.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, x       ,-5.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		bb.vertex(mat, x - 3.0f, 0.0f, 0.0f).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		BufferUploader.drawWithShader(bb.end());
	}
	
	private void drawRightAlignedString(Minecraft mc, PoseStack pose, String txt, int x, int y, int colour) {
		int width = mc.font.width(txt);
		mc.font.draw(pose, txt, x - width, y, colour);
	}
	private void drawCenteredString(Minecraft mc, PoseStack pose, String txt, int x, int y, int colour) {
		int width = mc.font.width(txt);
		mc.font.draw(pose, txt, x - width / 2.0f, y, colour);
	}
}
