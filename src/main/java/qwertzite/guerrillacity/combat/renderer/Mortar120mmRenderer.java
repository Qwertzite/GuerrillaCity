package qwertzite.guerrillacity.combat.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.combat.entity.Mortar120mmShellEntity;
import qwertzite.guerrillacity.combat.model.Mortar120mmModel;
import qwertzite.guerrillacity.combat.model.Mortar120mmShellModel;
import qwertzite.guerrillacity.core.util.math.GcMath;


public class Mortar120mmRenderer extends EntityRenderer<Mortar120mmEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(GuerrillaCityCore.MODID, "textures/entity/mortar_120mm.png");
	private static final ResourceLocation SHELL = new ResourceLocation(GuerrillaCityCore.MODID, "textures/entity/mortar_shell_120mm_he.png");
	protected Mortar120mmModel model;
	protected Mortar120mmShellModel shell;
	
	public Mortar120mmRenderer(Context context) {
		super(context);
		this.model = new Mortar120mmModel(context.bakeLayer(Mortar120mmModel.MORTAR_MODEL));
		this.shell = new Mortar120mmShellModel(context.bakeLayer(Mortar120mmShellModel.SHELL_MODEL));
	}
	
	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void render(Mortar120mmEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffers, int light) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(-entity.getBaseYaw() / 1000.0f * GcMath.RAD2DEG));
		stack.scale(0.5f, 0.5f, 0.5f);
		
		this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		VertexConsumer vertexconsumer = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		
		float status = entity.getFiringStatus();
		if (status != 0) {
			status = Mortar120mmEntity.FIRING_INTERVAL - status + partialTicks;
			status = status * (status + 1) / 2;
			if (status * Mortar120mmEntity.GRAVITY < 36 / 16.0f) {
				float elevation = entity.getElevation();
				float barrelPos = (20.0f + 53.0f) / 16.0f - status * Mortar120mmShellEntity.GRAVITY;
				float hor = barrelPos * Mth.cos(elevation / 1000.0f);
				float ver = 4.0f/16.0f + barrelPos * Mth.sin(elevation / 1000.0f);
				stack.mulPose(Vector3f.YP.rotationDegrees(-entity.getFineYaw() / 1000.0f * GcMath.RAD2DEG));
				stack.translate(0.0d, ver, hor);
				stack.mulPose(Vector3f.XP.rotationDegrees(-elevation / 1000.0f * GcMath.RAD2DEG));
				stack.scale(16/20.0f, 16/20.0f, 16/20.0f);
				
				VertexConsumer shellVetexConsumer = buffers.getBuffer(this.shell.renderType(SHELL));
				this.shell.renderToBuffer(stack, shellVetexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		
		stack.popPose();
		
		super.render(entity, entityYaw, partialTicks, stack, buffers, light);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Mortar120mmEntity entity) {
		return TEXTURE;
	}
}
