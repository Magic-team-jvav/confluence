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

public class FlurryBootsModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("flurry_boots"), "main");

    public FlurryBootsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 7).addBox(-2F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
            .texOffs(15, 0).addBox(-2F, 10.0F, -3.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(12, 7).addBox(-2F, 10.0F, -3.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 0).addBox(-2.5F, 5.5F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(5, 16).addBox(2F, 9.5F, -3.75F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(16, 11).addBox(2F, 8.5F, -2.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 16).addBox(2F, 7.5F, -2.5F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(14, 14).addBox(-4F, -2F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.125F)), PartPose.offsetAndRotation(2.0F, 11F, -2.0F, 0.829F, 0.0F, 0.0F));

        PartDefinition bone2 = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
            .texOffs(15, 0).mirror().addBox(-2.0F, 10.0F, -3.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(12, 7).mirror().addBox(-2.0F, 10.0F, -3.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 0).mirror().addBox(-2.5F, 5.5F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(5, 16).mirror().addBox(-3.0F, 9.5F, -3.75F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(16, 11).mirror().addBox(-3.0F, 8.5F, -2.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 16).mirror().addBox(-3.0F, 7.5F, -2.5F, 1.0F, 1.0F, 3.0F,CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, 0.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(14, 14).mirror().addBox(-4.0F, -2F, -0.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.125F)), PartPose.offsetAndRotation(2.0F, 11F, -2.0F, 0.829F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}