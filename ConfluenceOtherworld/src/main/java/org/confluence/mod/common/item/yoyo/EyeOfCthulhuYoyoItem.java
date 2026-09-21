package org.confluence.mod.common.item.yoyo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.YoyoEffectProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;

public final class EyeOfCthulhuYoyoItem extends YoyoItem {
    public EyeOfCthulhuYoyoItem() {
        super(new Properties().unbreakable(), ModRarity.YELLOW, 28.75F, 22.5F, 0xFFFFFFFF, 0, 3.5F);
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(7))
            YoyoEffectProjectile.shootAtNearest(yoyo, YoyoEffectProjectile.Kind.EYE, target);
        session.countSpecialHit();
    }

    @Override
    public float hitMultiplier(ServerPlayer owner) {return YoyoSession.of(owner).isSpecialHit(7) ? 2 : 1;}

    @Override
    protected String effectTooltip() {return "tooltip.confluence.yoyo.afterimage";}
}
