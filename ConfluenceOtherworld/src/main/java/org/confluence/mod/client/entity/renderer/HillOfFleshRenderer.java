package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.HillOfFlesh;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/// 血肉山生成前半段从地面下方旋转升起，进入战斗后固定在遭遇锚点。
/// 模型包含大量互相穿插的肉质表面，使用透明通道避免裁切通道留下硬边。
public final class HillOfFleshRenderer extends BossGeoRenderer<HillOfFlesh> {
    // 出场动画资源持续 150 tick，用它将材质透明度从 0 平滑插值到 1。
    private static final float INITIALIZATION_TICKS = 150.0F;

    public HillOfFleshRenderer(EntityRendererProvider.Context context) {
        super(context, Confluence.asResource("boss/hill_of_flesh"));
    }

    @Override
    protected void adjustPose(PoseStack poseStack, HillOfFlesh hill,
                              BakedGeoModel model, float partialTick) {
        if (!hill.isInitializing()) {
            return;
        }
        float progress = Mth.clamp((hill.tickCount + partialTick) / INITIALIZATION_TICKS, 0.0F, 1.0F);
        float riseProgress = Mth.clamp(progress * 2.0F, 0.0F, 1.0F);
        float eased = riseProgress < 0.5F
                ? 2.0F * riseProgress * riseProgress
                : 1.0F - (float) Math.pow(-2.0F * riseProgress + 2.0F, 2.0F) * 0.5F;
        poseStack.translate(0.0F, Mth.lerp(eased, -15.0F, 0.0F), 0.0F);
        poseStack.mulPose(Axis.YP.rotation(riseProgress * Mth.TWO_PI * 2.0F));
    }

    @Override
    public RenderType getRenderType(HillOfFlesh hill, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
