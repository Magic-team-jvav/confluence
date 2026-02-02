package org.confluence.terra_curio.client.model.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.terra_curio.TerraCurio;

public class TerrasparkBootsModel extends HumanoidModel<LivingEntity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("terraspark_boots"), "main");

	public TerrasparkBootsModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition group2 = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 4).addBox(-2.0F, 10.0F, -3.1F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)//鞋跟  y越大，越往下 x越大，越往左 z越大越往后
		.texOffs(12, 15).addBox(-2.0F, 10F, -4.05F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))//鞋子前部
		.texOffs(14, 7).addBox(-2.5F, 8.375F, -3.3F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)//鞋子1
		.texOffs(12, 0).addBox(-2.5F, 6.875F, -3.3F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)//鞋子2
		.texOffs(0, 10).addBox(-2.5F, 5.450F, -3.05F, 5.0F, 1.0F, 4.0F, CubeDeformation.NONE)//鞋子3
		.texOffs(0, 15).addBox(-2.5F, 4.450F, -3.05F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE)//鞋子4
		.texOffs(0, 0).addBox(-2.0F, 6.0F, -1.8F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(8.0F, 24.0F, -8.0F));//本体

		group2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 17).addBox(1.875F, 0.0F, 1.5F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.25F, 7.12549F, -0.55F, 0.3927F, 0.0F, 0.0F));//装饰1

		group2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(1.875F, 1.25F, 1.25F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.25F, 7.1254F, -1.3F, 0.3927F, 0.0F, 0.0F));//装饰2

		group2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(14, 11).addBox(-2.5F, 1.5F, -1.0F, 5.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.0F, 8.575F, -1.55F, -0.7854F, 0.0F, 0.0F));//鞋带

		PartDefinition group = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(16, 4).mirror().addBox(-2.0F, 10.0F, -3.1F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(12, 15).mirror().addBox(-2.0F, 10F, -4.05F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
		.texOffs(14, 7).mirror().addBox(-2.5F, 8.375F, -3.3F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)
		.texOffs(12, 0).mirror().addBox(-2.5F, 6.875F, -3.3F, 5.0F, 1.0F, 3.0F, CubeDeformation.NONE)
		.texOffs(0, 10).mirror().addBox(-2.5F, 5.450F, -3.05F, 5.0F, 1.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(0, 15).mirror().addBox(-2.5F, 4.450F, -3.05F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 0).mirror().addBox(-2.0F, 6.0F, -1.8F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		group.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-2.875F, 0.0F, 1.5F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.25F, 7.12549F, -0.55F, 0.3927F, 0.0F, 0.0F));

		group.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.875F, 1.25F, 1.25F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.25F, 7.1254F, -1.3F, 0.3927F, 0.0F, 0.0F));

		group.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(14, 11).mirror().addBox(-2.5F, 1.5F, -1.0F, 5.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.0F, 8.575F, -1.55F, -0.7854F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
		rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
	}
}