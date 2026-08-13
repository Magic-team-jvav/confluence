package org.confluence.mod.client.model.entity.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.MimicHookEntity;

public class TendonHookModel extends EntityModel<MimicHookEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("tendon_hook"), "main");
    private final ModelPart bone;

    public TendonHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, 8.0F, -3.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 0).addBox(-2.0F, 6.0F, -4.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-1.1108F, -1.89F, -0.9646F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(16, 8).addBox(-1.8892F, -0.61F, -1.0354F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5563F, 4.3375F, -3.5063F, -0.3655F, -0.7119F, 0.5299F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 13).addBox(-1.8891F, -0.61F, -1.0353F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(20, 22).addBox(-1.1108F, -1.89F, -0.9646F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4563F, 4.3375F, 1.5437F, 0.3655F, 0.7119F, 0.5299F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 18).addBox(-1.1108F, -1.89F, -0.9646F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(10, 18).addBox(-1.8891F, -0.61F, -1.0353F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5437F, 4.3375F, -3.6063F, -2.7761F, -0.7119F, 2.6117F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(10, 23).addBox(-1.1108F, -1.89F, -0.9646F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(0, 17).addBox(-1.8891F, -0.61F, -1.0353F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5437F, 4.3375F, 1.4437F, 2.7761F, 0.7119F, 2.6117F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(MimicHookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
