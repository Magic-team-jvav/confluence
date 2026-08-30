package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.mod.client.model.entity.RainbowSheepFurModel;
import org.confluence.mod.client.model.entity.RainbowSheepModel;
import org.confluence.mod.common.entity.RainbowSheep;

public class RainbowSheepFurLayer extends RenderLayer<RainbowSheep, RainbowSheepModel> {
    private static final ResourceLocation SHEEP_FUR_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/sheep/sheep_fur.png");
    private final RainbowSheepFurModel model;

    public RainbowSheepFurLayer(RenderLayerParent<RainbowSheep, RainbowSheepModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new RainbowSheepFurModel(modelSet.bakeLayer(RainbowSheepFurModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, RainbowSheep livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (livingEntity.isSheared()) return;
        if (livingEntity.isInvisible()) {
            if (Minecraft.getInstance().shouldEntityAppearGlowing(livingEntity)) {
                getParentModel().copyPropertiesTo(model);
                model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
                model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.outline(SHEEP_FUR_LOCATION));
                int packedOverlay = LivingEntityRenderer.getOverlayCoords(livingEntity, 0.0F);
                model.renderToBuffer(poseStack, vertexconsumer, packedLight, packedOverlay, -16777216);
            }
        } else {
            coloredCutoutModelCopyLayerRender(
                    getParentModel(),
                    model,
                    SHEEP_FUR_LOCATION,
                    poseStack,
                    bufferSource,
                    packedLight,
                    livingEntity,
                    limbSwing,
                    limbSwingAmount,
                    ageInTicks,
                    netHeadYaw,
                    headPitch,
                    partialTick,
                    FastColor.ARGB32.opaque(ExpertColorAnimation.INSTANCE.getColor())
            );
        }
    }
}
