package org.confluence.mod.common.summon.projectile;

import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.summon.SummonInstance;

/// 召唤小鬼的火焰附件弹幕。
public final class ImpFireballAttachment extends SummonProjectileInstance {
    public ImpFireballAttachment(SummonInstance source, LivingEntity target) {
        super(SummonProjectileTypes.IMP_FIREBALL.id(), source, target, 2.0F, 1.0F);
    }

    @Override
    protected void onSuccessfulHit(LivingEntity target) {
        target.setSecondsOnFire(5);
    }
}
