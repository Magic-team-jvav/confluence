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

public class WebSlingerModel extends EntityModel<AbstractHookEntity.Impl> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("web_slinger"), "main");
    private final ModelPart bone;

    public WebSlingerModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.ZERO);
        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 23).addBox(-4.0F, -1.0F, 1.0F, 9.0F, 8.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, -7.0F, -0.3911F, -0.0066F, -0.0334F));
        bone.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 2.0F, -10.0F, 8.0F, 0.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.1319F, -0.1438F, 0.0171F));
        bone.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, 2.0F, -9.0F, 8.0F, 0.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 2.0F, 4.0F, 0.5166F, 0.2195F, 2.0516F));
        bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 8).addBox(-5.0F, 2.0F, -9.0F, 8.0F, 0.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 1.0F, 2.0F, -0.6456F, 0.2054F, 1.1072F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(AbstractHookEntity.Impl entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}