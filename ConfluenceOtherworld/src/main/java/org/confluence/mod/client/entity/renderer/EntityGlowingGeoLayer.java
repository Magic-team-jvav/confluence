package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

/// 生物发光层沿用自动发光纹理解析，但不丢弃本体传来的受击叠色。
public class EntityGlowingGeoLayer<T extends Entity & GeoEntity> extends AutoGlowingGeoLayer<T> {
    public EntityGlowingGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    protected RenderType getRenderType(T entity) {
        return RenderStateShardAccessor.entityGlow(getTextureResource(entity));
    }

    @Override
    public void render(PoseStack poses, T entity, BakedGeoModel model, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, float partialTick, int light, int overlay) {
        RenderType glow = getRenderType(entity);
        getRenderer().reRender(model, poses, buffers, entity, glow, buffers.getBuffer(glow), partialTick, LightTexture.FULL_BRIGHT, overlay, 1, 1, 1, 1);
    }
}
