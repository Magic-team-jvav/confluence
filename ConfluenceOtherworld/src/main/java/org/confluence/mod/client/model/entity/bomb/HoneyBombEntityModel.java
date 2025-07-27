package org.confluence.mod.client.model.entity.bomb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.bomb.LiquidBombEntity;
import org.jetbrains.annotations.NotNull;

public class HoneyBombEntityModel extends EntityModel<LiquidBombEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("honey_bomb"), "main");
    private final ModelPart root;

    public HoneyBombEntityModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(20, 12).addBox(-1.5F, -26.0F, -2.5F, 3.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(20, 19).addBox(-0.5F, -28.0F, -1.0F, 1.0F, 2.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(0.0F, -28.0F, -1.5F, 0.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-3.0F, -25.0F, -4.0F, 6.0F, 6.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(0, 12).addBox(-2.5F, -24.5F, -3.5F, 5.0F, 4.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull LiquidBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
