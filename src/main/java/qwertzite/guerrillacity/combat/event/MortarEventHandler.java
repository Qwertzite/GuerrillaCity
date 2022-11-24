package qwertzite.guerrillacity.combat.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.combat.entity.Mortar120mmShellEntity;
import qwertzite.guerrillacity.combat.network.Mortar120mmCtrlPacket;
import qwertzite.guerrillacity.core.common.GcKeyBindings;
import qwertzite.guerrillacity.core.network.GcNetwork;

public class MortarEventHandler {
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
			Entity e = ((EntityHitResult) mc.hitResult).getEntity();
			if (e instanceof Mortar120mmEntity mortar) {
				KeyMapping kUp = GcKeyBindings.CURSOR_UP.get();
				KeyMapping kDn = GcKeyBindings.CURSOR_DOWN.get();
				KeyMapping kLf = GcKeyBindings.CURSOR_LEFT.get();
				KeyMapping kRi = GcKeyBindings.CURSOR_RIGHT.get();
				
				int elev = (kUp.isDown() ? -1 : 0) + (kDn.isDown() ? 1 : 0);
				int yaw = (kLf.isDown() ? -1 : 0) + (kRi.isDown() ? 1 : 0);
				
				if (elev != 0 || yaw != 0) {
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
				this.drawCenteredString(mc, pose, "CTRL + RIGHT-CLICK TO DISASSEMBLE", x, y + 75, 0xFF00FF00);
				if (!MORTAR.containsKey(mortar)) {
					this.drawCenteredString(mc, pose, "PRESS %S TO SHOW TRAJECTORY".formatted(GcKeyBindings.BTN_VIEW.get().getKey().getName()), x, y + 85, 0xFF00FF00);
				}
				
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
	
	private Map<Mortar120mmEntity, TrajectoryData> MORTAR = new HashMap<>();
	
	public boolean onKeyClick(GcKeyBindings key) {
		Minecraft mc = Minecraft.getInstance();
		synchronized (MORTAR) {
			if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
				Entity e = ((EntityHitResult) mc.hitResult).getEntity();
				if (e instanceof Mortar120mmEntity mortar) {
					if (MORTAR.containsKey(mortar)) MORTAR.remove(mortar);
					else MORTAR.put(mortar, null);
					return true;
				}
			}
			MORTAR.clear();
		}
		return false;
	}
	
	@SubscribeEvent
	public void onLevelRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			RenderSystem.disableTexture();
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.lineWidth(2.0F);
			
			PoseStack pose = event.getPoseStack();
			pose.pushPose();
			@SuppressWarnings("resource")
			Vec3 cameraPos = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
			pose.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
			
			synchronized(MORTAR) {
				Map<Mortar120mmEntity, TrajectoryData> newData = new HashMap<>();
				for (var iter = MORTAR.keySet().iterator(); iter.hasNext();) {
					var mortar = iter.next();
					pose.pushPose();
					pose.translate(mortar.getX(), mortar.getY(), mortar.getZ());
					
					if (!mortar.isAlive() || mortar.touchingUnloadedChunk()) iter.remove();
					this.computeAndRenderTrajectory(pose, mortar, MORTAR.get(mortar), newData);
					
					pose.popPose();
				}
				MORTAR.putAll(newData);
			}
			pose.popPose();
			
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.lineWidth(1.0F);
		}
	}
	
	public void computeAndRenderTrajectory(PoseStack pose, Mortar120mmEntity mortar, TrajectoryData trajectory, Map<Mortar120mmEntity, TrajectoryData> newData) {
		int traverse = mortar.getBaseYaw() + mortar.getFineYaw();
		int elevation = mortar.getElevation();
		if (trajectory == null || elevation != trajectory.lastElevation || traverse != trajectory.lastTraverse) {
			trajectory = TrajectoryData.create(elevation, traverse);
			float elev = elevation / 1000.0f;
			float trav = traverse / 1000.0f;
			float fpx = 53.0f/32.0f * Mth.cos(elev);
			float fpy = 4.0f/32.0f + 53.0f/32.0f * Mth.sin(elev);
			float fpz = fpx * Mth.cos(trav);
			fpx =  -fpx * Mth.sin(trav);
			Vec3 pos = new Vec3(mortar.getX() + fpx, mortar.getY() + fpy, mortar.getZ() + fpz);
			
			float ex = -Mth.sin(trav) * Mth.cos(elev);
			float ey = Mth.sin(elev);
			float ez = Mth.cos(trav) * Mth.cos(elev);
			ex *= Mortar120mmShellEntity.INITIAL_VEL[0];
			ey *= Mortar120mmShellEntity.INITIAL_VEL[0];
			ez *= Mortar120mmShellEntity.INITIAL_VEL[0];
			
			float drag = 1.0f - Mortar120mmShellEntity.AIR_DRAG;
			float grav = Mortar120mmShellEntity.GRAVITY;
			
			Level world = mortar.getLevel();
			HitResult trace;
			do {
				trajectory.vertexes.add(pos.subtract(mortar.getX(), mortar.getY(), mortar.getZ()));
				Vec3 end = pos.add(ex, ey, ez);
				ex *= drag;
				ey *= drag;
				ez *= drag;
				ey -= grav;
				trace = world.clip(new ClipContext(pos, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
				pos = end;
			} while(pos.y >= 0.0f && (trace == null || trace.getType() != Type.BLOCK));
			
			trajectory.vertexes.add((trace != null ? trace.getLocation() : pos).subtract(mortar.getX(), mortar.getY(), mortar.getZ()));
			newData.put(mortar, trajectory);
		}
		
		
		Matrix4f mat = pose.last().pose();
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bb = tessellator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		
		bb.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
		for (Vec3 v : trajectory.vertexes) {
			bb.vertex(mat, (float) v.x, (float) v.y, (float) v.z).color(0.0f, 1.0f, 0.0f, 1.0f).endVertex();
		}
		BufferUploader.drawWithShader(bb.end());
		
		Vec3 last = trajectory.vertexes.get(trajectory.vertexes.size()-1);
		pose.translate(last.x, last.y, last.z);
		bb.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
		bb.vertex(mat, 0.0f, 0.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 0.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat,-1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 0.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f, 1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 0.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f,-1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat,-1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f, 1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f,-1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		BufferUploader.drawWithShader(bb.end());
		bb.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
		bb.vertex(mat, 1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f, 1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat,-1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 0.0f, 2.0f,-1.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		bb.vertex(mat, 1.0f, 2.0f, 0.0f).color(1.0f, 0.1f, 0.1f, 1.0f).endVertex();
		BufferUploader.drawWithShader(bb.end());
	}
	
	private static record TrajectoryData(List<Vec3> vertexes, int lastElevation, int lastTraverse) {
		public static  TrajectoryData create(int elevation, int traverse) {
			return new TrajectoryData(new LinkedList<>(), elevation, traverse);
		}
	}
}
