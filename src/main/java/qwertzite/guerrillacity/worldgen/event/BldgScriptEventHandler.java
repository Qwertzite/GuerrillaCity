package qwertzite.guerrillacity.worldgen.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import qwertzite.guerrillacity.worldgen.script.ScriptWriter;

public class BldgScriptEventHandler {

	@SubscribeEvent
	public void onLevelRender(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
			BoundingBox area = ScriptWriter.$().getTargetArea();
			if (area == null) return;
			int minX = area.minX();
			int maxX = area.maxX() + 1;
			int minY = area.minY();
			int maxY = area.maxY() + 1;
			int minZ = area.minZ();
			int maxZ = area.maxZ() + 1;
			
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
			
			
			Matrix4f mat = pose.last().pose();
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder bb = tessellator.getBuilder();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			
			bb.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
			bb.vertex(mat, minX, minY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, minY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, minY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, minY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, minY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			BufferUploader.drawWithShader(bb.end());
			bb.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
			bb.vertex(mat, minX, maxY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, maxY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, maxY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, maxY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, maxY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			BufferUploader.drawWithShader(bb.end());
			bb.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
			bb.vertex(mat, minX, minY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, maxY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, minY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, maxY, minZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, minY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, minX, maxY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, minY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			bb.vertex(mat, maxX, maxY, maxZ).color(0.0f, 1.0f, 1.0f, 1.0f).endVertex();
			BufferUploader.drawWithShader(bb.end());
			
			pose.popPose();
			
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.lineWidth(1.0F);
		}
	}
}
