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

public class MermanModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("merman"), "main");

    public MermanModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 17).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(34, 0).addBox(4.0F, -5.0F, -4.5F, 0.0F, 10.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.75F, 4.0F, 2.5F, 0.0F, 0.1745F, 0.0F));

        Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(34, 10).addBox(-4.0F, -5.0F, -4.5F, 0.0F, 10.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.75F, 4.0F, 2.5F, 0.0F, -0.1745F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(30, 30).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(119, 112).addBox(0.0F, -2.0F, 2.0F, 0.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(12, 62).addBox(-3.0F, -4.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 58).addBox(-3.0F, -10.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(52, 42).addBox(-1.0F, -4.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 16).addBox(-1.0F, -10.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 22.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(52, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 50).addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(36, 46).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(20, 46).addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));

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