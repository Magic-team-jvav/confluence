package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class ThrownPhasesaberProjectile extends PhasebladeProjectile {
    public ThrownPhasesaberProjectile(EntityType<? extends ThrownPhasesaberProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected float airDamageMultiplier() {return 1.5F;}

    @Override
    protected float airKnockbackMultiplier() {return 1.25F;}
}
