package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.entity.model.WormPartGeoModel;
import org.confluence.mod.common.entity.boss.BossWormPart;

/// 世界吞噬者体节渲染器。
public final class BossWormPartRenderer extends BossGeoRenderer<BossWormPart> {
    public BossWormPartRenderer(EntityRendererProvider.Context context) {
        super(context, new WormPartGeoModel<>(
                Confluence.asResource("geo/entity/boss/eater_of_worlds_segment.geo.json"),
                Confluence.asResource("textures/entity/boss/eater_of_worlds_segment.png"),
                Confluence.asResource("geo/entity/boss/eater_of_worlds_tail.geo.json"),
                Confluence.asResource("textures/entity/boss/eater_of_worlds_tail.png")), true, 2.2F, 0.0F);
    }

    @Override
    protected boolean usesInterpolatedLight(BossWormPart segment) {
        return true;
    }

    @Override
    public int getPackedOverlay(BossWormPart segment, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(segment.isHurtFlashing()));
    }

    @Override
    protected int getSkyLightLevel(BossWormPart segment, BlockPos probe) {
        int light = super.getSkyLightLevel(segment, probe);
        int visibleHeight = Math.max(1, Mth.ceil(segment.getBbHeight()));
        for (int offset = 1; offset <= visibleHeight; offset++) {
            light = Math.max(light, segment.level().getBrightness(LightLayer.SKY, probe.above(offset)));
        }
        return light;
    }

    @Override
    protected int getBlockLightLevel(BossWormPart segment, BlockPos probe) {
        int light = super.getBlockLightLevel(segment, probe);
        int visibleHeight = Math.max(1, Mth.ceil(segment.getBbHeight()));
        for (int offset = 1; offset <= visibleHeight; offset++) {
            light = Math.max(light, segment.level().getBrightness(LightLayer.BLOCK, probe.above(offset)));
        }
        return light;
    }
}
