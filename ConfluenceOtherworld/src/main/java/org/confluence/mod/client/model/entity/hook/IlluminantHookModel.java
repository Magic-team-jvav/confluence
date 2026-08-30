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

public class IlluminantHookModel extends EntityModel<MimicHookEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("illuminant_hook"), "main");
    private final ModelPart bone;

    public IlluminantHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 9.0F, -3.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 23).addBox(0.0F, 12.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-1.8322F, -2.0427F, -1.0119F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2187F, 6.3207F, 0.4313F, 0.2194F, -0.2143F, -0.8091F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 15).addBox(-0.8215F, -1.2563F, -0.9881F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2187F, 6.3207F, 0.4313F, 0.1201F, -0.2815F, -0.4097F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(24, 3).addBox(-1.018F, -1.8075F, -2.0604F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1552F, 6.3257F, -4.2596F, 0.7854F, 0.2618F, 0.0F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(8, 7).addBox(-0.982F, -1.2494F, -1.1489F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1552F, 6.3257F, -4.2596F, -0.3927F, 0.2618F, 0.0F));

        PartDefinition cube_r5 = hook.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(8, 23).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1971F, 4.5664F, 2.2221F, 0.6155F, 0.5236F, 0.9553F));

        PartDefinition cube_r6 = hook.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 7).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, 8.0F, 1.25F, 0.3655F, 0.7119F, 0.5299F));

        PartDefinition cube_r7 = hook.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(16, 8).addBox(-0.5F, -4.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 12.0F, -4.0F, 1.5708F, -1.1781F, -1.5708F));

        PartDefinition cube_r8 = hook.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 16).addBox(-0.5F, -4.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 12.0F, 2.0F, -1.5708F, -1.1781F, 1.5708F));

        PartDefinition cube_r9 = hook.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(16, 0).addBox(-0.5F, -4.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 12.0F, -1.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r10 = hook.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(8, 15).addBox(-0.5F, -4.0F, -1.5F, 1.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 12.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(MimicHookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
