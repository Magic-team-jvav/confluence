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
import org.confluence.mod.common.entity.hook.LunarHookEntity;

public class LunarHookSolarModel extends EntityModel<LunarHookEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("lunar_hook_solar"), "main");
    private final ModelPart bone;

    public LunarHookSolarModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(1, 11).addBox(-0.75F, -1.0F, -3.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(18, 18).addBox(-0.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -4.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 2).addBox(-4.5F, -0.5F, 0.5F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
        .texOffs(0, 19).addBox(-2.5F, -0.5F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -4.0F, -0.5F, 0.0F, 0.0F, -1.5708F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(20, 4).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.361F, -5.0787F, 0.0F, 3.1416F, 0.0F, 1.5708F));

        PartDefinition cube_r5 = hook.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 19).addBox(-0.75F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -3.25F, 0.0F, 3.1416F, 0.0F, 1.9635F));

        PartDefinition cube_r6 = hook.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(18, 14).addBox(-3.75F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, -3.5F, 0.0F, 3.1416F, 0.0F, 2.3562F));

        PartDefinition cube_r7 = hook.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(20, 0).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2666F, -5.0578F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition cube_r8 = hook.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(6, 19).addBox(-2.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.3839F, -4.3839F, 0.0F, 0.0F, 0.0F, 1.1781F));

        PartDefinition cube_r9 = hook.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(18, 10).addBox(-3.75F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -3.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(LunarHookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
