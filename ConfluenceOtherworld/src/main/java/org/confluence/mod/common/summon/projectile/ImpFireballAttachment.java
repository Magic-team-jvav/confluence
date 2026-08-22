package org.confluence.mod.common.summon.projectile;

import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.entity.boss.EaterOfWorlds;
import org.confluence.mod.common.summon.SummonInstance;

/// 召唤小鬼的火焰附件弹幕。
public final class ImpFireballAttachment extends SummonProjectileInstance {
    public ImpFireballAttachment(SummonInstance source, LivingEntity target) {
        super(SummonProjectileTypes.IMP_FIREBALL.id(), source, target, 1.0F, 1.0F);
    }

    @Override
    protected void onImpact(LivingEntity target) {
        target.setSecondsOnFire(5);
    }

    @Override
    protected boolean canHitTarget(LivingEntity target) {
        return !(target instanceof EaterOfWorlds);
    }
}
