package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.StormSpearShotProjectile;
import org.jetbrains.annotations.NotNull;

public class StormSpearShotProjectileModel extends EntityModel<StormSpearShotProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("storm_spear_shot_projectile"), "main");
    private final ModelPart root;

    public StormSpearShotProjectileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(12, 10).addBox(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F))
                .texOffs(0, 16).addBox(-1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.ZERO);

        bone.addOrReplaceChild("cube_ml_r1", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, 2.0659F, -0.5303F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(12, 16).addBox(-1.0F, -4.1213F, -3.5355F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_mm_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.5858F, -4.0F, 2.0F, 2.0F, 8.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull StormSpearShotProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
