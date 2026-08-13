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
import org.confluence.mod.common.entity.hook.DualHookEntity;

public class DualHookModel extends EntityModel<DualHookEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("dual_hook"), "main");
    private final ModelPart bone;

    public DualHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 9.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(14, 21).addBox(-3.5427F, -5.989F, -0.9861F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7187F, 10.3207F, -0.0687F, 0.2511F, -0.1328F, -1.2073F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-0.8917F, -5.5568F, -0.9623F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7187F, 10.3207F, -0.0687F, 0.1822F, -0.2183F, -0.8178F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(22, 0).addBox(-0.936F, -3.7364F, -5.7884F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0948F, 10.3257F, -3.0096F, 0.3803F, 0.2415F, -0.0149F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 8).addBox(-0.8999F, -5.4317F, -0.7934F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0948F, 10.3257F, -3.0096F, -0.7978F, 0.2415F, -0.0149F));

        PartDefinition cube_r5 = hook.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(14, 18).addBox(1.5626F, -6.0222F, -0.9912F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7236F, 10.2832F, 0.736F, 0.6924F, 0.182F, 1.375F));

        PartDefinition cube_r6 = hook.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(8, 8).addBox(-0.8059F, -5.3909F, -1.0088F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7236F, 10.2832F, 0.736F, 0.5936F, 0.4197F, 1.0466F));

        PartDefinition cube_r7 = hook.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 16).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 9.0F, 0.5F, 1.5708F, 1.1781F, 1.5708F));

        PartDefinition cube_r8 = hook.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 12).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 9.0F, -2.5F, -1.5708F, 1.1781F, -1.5708F));

        PartDefinition cube_r9 = hook.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(16, 6).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 9.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r10 = hook.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(16, 0).addBox(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 9.0F, -1.0F, 0.0F, 0.0F, -0.3927F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(DualHookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
