package org.confluence.mod.common.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

public final class FrostMonsterProjectile extends StraightMonsterProjectile {
    private final Kind kind;

    public FrostMonsterProjectile(EntityType<? extends FrostMonsterProjectile> type, Level level, Kind kind) {
        super(type, level);
        this.kind = kind;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && !isRemoved() && kind != Kind.BEAM) {
            var velocity = getDeltaMovement();
            for (int i = 0; i < 4; i++) {
                double fraction = i / 4.0;
                level().addParticle(ParticleTypes.SNOWFLAKE, getX() - velocity.x * fraction, getY() - velocity.y * fraction, getZ() - velocity.z * fraction, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        if (level().isClientSide) return;
        double durationMultiplier = LibUtils.isMaster(level(), blockPosition()) ? 2.5 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 2.0 : 1.0;
        if (kind == Kind.BLAST) {
            if (random.nextInt(3) == 0)
                target.addEffect(new MobEffectInstance(ModEffects.FROST_BURN.get(), (int) (120 * durationMultiplier)), owner);
        } else {
            if (TCUtils.hasType(target, TCItems.FROZEN$IMMUNE)) return;
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) ((kind == Kind.BEAM ? 900 : 500) * durationMultiplier)), owner);
            if (random.nextInt(kind == Kind.BEAM ? 5 : 10) == 0) {
                int duration = kind == Kind.BEAM ? 20 : 10 + random.nextInt(31);
                target.addEffect(new MobEffectInstance(ModEffects.FROZEN.get(), (int) (duration * durationMultiplier)), owner);
            }
        }
    }

    public enum Kind {
        BLAST,
        SPIT,
        BEAM
    }
}
