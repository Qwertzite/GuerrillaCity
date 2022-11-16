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
import qwertzite.guerrillacity.combat.entity.Mortar120mmShellEntity;
import qwertzite.guerrillacity.combat.model.Mortar120mmShellModel;

public class Mortar120mmShellRenderer extends EntityRenderer<Mortar120mmShellEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(GuerrillaCityCore.MODID, "textures/entity/mortar_shell_120mm_he.png");
	protected Mortar120mmShellModel model;

	public Mortar120mmShellRenderer(Context context) {
		super(context);
		this.model = new Mortar120mmShellModel(context.bakeLayer(Mortar120mmShellModel.SHELL_MODEL));
	}
	
	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void render(Mortar120mmShellEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffers, int light) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
		stack.mulPose(Vector3f.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
		
		
		VertexConsumer vertexconsumer = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

		stack.popPose();
		super.render(entity, entityYaw, partialTicks, stack, buffers, light);
	}
	
	@Override
	public ResourceLocation getTextureLocation(Mortar120mmShellEntity entity) {
		return TEXTURE;
	}

}
