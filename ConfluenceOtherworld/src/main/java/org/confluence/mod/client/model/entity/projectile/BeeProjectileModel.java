package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;

/// 本体蜜蜂弹幕的轻量模型。
///
/// 剑类蜜蜂弹幕、蜜蜂箭和蜜蜂枪弹使用的实体类型并不相同，但外观都应该是同一个小蜜蜂。
/// 因此模型只依赖原版 Entity，不把渲染层绑死到某一种弹幕类上，后续新增蜜蜂类弹幕时可以直接复用。
public final class BeeProjectileModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("bee_projectile"), "main");
    private final ModelPart root;

    public BeeProjectileModel(ModelPart root) {
        this.root = root.getChild("bee");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition bee = root.addOrReplaceChild(
                "bee",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 5.0F, CubeDeformation.NONE)
                        .texOffs(0, 0)
                        .addBox(0.0F, -0.25F, 0.5F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE),
                PartPose.ZERO);
        bee.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create()
                        .texOffs(0, 9)
                        .addBox(-4.3922F, 2.0F, -2.3309F, 3.0F, 0.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2182F, 0.0F));
        bee.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create()
                        .texOffs(6, 9)
                        .addBox(1.2164F, 2.0F, -2.4763F, 3.0F, 0.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2182F, 0.0F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
