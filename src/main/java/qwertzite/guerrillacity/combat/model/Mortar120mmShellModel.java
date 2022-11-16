package qwertzite.guerrillacity.combat.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import qwertzite.guerrillacity.combat.GcCombatModule;
import qwertzite.guerrillacity.combat.entity.Mortar120mmShellEntity;
import qwertzite.guerrillacity.core.util.math.GcMath;

public class Mortar120mmShellModel extends HierarchicalModel<Mortar120mmShellEntity> {
	public static final ModelLayerLocation SHELL_MODEL = new ModelLayerLocation(GcCombatModule.KEY_MORTAR_120MM_SHELL_ENTITY.location(), "main");
	
	private static final int TEXTURE_WIDTH = 128;
	private static final int TEXTURE_HEIGHT = 128;
	
	private static final int BODY_U = 0;
	private static final int BODY_V = 0;
	private static final int FUSE_U = 0;
	private static final int FUSE_V = 16;
	private static final int AFT_U = 0;
	private static final int AFT_V = 25;
	private static final int SHAFT_U = 32;
	private static final int SHAFT_V = 0;
	private static final int FIN_U = 24;
	private static final int FIN_V = 16;
	
	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("body",
				CubeListBuilder.create().texOffs(BODY_U, BODY_V).addBox(-3.0f, -3.0f, 0.0f, 6, 6, 10),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("fuse",
				CubeListBuilder.create().texOffs(FUSE_U, FUSE_V).addBox(-1.5f, -1.5f, 10.0f, 3, 3, 6),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("aft",
				CubeListBuilder.create().texOffs(AFT_U, AFT_V).addBox(-2.0f, -2.0f, -8.0f, 4, 4, 8),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("shaft",
				CubeListBuilder.create().texOffs(SHAFT_U, SHAFT_V).addBox(-1.0f, -1.0f, -19.0f, 2, 2, 11),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("fin1",
				CubeListBuilder.create().texOffs(FIN_U, FIN_V).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 4),
				PartPose.offsetAndRotation(0.0f, 0.0f, -19.0f, 0.0f, 0.0f, 45.0f * GcMath.DEG2RAD));
		partdefinition.addOrReplaceChild("fin2",
				CubeListBuilder.create().texOffs(FIN_U, FIN_V).addBox(-4.0f, 0.0f, 0.0f, 8, 1, 4),
				PartPose.offsetAndRotation(0.0f, 0.0f, -19.0f, 0.0f, 0.0f, -45.0f * GcMath.DEG2RAD));
		return LayerDefinition.create(meshdefinition, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
	
	private ModelPart root;
	
	public Mortar120mmShellModel(ModelPart root) {
		this.root = root;
	}
	
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void setupAnim(Mortar120mmShellEntity mortar, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}
	
	@Override
	public ModelPart root() {
		return this.root;
	}
}
