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
import org.confluence.terra_curio.mixin.client.accessor.ModelPartAccessor;

public class ShieldOfCthulhuModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("shield_of_cthulhu"), "main");

    public ShieldOfCthulhuModel(ModelPart root) {
        super(root);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 24).addBox(3.0F, 4.0F, -5.0F, 2.0F, 10.0F, 10.0F, CubeDeformation.NONE)
            .texOffs(40, 56).addBox(-3.0F, 6.0F, -3.0F, 6.0F, 2.0F, 6.0F, CubeDeformation.NONE)
            .texOffs(24, 30).addBox(-3.0F, 6.0F, -6.0F, 6.0F, 2.0F, 12.0F, CubeDeformation.NONE)
            .texOffs(0, 50).addBox(-3.0F, 3.0F, -3.0F, 6.0F, 8.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(8.0F, 19.0F, 0.0F));
        left_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.5F, -1.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 3.7678F, -4.3033F, -1.3526F, 0.0F, 0.0F));
        left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -6.0F, -6.0F, 2.0F, 12.0F, 12.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.25F, 9.0F, 0.0F, -0.7854F, 0.0F, 0.0F));
        left_arm.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, -1.0F, -3.5F, 2.0F, 2.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 13.7678F, -4.3033F, -0.2182F, 0.0F, 0.0F));
        PartDefinition mouth = left_arm.addOrReplaceChild("mouth", CubeListBuilder.create(), PartPose.offset(0.0F, 7.0F, 0.0F));
        mouth.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(17, 13).addBox(-1.0F, -5.0F, -6.0F, 6.0F, 6.0F, 11.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.25F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        if (leftArm.visible) {
            poseStack.pushPose();
            leftArm.translateAndRotate(poseStack);
            if (!leftArm.skipDraw) {
                ((ModelPartAccessor) (Object) leftArm).callCompile(poseStack.last(), vertexConsumer, packedLight, packedOverlay, color);
            }

            leftArm.getChild("cube_r1").render(poseStack, vertexConsumer, packedLight, packedOverlay);
            leftArm.getChild("cube_r2").render(poseStack, vertexConsumer, packedLight, packedOverlay);
            leftArm.getChild("cube_r3").render(poseStack, vertexConsumer, packedLight, packedOverlay);

            poseStack.pushPose();
            poseStack.scale(0.8F, 1.3F, 1.0F);
            leftArm.getChild("mouth").render(poseStack, vertexConsumer, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.popPose();
        }
    }
}