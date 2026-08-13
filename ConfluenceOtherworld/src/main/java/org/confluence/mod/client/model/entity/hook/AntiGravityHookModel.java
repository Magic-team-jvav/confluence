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
import org.confluence.mod.common.entity.hook.AbstractHookEntity;

public class AntiGravityHookModel extends EntityModel<AbstractHookEntity.Impl> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("anti_gravity_hook"), "main");
    private final ModelPart bone;

    public AntiGravityHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.5F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 14).addBox(0.0F, 1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(8, 14).addBox(-1.0F, 0.25F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.975F, -1.75F, 2.0F, -2.3562F, 0.0F, 3.1416F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 3).addBox(-1.0F, 0.25F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.05F, -3.0F, -1.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 6).addBox(-1.0F, 0.25F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -3.0F, 1.0F, -1.5708F, 0.0F, 3.1416F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 0).addBox(-1.0F, 0.25F, 0.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.025F, -1.75F, -2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r5 = hook.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 10).addBox(-1.0F, 1.6642F, -2.4142F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(0, 17).addBox(-1.0F, -0.3358F, -2.4142F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.75F, 0.0F, -2.3562F, 0.7854F, 3.1416F));

        PartDefinition cube_r6 = hook.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, 1.6642F, -2.4142F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(16, 14).addBox(-1.0F, -0.3358F, -2.4142F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 2.75F, 0.0F, 0.7854F, -0.7854F, 0.0F));

        PartDefinition cube_r7 = hook.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 5).addBox(-2.0F, 4.0F, -3.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -4.5F, 0.75F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AbstractHookEntity.Impl entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
