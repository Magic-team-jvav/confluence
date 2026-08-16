package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;

/// Boss 本体及其独立部件共用的 GeckoLib 渲染器。
///
/// <p>该类型只保留 Boss 渲染注册所需的明确语义和链式缩放返回值，
/// 绘制距离与视锥判断继续使用 Minecraft 和 GeckoLib 的默认规则。</p>
public class BossGeoRenderer<T extends Entity & GeoEntity> extends GeoNormalRenderer<T> {
    public BossGeoRenderer(EntityRendererProvider.Context context, ResourceLocation path) {
        super(context, path);
    }

    public BossGeoRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation path,
            boolean rotateAlongPitch,
            float modelScale,
            float modelOffsetY) {
        super(context, path, rotateAlongPitch, modelScale, modelOffsetY);
    }

    public BossGeoRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public BossGeoRenderer<T> withScale(float scale) {
        super.withScale(scale);
        return this;
    }
}
