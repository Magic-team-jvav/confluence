package org.confluence.terraentity.client.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.model.CrownOfKingSlimeModelEntity;


public class CrownOfKingSlimeModel extends EntityModel<CrownOfKingSlimeModelEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraEntity.space( "crown_of_king_slime"), "main");
	public static final ResourceLocation TEXTURE = TerraEntity.space("textures/entity/model/crown_of_king_slime.png");
	public static final RenderType RENDER_TYPE = RenderType.entityCutout(CrownOfKingSlimeModel.TEXTURE);
	private final ModelPart bone6;

	public CrownOfKingSlimeModel(ModelPart root) {
		this.bone6 = root.getChild("bone6");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition bone6 = partdefinition.addOrReplaceChild("bone6", CubeListBuilder.create().texOffs(152, 31).addBox(-24.0F, -13.0F, -24.0F, 5.0F, 13.0F, 48.0F, CubeDeformation.NONE)
				.texOffs(0, 154).addBox(-19.0F, -13.0F, -24.0F, 38.0F, 13.0F, 5.0F, CubeDeformation.NONE)
				.texOffs(150, 0).addBox(-19.0F, -13.0F, 19.0F, 38.0F, 13.0F, 5.0F, CubeDeformation.NONE)
				.texOffs(136, 106).addBox(19.0F, -13.0F, -24.0F, 5.0F, 13.0F, 48.0F, CubeDeformation.NONE)
				.texOffs(0, 0).addBox(-25.0F, -29.0F, -25.0F, 50.0F, 29.0F, 50.0F, CubeDeformation.NONE)
				.texOffs(0, 25).addBox(-4.0F, -18.0F, -26.5F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
				.texOffs(0, 79).addBox(-23.0F, -29.0F, -23.0F, 46.0F, 29.0F, 46.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
		bone6.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 90).addBox(2.5322F, -5.4094F, -1.9558F, 4.0F, 9.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -23.1016F, -20.4533F, -0.2138F, 0.0477F, -0.2775F));
		bone6.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(18, 91).addBox(-6.5322F, -5.4094F, -1.9558F, 4.0F, 9.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -23.1016F, -20.4533F, -0.2138F, -0.0477F, 0.2775F));
		bone6.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, 1.6016F, -2.7967F, 14.0F, 20.0F, 5.0F, new CubeDeformation(0.5313F)), PartPose.offsetAndRotation(0.0F, -23.1016F, -20.4533F, -0.1309F, 0.0F, 0.0F));
		PartDefinition bone = bone6.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, -25.0F, -24.0F, -0.3927F, 0.0F, 0.0F));
		bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 79).addBox(-9.1669F, -9.1669F, -5.6614F, 6.0F, 6.0F, 5.0F, new CubeDeformation(1.25F)), PartPose.offsetAndRotation(4.0F, 2.6672F, 4.6497F, -0.0928F, 0.0924F, 0.7811F));
		PartDefinition bone2 = bone6.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(24, 25).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 11.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(21.0F, -19.0F, -2.0F, 0.0F, 0.0F, -0.0873F));
		bone2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(22, 79).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0313F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition bone4 = bone6.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(24, 25).mirror().addBox(-2.0F, 0.0F, -3.0F, 4.0F, 11.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-21.0F, -19.0F, -2.0F, 0.0F, 0.0F, 0.0873F));
		bone4.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(22, 79).mirror().addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0313F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition bone3 = bone6.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(24, 25).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 11.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(20.25F, -23.0F, -18.5F, -0.1309F, 0.2618F, -0.0873F));
		bone3.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(22, 79).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0313F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition bone5 = bone6.addOrReplaceChild("bone5", CubeListBuilder.create().texOffs(24, 25).mirror().addBox(-2.0F, 0.0F, -3.0F, 4.0F, 11.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-20.25F, -23.0F, -18.5F, -0.1309F, -0.2618F, 0.0873F));
		bone5.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(22, 79).mirror().addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0313F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(CrownOfKingSlimeModelEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		bone6.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}