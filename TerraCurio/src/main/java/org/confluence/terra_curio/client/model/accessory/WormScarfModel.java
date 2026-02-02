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

public class WormScarfModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("worm_scarf"), "main");

    public WormScarfModel(ModelPart root) {
        super(root);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 2.0F, -4.0F, 3.0F, 7.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(0, 13).addBox(-2.0F, 2.0F, -3.25F, 3.0F, 6.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(10, 17).addBox(0.0F, 9.0F, -4.0F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE)
            .texOffs(10, 13).addBox(-2.0F, 8.0F, -3.25F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.ZERO);
        body.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 3.0F, 10.0F, CubeDeformation.NONE), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay);
    }
}