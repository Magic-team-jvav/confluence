package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.boss.BaseBossPart;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

/// Boss 本体及其独立部件共用的 GeckoLib 渲染器。
///
/// 该类型只保留 Boss 渲染注册所需的明确语义和链式缩放返回值，
/// 绘制距离与视锥判断继续使用 Minecraft 和 GeckoLib 的默认规则。
public class BossGeoRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    public BossGeoRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
    }

    public BossGeoRenderer(EntityRendererProvider.Context context, ResourceLocation path, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        super(context, path, rotateAlongPitch, modelScale, modelOffsetY);
    }

    public BossGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    public BossGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model, boolean rotateAlongPitch, float modelScale, float modelOffsetY) {
        super(context, model, rotateAlongPitch, modelScale, modelOffsetY);
    }

    /// 普通 Boss 模型使用不透明裁切通道。需要淡入、幻象或溶解效果的专用渲染器
    /// 可以单独覆盖该方法，避免透明排序影响所有 Boss 的深度和光照。
    @Override
    public RenderType getRenderType(T entity, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Override
    public BossGeoRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }

    @Override
    public int getPackedOverlay(T entity, float u, float partialTick) {
        if (entity instanceof BaseBossPart<?> part && part.isHurtFlashing()) {
            return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(true));
        }
        return super.getPackedOverlay(entity, u, partialTick);
    }
}
