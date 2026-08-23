package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.client.entity.model.BaseSlimeModel;

/// 普通史莱姆的半透明外壳。
///
/// 外壳与内核使用同一张纹理，但必须走半透明渲染类型；实体隐身时仅在发光轮廓可见的
/// 情况下绘制轮廓，行为与原版史莱姆保持一致。
public final class BaseSlimeOuterLayer<T extends LivingEntity> extends RenderLayer<T, BaseSlimeModel<T>> {
    private final BaseSlimeModel<T> outerModel;

    public BaseSlimeOuterLayer(RenderLayerParent<T, BaseSlimeModel<T>> parent, EntityModelSet modelSet) {
        super(parent);
        this.outerModel = new BaseSlimeModel<>(modelSet.bakeLayer(BaseSlimeModel.OUTER_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T slime, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(slime);
        if (slime.isInvisible() && !glowing) {
            return;
        }
        getParentModel().copyPropertiesTo(outerModel);
        outerModel.prepareMobModel(slime, limbSwing, limbSwingAmount, partialTick);
        outerModel.setupAnim(slime, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        RenderType renderType = glowing && slime.isInvisible()
                ? RenderType.outline(getTextureLocation(slime))
                : RenderType.entityTranslucent(getTextureLocation(slime));
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        outerModel.renderToBuffer(poseStack, buffer, packedLight, LivingEntityRenderer.getOverlayCoords(slime, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
