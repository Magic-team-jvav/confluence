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

public class ThornHookModel extends EntityModel<AbstractHookEntity.Impl> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("thorn_hook"), "main");
    private final ModelPart bone;

    public ThornHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 5.0F, -3.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 6).addBox(0.0F, 5.25F, -2.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition cube_r1 = hook.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 12).addBox(-0.9318F, 1.2525F, 0.5F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5768F, 4.2732F, -0.5F, 3.1416F, 0.0F, 0.7854F));

        PartDefinition cube_r2 = hook.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(8, 10).addBox(-0.9318F, 0.2525F, -1.5F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5768F, 4.2732F, -1.0F, 3.1416F, 0.0F, 0.7854F));

        PartDefinition cube_r3 = hook.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(20, 8).addBox(-1.7525F, 0.0682F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5768F, 4.2732F, -1.0F, 3.1416F, 0.0F, 2.3562F));

        PartDefinition cube_r4 = hook.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.5F, -0.5F, 3.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
        .texOffs(8, 6).addBox(-1.0F, -0.5F, -2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9071F, 3.6929F, -0.5F, 0.0F, 0.0F, 2.3562F));

        PartDefinition cube_r5 = hook.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(20, 4).addBox(-2.0F, -0.5F, -1.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 4.5F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r6 = hook.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 18).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 7.0F, -1.0F, 0.0F, -1.5708F, 0.3927F));

        PartDefinition cube_r7 = hook.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 18).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0F, -1.0F, 0.0F, -1.5708F, -0.3927F));

        PartDefinition cube_r8 = hook.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 0).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r9 = hook.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(8, 14).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 7.0F, -2.0F, 0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AbstractHookEntity.Impl entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}