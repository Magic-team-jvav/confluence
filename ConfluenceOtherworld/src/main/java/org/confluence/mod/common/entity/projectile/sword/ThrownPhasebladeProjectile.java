package org.confluence.mod.common.entity.projectile.sword;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public final class ThrownPhasebladeProjectile extends PhasebladeProjectile {
    public ThrownPhasebladeProjectile(EntityType<? extends ThrownPhasebladeProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected float airDamageMultiplier() {return 1.25F;}

    @Override
    protected float airKnockbackMultiplier() {return 1.25F;}
}
