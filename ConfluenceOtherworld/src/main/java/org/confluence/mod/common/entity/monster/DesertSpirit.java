package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.leaf.CasterCycleAction;
import org.confluence.mod.common.entity.projectile.DesertSpiritCurse;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.RawAnimation;

public final class DesertSpirit extends BaseCasterMonster {
    private static final CasterCycleAction.Timing TIMING = new CasterCycleAction.Timing(1, 121, 70, 10, 10, 60);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public DesertSpirit(EntityType<? extends DesertSpirit> type, Level level) {
        super(type, level, CasterCycleAction.HurtResponse.PAUSE_THEN_TELEPORT);
    }

    @Override
    protected EntityType<DesertSpiritCurse> projectileType() {
        return ModEntities.DESERT_SPIRIT_CURSE.get();
    }

    @Override
    protected int castsPerCycle() {
        return 5;
    }

    @Override
    protected CasterCycleAction.Timing casterTiming() {
        return TIMING;
    }

    @Override
    protected boolean shouldInterruptCastingAfterHurt() {
        return random.nextInt(3) != 0;
    }

    @Override
    protected RawAnimation getRestAnimation(boolean moving) {
        return IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DEATH.get();
    }

}
