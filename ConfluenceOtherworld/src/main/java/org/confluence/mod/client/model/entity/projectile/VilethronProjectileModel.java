package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.strip.StripedProjectile;
import org.jetbrains.annotations.NotNull;

public class VilethronProjectileModel extends EntityModel<StripedProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("vilethron_projectile"), "main");
    private final ModelPart root;

    public VilethronProjectileModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 26).addBox(-8.0F, -30.0F, 0.0F, 0.0F, 16.0F, 26.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-16.0F, -21.0F, 0.0F, 16.0F, 0.0F, 26.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(12.0F, 24.0F, 8.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void setupAnim(@NotNull StripedProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
