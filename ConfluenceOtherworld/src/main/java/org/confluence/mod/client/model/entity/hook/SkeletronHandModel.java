package org.confluence.mod.client.model.entity.hook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;

public class SkeletronHandModel extends EntityModel<AbstractHookEntity.Impl> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("skeletron_hand"), "main");
    private final ModelPart bone;

    public SkeletronHandModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(21, 16).addBox(-1.9063F, -1.0F, -4.4226F, 1.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 1.0F, 3.0F, 0.0F, 0.4363F, -1.0472F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 3).addBox(-1.9063F, -1.0F, -2.4226F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0F, -0.5F, -1.0F, 0.3927F, 0.4363F, -1.0472F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-4.7839F, -1.0F, -1.1552F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.25F, 0.5F, -4.25F, -0.48F, 0.48F, 3.1416F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(14, 16).addBox(-4.7839F, -1.0F, -4.1552F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(3.75F, 0.0F, -3.0F, -0.0803F, -0.6016F, 1.328F));
        bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(20, 5).addBox(-4.7839F, -1.0F, -4.1552F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.75F, 0.0F, -3.0F, 0.0664F, -0.6133F, 1.3125F));
        bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(8, 16).addBox(-4.7839F, 0.0F, -3.1552F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-2.0F, -1.0F, -2.0F, 0.1309F, -0.6109F, 1.0472F));
        bone.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 8).addBox(-1.9063F, -1.0F, -6.4226F, 2.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 0.0F, 1.0F, -0.0981F, 0.4205F, 1.3347F));
        bone.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-1.9063F, -1.0F, -7.4226F, 2.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 0.0F, 1.0F, 0.0328F, 0.4205F, 1.3347F));
        bone.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(10, 9).addBox(-1.9063F, 0.0F, -6.4226F, 2.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, -1.0F, 1.0F, 0.0F, 0.4363F, 1.0472F));
        bone.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 15).addBox(-0.0937F, 0.0F, -6.4226F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 1.0F, 3.0F, 0.0F, -0.4363F, 0.0F));
        bone.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(11, 0).addBox(-2.5F, -0.5F, 1.0F, 6.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3491F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AbstractHookEntity.Impl entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}