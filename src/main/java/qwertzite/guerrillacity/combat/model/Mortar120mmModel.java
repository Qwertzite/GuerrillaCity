package qwertzite.guerrillacity.combat.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import qwertzite.guerrillacity.combat.GcCombatModule;
import qwertzite.guerrillacity.combat.entity.Mortar120mmEntity;
import qwertzite.guerrillacity.core.util.math.GcMath;

public class Mortar120mmModel extends HierarchicalModel<Mortar120mmEntity> {
	public static final ModelLayerLocation MORTAR_MODEL = new ModelLayerLocation(GcCombatModule.KEY_MORTAR_120MM_ENTITY.location(), "main");

	private static final int TEXTURE_WIDTH = 256;
	private static final int TEXTURE_HEIGHT = 256;

	private static final int BASE_PLATE_U = 0;
	private static final int BASE_PLATE_V = 0;
	private static final int BREECH_CAP_U = 0;
	private static final int BREECH_CAP_V = 0;
	private static final int BARREL_U =  0;
	private static final int BARREL_V = 28;
	private static final int TRAVERSE_GEAR_U = 24;
	private static final int TRAVERSE_GEAR_V = 28;
	private static final int BIPOD_LEG_L_U = 24;
	private static final int BIPOD_LEG_L_V = 34;
	private static final int BIPOD_LEG_R_U = 24;
	private static final int BIPOD_LEG_R_V = 34;
	private static final int BIPOD_JOINT_U = 32;
	private static final int BIPOD_JOINT_V = 34;
	private static final int BIPOD_STEM_U = 32;
	private static final int BIPOD_STEM_V = 41;
	
	public static LayerDefinition create() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("base_plate",
				CubeListBuilder.create().texOffs(BASE_PLATE_U, BASE_PLATE_V).addBox(-12.0f, 0.0f, -12.0f, 24, 4, 24),
				PartPose.ZERO);
		var breech_cap = partdefinition.addOrReplaceChild("breech_cap",
				CubeListBuilder.create().texOffs(BREECH_CAP_U, BREECH_CAP_V).addBox(-2.0f, -0.0f, -2.0f, 4, 3, 4),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		breech_cap.addOrReplaceChild("barrel", 
				CubeListBuilder.create().texOffs(BARREL_U, BARREL_V).addBox(-3.0f, 3.0f, -3.0f, 6, 50, 6),
				PartPose.offset(0.0f, 0.0f, 0.0f)); // 0 4 0
		partdefinition.addOrReplaceChild("traverse_gear",
				CubeListBuilder.create().texOffs(TRAVERSE_GEAR_U, TRAVERSE_GEAR_V).addBox(-6.0f, 31.0f, 3.0f, 12, 3, 3),
				PartPose.offset(0.0f, 4.0f, 0.0f));
		partdefinition.addOrReplaceChild("bipod_stem",
				CubeListBuilder.create().texOffs(BIPOD_STEM_U, BIPOD_STEM_V).addBox(-1.0f, -25.0f, -1.0f, 2, 25, 2),
				PartPose.ZERO);
		var bipod = partdefinition.addOrReplaceChild("bipod_joint",
				CubeListBuilder.create().texOffs(BIPOD_JOINT_U, BIPOD_JOINT_V).addBox(-4.0f, 20.0f, -1.5f, 8, 4, 3),
				PartPose.offset(0.0f, 0.0f, 40.0f));
		bipod.addOrReplaceChild("bipod_leg_left",
				CubeListBuilder.create().texOffs(BIPOD_LEG_L_U, BIPOD_LEG_L_V).addBox(-1.0f, 0.0f, -1.0f, 2, 29, 2),
				PartPose.offsetAndRotation(22.0f, 0.0f, 0.0f, 0.0f, 0.0f, 45.0f * GcMath.DEG2RAD));
		bipod.addOrReplaceChild("bipod_leg_right",
				CubeListBuilder.create().texOffs(BIPOD_LEG_R_U, BIPOD_LEG_R_V).addBox(-1.0f, 0.0f, -1.0f, 2, 29, 2),
				PartPose.offsetAndRotation(-22.0f, 0.0f, 0.0f, 0.0f, 0.0f, -45.0f * GcMath.DEG2RAD));
		
		return LayerDefinition.create(meshdefinition, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
	
	private ModelPart root;
	private ModelPart basePlate;
	private ModelPart breechCap;
	private ModelPart traverseGear;
	private ModelPart bipodJoint;
	private ModelPart bipodStem;
	
	public Mortar120mmModel(ModelPart root) {
		this.root = root;
		// base plate
		this.basePlate = root.getChild("base_plate");
		// barrel assembly
		this.breechCap = root.getChild("breech_cap");
		this.traverseGear = root.getChild("traverse_gear");
		// bipod assembly
		this.bipodJoint = root.getChild("bipod_joint");
		this.bipodStem = root.getChild("bipod_stem");
	}

	@Override
	public void setupAnim(Mortar120mmEntity mortar, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
		
		float yaw = - mortar.getFineYaw() / 1000.0f;// 0 * AcMathHelper.DEG2RAD;
		float elevation = mortar.getElevation() / 1000.0f;// * GcMath.RAD2DEG;
		float elev = GcMath.PI/2 - elevation;
		
		// base - none
		// barrel assembly
		this.breechCap.xRot = elev;
		this.breechCap.yRot = yaw;
		this.traverseGear.xRot = elev;
		// bipod assembly
		float cos = Mth.cos(elevation);
		float sin = Mth.sin(elevation);// atan y x
		float pivotZ = 32.5f*cos + 4.5f*sin;
		float pivotY = 4.0f + 32.5f*sin - 4.5f*cos;
		float bipodAngle = GcMath.PI / 2 - (float) Mth.atan2(pivotY, 40.0f - pivotZ);
		this.bipodJoint.xRot = -bipodAngle;
		
		this.bipodStem.setPos(0.0f, pivotY, pivotZ);
		this.bipodStem.xRot = -bipodAngle;
	}
	
	@Override
	public ModelPart root() {
		return this.root;
	}
}
