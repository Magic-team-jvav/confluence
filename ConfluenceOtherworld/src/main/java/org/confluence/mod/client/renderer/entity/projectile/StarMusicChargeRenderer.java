package org.confluence.mod.client.renderer.entity.projectile;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.confluence.mod.common.entity.projectile.mana.StarMusicChargeEntity;

/// 星乐头顶聚星：随蓄力时间从小放大，复用星怒的交叉面片。
public class StarMusicChargeRenderer extends FallingStarCrossRenderer<StarMusicChargeEntity> {
    public StarMusicChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float scale(StarMusicChargeEntity entity) {
        float progress = Math.min(entity.tickCount, 60) / 60.0F;
        return 0.35F + progress * 0.65F;
    }
}
