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

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(292, 0).addBox(-10.0F, 4.0F, -14.0F, 22.0F, 3.0F, 28.0F, new CubeDeformation(0.0F))
                .texOffs(226, 280).addBox(-11.0F, 26.0F, -23.0F, 36.0F, 4.0F, 33.0F, new CubeDeformation(0.0F))
                .texOffs(250, 124).addBox(-15.0F, 11.0F, -15.0F, 25.0F, 7.0F, 23.0F, new CubeDeformation(0.0F))
                .texOffs(116, 317).addBox(-3.0F, 0.0F, -10.0F, 17.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(292, 61).addBox(-5.0F, -4.0F, -5.0F, 10.0F, 4.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 154).addBox(-31.0F, 30.0F, -30.0F, 63.0F, 10.0F, 59.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(226, 223).addBox(-15.0F, 0.0F, -30.0F, 47.0F, 7.0F, 50.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 40.0F, 7.0F, 0.0F, 0.6109F, 0.0F));

        PartDefinition cube_r2 = bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(244, 204).addBox(-7.0F, 0.0F, -11.0F, 14.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 53.0F, 2.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition cube_r3 = bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(292, 31).addBox(-3.0F, 0.0F, -17.0F, 19.0F, 6.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 47.0F, 7.0F, 0.0F, 0.9599F, 0.0F));

        PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(116, 285).addBox(-12.0F, 0.0F, -9.0F, 24.0F, 3.0F, 29.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 23.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r5 = bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 285).addBox(-3.0F, 0.0F, -20.0F, 22.0F, 4.0F, 36.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 7.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(250, 80).addBox(-5.0F, 0.0F, -25.0F, 29.0F, 5.0F, 39.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 18.0F, 3.0F, 0.0F, 0.3491F, 0.0F));

        PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-29.0F, 19.0F, -36.0F, 76.0F, 10.0F, 70.0F, new CubeDeformation(0.0F))
                .texOffs(244, 154).addBox(-8.0F, -8.0F, -22.0F, 39.0F, 7.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 15.0F, 0.0F));

        PartDefinition cube_r7 = bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 80).addBox(-21.0F, 0.0F, -33.0F, 58.0F, 7.0F, 67.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.0F, 0.2618F, 0.0F));

        PartDefinition cube_r8 = bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 223).addBox(-17.0F, 0.0F, -30.0F, 58.0F, 7.0F, 55.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.0F, 0.6109F, 0.0F));

        return LayerDefinition.create(meshdefinition, 432, 432);
    }

    @Override
    public void setupAnim(@NotNull HurtnadoProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
