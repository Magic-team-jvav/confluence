package org.confluence.mod.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.mesdag.opallight.light.render.ColoredLightBufferSource;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

public interface ColoredGeoRenderer<T extends GeoAnimatable> extends GeoRenderer<T> {
    @Override
    default VertexConsumer checkAndRefreshBuffer(boolean isReRender, VertexConsumer buffer, MultiBufferSource source, RenderType renderType) {
        // 让 GeckoLib 检查原生缓冲区，刷新后保留原来的彩光包装。
        VertexConsumer refreshed = GeoRenderer.super.checkAndRefreshBuffer(isReRender, ColoredLightBufferSource.unwrap(buffer), ColoredLightBufferSource.unwrap(source), renderType);
        return ColoredLightBufferSource.rewrap(buffer, refreshed);
    }
}
