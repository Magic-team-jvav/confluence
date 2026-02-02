package org.confluence.terra_furniture.test;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terra_furniture.api.client.model.CacheBlockModel;
import org.confluence.terra_furniture.common.block.light.LargeChandelierBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.texture.GeoAbstractTexture;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class TestGlow extends GeoBlockRenderer<LargeChandelierBlock.BEntity> {
    public TestGlow() {
        super(new CacheBlockModel<>());
        addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            protected @NotNull RenderType getRenderType(LargeChandelierBlock.BEntity animatable, @Nullable MultiBufferSource bufferSource) {
                return RenderType.eyes(GeoAbstractTexture.appendToPath(getTextureLocation(animatable), "_glowmask"));
            }
        });
    }

    @Override
    public @Nullable RenderType getRenderType(LargeChandelierBlock.BEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout( texture);
    }
}
