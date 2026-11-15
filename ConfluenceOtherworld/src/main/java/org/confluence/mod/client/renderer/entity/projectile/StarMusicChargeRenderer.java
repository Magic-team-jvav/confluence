package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
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

    /// 聚星靠每刻传送到施法者头顶来跟随，自身坐标会落后一截、还会被位置同步包来回拉扯，
    /// 玩家一走动就看着它在原地抖；这里和钛金碎片屏障一样按施法者坐标补偿差值，
    /// 并带上 {@link StarMusicChargeEntity#followOffset} 的头顶偏移，保证它始终悬在头上。
    @Override
    protected void translate(StarMusicChargeEntity entity, float partialTick, PoseStack poseStack) {
        LivingEntity owner = entity.getLivingOwner();
        if (owner != null) {
            translateToOwner(entity, owner, StarMusicChargeEntity.followOffset(owner), partialTick, poseStack);
        }
    }
}
