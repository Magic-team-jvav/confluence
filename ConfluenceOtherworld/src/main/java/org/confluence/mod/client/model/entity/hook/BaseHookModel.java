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
import org.confluence.mod.common.entity.hook.BaseHookEntity;

public class BaseHookModel extends EntityModel<BaseHookEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("base_hook"), "main");
    private final ModelPart bone;

    public BaseHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, -Mth.HALF_PI, 0.0F));
        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.9063F, -1.0F, -6.4226F, 2.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.4363F, -1.0472F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 8).addBox(-4.7839F, -1.0F, -1.1552F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6109F, -1.0472F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-4.7839F, -1.0F, -1.1552F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6109F, 3.1416F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(10, 0).addBox(-4.7839F, -1.0F, -1.1552F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.6109F, 1.0472F));
        bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 8).addBox(-1.9063F, -1.0F, -6.4226F, 2.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, 0.4363F, 1.0472F));
        bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(10, 2).addBox(-0.0937F, -1.0F, -6.4226F, 2.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 3.0F, 0.0F, -0.4363F, 0.0F));
        bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(10, 10).addBox(-0.5F, -0.5F, -2.0F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));
        bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(11, 11).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(BaseHookEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
