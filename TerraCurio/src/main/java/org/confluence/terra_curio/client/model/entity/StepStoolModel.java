package org.confluence.terra_curio.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.entity.StepStoolEntity;

public class StepStoolModel extends EntityModel<StepStoolEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(TerraCurio.asResource("step_stool"), "main");
    private final ModelPart bb_main;

    public StepStoolModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 2.0F, 16.0F, CubeDeformation.NONE)
                .texOffs(26, 18).addBox(4.0F, -14.0F, 4.0F, 2.0F, 14.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(18, 18).addBox(-6.0F, -14.0F, 4.0F, 2.0F, 14.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(8, 0).addBox(4.0F, -14.0F, -6.0F, 2.0F, 14.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-6.0F, -14.0F, -6.0F, 2.0F, 14.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 31).addBox(-4.0F, -11.0F, 4.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(-4.0F, -11.0F, -5.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 18).addBox(4.5F, -11.0F, -4.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 18).addBox(-5.5F, -11.0F, -4.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(StepStoolEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        bb_main.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}