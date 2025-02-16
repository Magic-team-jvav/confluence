package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.HurtnadoProjectile;
import org.confluence.mod.common.entity.projectile.strip.StripedProjectile;
import org.jetbrains.annotations.NotNull;

public class HurtnadoProjectileModel extends EntityModel<HurtnadoProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("hurtnado_projectile"), "main");
    private final ModelPart root;

    public HurtnadoProjectileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(32, 52).addBox(-2.0F, 4.0F, -4.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-9.0F, 10.0F, -8.0F, 17.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-3.0F, 7.0F, -7.0F, 11.0F, 1.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-3.0F, 4.0F, -5.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(52, 25).addBox(-2.0F, 0.0F, -3.0F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(52, 16).addBox(-3.0F, 0.0F, -4.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 10.0F, 0.0F, 0.0F, 0.9599F, 0.0F));

        PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 42).addBox(-3.0F, 0.0F, -5.0F, 7.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 9.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, 0.0F, -6.0F, 8.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 7.0F, 0.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition cube_r5 = bb_main.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, 0.0F, -5.0F, 7.0F, 1.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 6.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        PartDefinition cube_r6 = bb_main.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(54, 52).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.75F, 0.7854F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 80, 80);
    }

    @Override
    public void setupAnim(@NotNull HurtnadoProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
