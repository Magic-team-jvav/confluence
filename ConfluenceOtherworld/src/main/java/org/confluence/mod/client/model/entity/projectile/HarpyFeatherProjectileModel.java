package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.HarpyFeatherProjectile;

/// 鸟妖羽毛弹幕的双羽片模型。
///
/// <p>模型尺寸和贴图布局保持 1.21 侧的 48×48 结构，旋转交给渲染器根据弹幕速度统一处理。</p>
public final class HarpyFeatherProjectileModel extends EntityModel<HarpyFeatherProjectile> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("harpy_feather_projectile"), "main");
    private final ModelPart bone;

    public HarpyFeatherProjectileModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bone = root.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0F, 22.75F, 0.0F, 0.1745F, 0.0F, 0.0F));
        bone.addOrReplaceChild(
                "right_feather",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(
                                -2.0F, -2.0F, -9.0F,
                                7.0F, 0.0F, 16.0F,
                                new CubeDeformation(0.0F)),
                PartPose.rotation(0.0F, 0.0F, -0.5672F));
        bone.addOrReplaceChild(
                "left_feather",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(
                                -4.0F, -2.0F, -9.0F,
                                7.0F, 0.0F, 16.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(
                        -6.25F, -0.75F, 0.0F,
                        0.0F, 0.0F, 0.6109F));
        return LayerDefinition.create(mesh, 48, 48);
    }

    @Override
    public void setupAnim(HarpyFeatherProjectile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
        poseStack.translate(0.0, -1.45, 0.0);
        bone.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
