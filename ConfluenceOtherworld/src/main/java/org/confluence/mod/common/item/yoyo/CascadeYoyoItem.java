package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEffectProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

public final class CascadeYoyoItem extends YoyoItem {
    @Override
    public boolean fullBright() {return true;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.cascade";}

    public CascadeYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.ORANGE, 5.5F, 14.6875F, 0xFFC896, 13 * 20, 4.3F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom().nextInt(3) == 0)
            target.setSecondsOnFire(1 + owner.getRandom().nextInt(4));
        if (!yoyo.isDetached()) {
            YoyoSession session = YoyoSession.of(owner);
            if (session.isSpecialHit(2))
                YoyoEffectProjectile.shootAtNearest(yoyo, YoyoEffectProjectile.Kind.CASCADE, target);
            session.countSpecialHit();
        }
    }
}
