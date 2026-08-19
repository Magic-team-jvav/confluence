package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.BrainOfCthulhu;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 克苏鲁之脑专用的淡入淡出渲染器。
///
/// <p>服务端只同步当前技能段和经过时间，客户端据此逐帧插值透明度。重新定位发生在完全淡出后，
/// 因而不会把服务端的瞬间坐标修改直接显示成模型跳切；实体深度关系仍由半透明实体通道处理。</p>
public final class BrainOfCthulhuRenderer extends BossGeoRenderer<BrainOfCthulhu> {
    public BrainOfCthulhuRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/brain_of_cthulhu"));
    }

    @Override
    public RenderType getRenderType(BrainOfCthulhu brain, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            BrainOfCthulhu brain,
            BakedGeoModel model,
            MultiBufferSource buffers,
            VertexConsumer buffer,
            boolean reRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        super.preRender(poseStack, brain, model, buffers, buffer, reRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha * brain.getFadeProgress(partialTick));
    }
}
