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

public class DuneriderBootsModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("dunerider_boots"), "main");

    public DuneriderBootsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 7).addBox(-2F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
            .texOffs(15, 0).addBox(-2F, 10.0F, -3.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(12, 7).addBox(-2F, 10.0F, -3.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 0).addBox(-2.5F, 5.5F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE)
            .texOffs(11, 11).addBox(2F, 6.5F, 0.5F, 1.0F, 6.0F, 5.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(15, 3).addBox(-1.5F, 9.0F, -7.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.4035F, -0.1744F, 0.4363F, 0.0F, 0.0F));

        PartDefinition bone2 = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 7).mirror().addBox(-2.0F, 7.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.25F))
            .texOffs(15, 0).mirror().addBox(-2.0F, 10.0F, -3.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.25F))
            .texOffs(12, 7).mirror().addBox(-2.0F, 10.0F, -3.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 0).mirror().addBox(-2.5F, 5.5F, -2.5F, 5.0F, 2.0F, 5.0F, CubeDeformation.NONE)
            .texOffs(11, 11).mirror().addBox(-3F, 6.5F, 0.5F, 1.0F, 6.0F, 5.0F, new CubeDeformation(-1.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(15, 3).mirror().addBox(-1.5F, 9.0F, -7.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.4035F, -0.1744F, 0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}
