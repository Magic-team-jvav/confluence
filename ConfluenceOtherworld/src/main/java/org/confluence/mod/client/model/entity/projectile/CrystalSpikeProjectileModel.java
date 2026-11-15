package org.confluence.mod.client.model.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.SlimeSpikeEntity;

/// 史莱姆尖刺弹丸的客户端模型。
public class CrystalSpikeProjectileModel extends EntityModel<SlimeSpikeEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("crystal_spike_projectile"), "main");
    private final ModelPart root;

    public CrystalSpikeProjectileModel(ModelPart root) {
        this.root = root.getChild("slime");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition slime = root.addOrReplaceChild("slime", CubeListBuilder.create(), PartPose.ZERO);
        slime.addOrReplaceChild("spike", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.9F, 1.5F, 0.9F))
                        .texOffs(5, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(1.1F, 0.6F, 1.1F)),
                PartPose.offsetAndRotation(
                        -0.2929F, 0.5478F, 2.4749F,
                        1.5272F, 0.0F, 1.5708F));
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void setupAnim(SlimeSpikeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
