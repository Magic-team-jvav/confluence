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

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 26).addBox(-8.0F, -15.0F, 0.0F, 0.0F, 16.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-16.0F, -6.0F, 0.0F, 16.0F, 0.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void setupAnim(@NotNull StripedProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
