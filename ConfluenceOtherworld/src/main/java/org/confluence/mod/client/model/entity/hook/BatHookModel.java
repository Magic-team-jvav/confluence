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

public class BatHookModel extends EntityModel<AbstractHookEntity.Impl> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("bat_hook"), "main");
    private final ModelPart bone;

    public BatHookModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 80.0F, Mth.HALF_PI));
        PartDefinition hook = bone.addOrReplaceChild("hook", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 5.0F, -3.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(14, 14).addBox(-0.5F, 2.0F, -2.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(16, 0).addBox(-0.5F, 1.0F, -1.0F, 3.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
        .texOffs(0, 14).addBox(3.0F, 4.0F, -1.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
        .texOffs(1, 8).addBox(-8.0F, 4.0F, -1.0F, 7.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AbstractHookEntity.Impl entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}