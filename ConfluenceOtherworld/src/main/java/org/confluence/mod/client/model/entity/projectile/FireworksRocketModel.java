package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.FireworksRocketProjectile;

/// 烟花火箭射弹的 Blockbench 模型（32x32 贴图），机头朝 -Z。
public final class FireworksRocketModel extends EntityModel<FireworksRocketProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("fireworks_rocket"), "main");
    private final ModelPart bone;

    public FireworksRocketModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("bone", CubeListBuilder.create()
                .texOffs(15, 24).addBox(-2.0F, 0.0F, -1.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-3.0F, -1.0F, -0.5F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, 0.0F, 0.0F, 4.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-2.0F, 2.0F, 6.0F, 4.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, -6.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(FireworksRocketProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
