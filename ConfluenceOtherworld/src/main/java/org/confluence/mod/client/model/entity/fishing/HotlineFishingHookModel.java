package org.confluence.mod.client.model.entity.fishing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.fishing.HotlineFishingHook;
import org.jetbrains.annotations.NotNull;

public class HotlineFishingHookModel extends EntityModel<HotlineFishingHook> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Confluence.asResource("hotline_fishing_hook"), "main");
    private final ModelPart bb_main;

    public HotlineFishingHookModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE)
            .texOffs(0, 6).addBox(0.0F, -4.0F, -1.0F, 0.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
        bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(6, 4).addBox(0.0F, -1.0F, -2.5F, 0.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -2.3562F, 0.0F));
        bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(6, 4).addBox(0.0F, -1.0F, -2.5F, 0.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(@NotNull HotlineFishingHook pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack pPoseStack, @NotNull VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int color) {
        bb_main.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, color);
    }
}
