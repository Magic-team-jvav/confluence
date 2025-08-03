package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.ThrowableDropSelfProjectile;
import org.jetbrains.annotations.NotNull;

public class FrostDaggerfishProjectileModel extends EntityModel<ThrowableDropSelfProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("frost_daggerfish_projectile"), "main");
    private final ModelPart root;

    public FrostDaggerfishProjectileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(12, 16).addBox(0.0F, -3.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -4.0F, -7.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(0.5F, -4.5F, -1.5F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(12, 9).addBox(0.0F, -3.0F, -10.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 26.5F, 3.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 4).addBox(1.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.25F, -1.0F, -5.5F, 0.0F, 0.0F, -2.3562F));

        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(18, 0).addBox(1.0F, -2.0F, -1.0F, 0.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.5F, -5.5F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull ThrowableDropSelfProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
