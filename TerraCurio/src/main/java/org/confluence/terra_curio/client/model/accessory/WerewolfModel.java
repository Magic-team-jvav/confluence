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

public class WerewolfModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("werewolf"), "main");

    public WerewolfModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(51, 28).addBox(-6.0F, -10.0F, -1.0F, 12.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 3.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(45, 16).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, -2.25F, 2.25F, -0.3579F, -0.2537F, 0.0461F));

        Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(23, 23).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, -4.75F, 3.0F, -0.0873F, -0.1309F, 0.0F));

        Head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(0, 46).addBox(-2.5F, -2.5F, -3.0F, 5.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -2.25F, 2.25F, -0.3579F, 0.2537F, -0.0461F));

        Head.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(24, 7).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -4.75F, 3.0F, -0.0873F, 0.1309F, 0.0F));

        Head.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(0, 32).addBox(-2.5F, -2.5F, -4.0F, 5.0F, 5.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -6.5F, 2.0F, 0.2618F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(43, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(28, 37).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.ZERO);

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(16, 69).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(67, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(36, 65).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(60, 61).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(60, 37).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 58).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 53).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 49).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}