package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.HornetStingerProjectile;

/// 黄蜂毒刺弹幕模型，与 1.21 侧的十字薄片网格保持一致。
public final class HornetStingerProjectileModel
        extends EntityModel<HornetStingerProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(Confluence.asResource("hornet_stinger"), "main");
    private final ModelPart root;

    public HornetStingerProjectileModel(ModelPart root) {
        this.root = root.getChild("stinger");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild(
                "stinger",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(0.0F, 4.0F, -7.0F, 0.0F, 6.0F, 10.0F,
                                CubeDeformation.NONE)
                        .texOffs(0, 0)
                        .addBox(-3.0F, 7.0F, -7.0F, 6.0F, 0.0F, 10.0F,
                                CubeDeformation.NONE),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(
            HornetStingerProjectile entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {}

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        root.render(
                poseStack, buffer, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}
