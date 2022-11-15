//package qwertzite.guerrillacity.combat.model;
//
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.client.model.ModelRenderer;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.entity.Entity;
//import qwertzite.aviation.core.util.AcMathHelper;
//
//public class MortarShell120mmHEModel extends ModelBase {
//	private static final int TEXTURE_WIDTH = 128;
//	private static final int TEXTURE_HEIGHT = 128;
//	
//	private static final int BODY_U = 0;
//	private static final int BODY_V = 0;
//	private static final int FUSE_U = 0;
//	private static final int FUSE_V = 16;
//	private static final int AFT_U = 0;
//	private static final int AFT_V = 25;
//	private static final int SHAFT_U = 32;
//	private static final int SHAFT_V = 0;
//	private static final int FIN_U = 24;
//	private static final int FIN_V = 16;
//	
//	private ModelRenderer body;
//	private ModelRenderer fuse;
//	private ModelRenderer aft;
//	private ModelRenderer shaft;
//	private ModelRenderer fin1;
//	private ModelRenderer fin2;
//	
//	public MortarShell120mmHEModel() {
//		
//		this.body = new ModelRenderer(this, BODY_U, BODY_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.body.addBox(-3.0f, -3.0f, 0.0f, 6, 6, 10);
//		this.fuse = new ModelRenderer(this, FUSE_U, FUSE_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.fuse.addBox(-1.5f, -1.5f, 10.0f, 3, 3, 6);
//		this.aft = new ModelRenderer(this, AFT_U, AFT_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.aft.addBox(-2.0f, -2.0f, -8.0f, 4, 4, 8);
//		this.shaft = new ModelRenderer(this, SHAFT_U, SHAFT_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.shaft.addBox(-1.0f, -1.0f, -19.0f, 2, 2, 11);
//		this.fin1 = new ModelRenderer(this, FIN_U, FIN_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.fin1.setRotationPoint(0.0f, 0.0f, -19.0f);
//		this.fin1.rotateAngleZ = 45.0f * AcMathHelper.DEG2RAD;
//		this.fin1.addBox(-4.0f, 0.0f, 0.0f, 8, 1, 4);
//		this.fin2 = new ModelRenderer(this, FIN_U, FIN_V).setTextureSize(TEXTURE_WIDTH, TEXTURE_HEIGHT);
//		this.fin2.setRotationPoint(0.0f, 0.0f, -19.0f);
//		this.fin2.rotateAngleZ = -45.0f * AcMathHelper.DEG2RAD;
//		this.fin2.addBox(-4.0f, 0.0f, 0.0f, 8, 1, 4);
//		// TODO Auto-generated constructor stub
//	}
//	
//	/**
//	 * Sets the models various rotation angles then renders the model.
//	 */
//	@Override
//	public void render(Entity entityIn, float bladeAngle, float limbSwingAmount, float ageInTicks, float netHeadYaw,
//			float headPitch, float scale) {
//		GlStateManager.rotate(headPitch, 1.0f, 0.0f, 0.0f);
//		
//		this.body.render(scale);
//		this.fuse.render(scale);
//		this.aft.render(scale);
//		this.shaft.render(scale);
//		this.fin1.render(scale);
//		this.fin2.render(scale);
//	}
//	
//}
