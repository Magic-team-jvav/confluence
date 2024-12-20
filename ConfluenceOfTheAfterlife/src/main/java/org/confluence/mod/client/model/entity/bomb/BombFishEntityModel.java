package org.confluence.mod.client.model.entity.bomb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.bomb.BombFishEntity;
import org.jetbrains.annotations.NotNull;

public class BombFishEntityModel extends EntityModel<BombFishEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("bomb_fish_entity"), "main");
    private final ModelPart root;

    public BombFishEntityModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 3).addBox(4.0F, 6.0F, -2.9904F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-5.9968F, 6.0F, -2.992F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(17, 21).addBox(-4.0F, 8.0F, -4.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 20).addBox(-4.0F, 8.0F, 0.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(17, 19).addBox(-4.0F, 8.0F, 4.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 18).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(17, 17).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-4.0F, -1.0F, 4.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 23).addBox(4.0F, 0.0F, -4.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 23).addBox(4.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 22).addBox(-5.0F, 0.0F, -4.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(8, 22).addBox(-5.0F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(4, 22).addBox(-5.0F, 0.0F, 4.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(4.0F, 0.0F, 4.0F, 1.0F, 8.0F, 1.0F, CubeDeformation.NONE), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    @Override
    public void setupAnim(@NotNull BombFishEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
