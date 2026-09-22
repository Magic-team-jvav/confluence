package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.CascadeFireProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public final class CascadeYoyoItem extends YoyoItem {
    private final int procDenominator;
    private final int minFireSeconds;
    private final int maxFireSeconds;
    private final int hitInterval;

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.cascade", hitInterval);}

    public CascadeYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int hitInterval, int procDenominator, int minFireSeconds, int maxFireSeconds) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.procDenominator = procDenominator;
        this.minFireSeconds = minFireSeconds;
        this.maxFireSeconds = maxFireSeconds;
        this.hitInterval = hitInterval;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        if (owner.getRandom().nextInt(procDenominator) == 0)
            target.setSecondsOnFire(minFireSeconds + owner.getRandom().nextInt(maxFireSeconds - minFireSeconds + 1));
        if (!yoyo.isDetached()) {
            YoyoSession session = YoyoSession.of(owner);
            if (session.isSpecialHit(hitInterval))
                new CascadeFireProjectile(ModEntities.CASCADE_FIRE.get(), yoyo.level()).shootAtNearest(yoyo, target);
            session.countSpecialHit();
        }
    }
}
