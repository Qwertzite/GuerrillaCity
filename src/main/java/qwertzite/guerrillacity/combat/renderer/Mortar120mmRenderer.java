package qwertzite.guerrillacity.combat.renderer;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import qwertzite.guerrillacity.GuerrillaCityCore;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.combat.model.Mortar120mmModel;
import qwertzite.guerrillacity.core.util.math.GcMath;


public class Mortar120mmRenderer extends EntityRenderer<Mortar120mmEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(GuerrillaCityCore.MODID, "textures/entity/mortar_120mm.png");
//	private static final ResourceLocation SHELL = new ResourceLocation(GuerrillaCityCore.MODID, "textures/entity/mortar_shell_120mm.png");
	protected Mortar120mmModel model;
//	protected MortarShell120mmHEModel shell = new MortarShell120mmHEModel();
	
	public Mortar120mmRenderer(Context context) {
		super(context);
		this.model = new Mortar120mmModel(context.bakeLayer(Mortar120mmModel.MORTAR_MODEL));
	}
	
	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void render(Mortar120mmEntity entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource buffers, int light) {
		stack.pushPose();
		stack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entity.getBaseYaw() / 1000.0f * GcMath.RAD2DEG));
		
		this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		
		VertexConsumer vertexconsumer = buffers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
		this.model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

//		float status = entity.getFiringStatus();
//		if (status != 0) {
//			status = Mortar120mmEntity.FIRING_INTERVAL - status - partialTicks;
//			status = status * (status + 1) / 2;
//			if (status * Mortar120mmEntity.GRAVITY < 36 / 32.0f)
//			{
//				float elevation = entity.getElevation();
//				float barrelPos = (20.0f + 53.0f) / 32.0f - status * Entity120mmMortarShellM933HE.GRAVITY;
//				float hor = barrelPos * Mth.cos(elevation / 1000.0f);
//				float ver = 4.0f/32.0f + barrelPos * Mth.sin(elevation / 1000.0f);
//				this.setupRotation(entity, entity.getFineYaw() / 1000.0f * GcMath.RAD2DEG, partialTicks);
//				GlStateManager.translate(0.0f, ver, hor);
//				
//				this.bindTexture(SHELL);
//				this.shell.render(entity, 0.0f, 0.0f, 0.0f, 0.0f, -elevation / 1000.0f * GcMath.RAD2DEG, 1.0f / 40);
//			}
//		}
		
		stack.popPose();
		
//		if (Minecraft.getInstance().objectMouseOver.entityHit == entity) {
//			this.computeAndRenderTrajectory(entity, entity.getElevation(), entity.getBaseYaw() + entity.getFineYaw());
//		}
		
		super.render(entity, entityYaw, partialTicks, stack, buffers, light);
	}
	
	private List<Vec3> vertexes = new LinkedList<>();
	private int lastElevation = 0;
	private int lastTraverse = 0;
//	public void computeAndRenderTrajectory(Mortar120mmEntity mortar, int elevation, int traverse) {
//		if (elevation != this.lastElevation || lastTraverse != traverse) {
//			this.vertexes.clear();
//			float elev = elevation / 1000.0f;
//			float trav = traverse / 1000.0f;
//			float fpx = 53.0f/32.0f * Mth.cos(elev);
//			float fpy = 4.0f/32.0f + 53.0f/32.0f * Mth.sin(elev);
//			float fpz = fpx * Mth.cos(trav);
//			fpx =  -fpx * Mth.sin(trav);
//			Vec3 pos = new Vec3(mortar.posX + fpx, mortar.posY + fpy, mortar.posZ + fpz);
//
//			float ex = -Mth.sin(trav) * Mth.cos(elev);
//			float ey = Mth.sin(elev);
//			float ez = Mth.cos(trav) * Mth.cos(elev);
//			ex *= Entity120mmMortarShellM933HE.INITIAL_VEL[0];
//			ey *= Entity120mmMortarShellM933HE.INITIAL_VEL[0];
//			ez *= Entity120mmMortarShellM933HE.INITIAL_VEL[0];
//			
//			float drag = 1.0f - Entity120mmMortarShellM933HE.AIR_DRAG;
//			float grav = Entity120mmMortarShellM933HE.GRAVITY;
//			
//			World world = mortar.getEntityWorld();
//			RayTraceResult trace;
//			do {
//				this.vertexes.add(pos.subtract(mortar.posX, mortar.posY, mortar.posZ));
//				Vec3 end = pos.addVector(ex, ey, ez);
//				ex *= drag;
//				ey *= drag;
//				ez *= drag;
//				ey -= grav;
//				trace = world.rayTraceBlocks(pos, end, true);
//				pos = end;
//			} while(pos.y >= 0.0f && (trace == null || trace.entityHit == mortar));
//			this.vertexes.add((trace != null ? trace.hitVec : pos).subtract(mortar.posX, mortar.posY, mortar.posZ));
//			
//			this.lastElevation = elevation;
//			this.lastTraverse = traverse;
//		}
//		GlStateManager.disableTexture2D();
//		GlStateManager.color(0.0f, 1.0f, 0.0f, 1.0f);
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder bb = tessellator.getBuffer();
//		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
//		for (Vec3d v : this.vertexes) {
//			bb.pos(v.x, v.y, v.z).endVertex();
//		}
//		tessellator.draw();
//		Vec3d last = this.vertexes.get(this.vertexes.size()-1);
////		GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
//		GlStateManager.translate(last.x, last.y, last.z);
//		bb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
//		bb.pos(0, 0, 0).endVertex();
//		bb.pos(0.5f, 1.0f, 0.0f).endVertex();
//		bb.pos(0.0f, 1.0f, 0.5f).endVertex();
//		bb.pos(-0.5f, 1.0f, 0.0f).endVertex();
//		bb.pos(0.0f, 1.0f, -0.5f).endVertex();
//		bb.pos(0.5f, 1.0f, 0.0f).endVertex();
//		tessellator.draw();
//		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//		bb.pos(-0.5f, 1.0f, 0.0f).endVertex();
//		bb.pos(0.0f, 1.0f, 0.5f).endVertex();
//		bb.pos(0.5f, 1.0f, 0.0f).endVertex();
//		bb.pos(0.0f, 1.0f, -0.5f).endVertex();
//		tessellator.draw();
//		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
//		GlStateManager.enableTexture2D();
//	}
	
	@Override
	public ResourceLocation getTextureLocation(Mortar120mmEntity entity) {
		return TEXTURE;
	}
}
