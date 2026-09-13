package org.confluence.mod.common.entity.monster;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 会瞬移并投掷火球的地狱法师。
///
/// 火焰免疫、环境火星和火球点燃效果均属于该生物自身，不由通用法师基类猜测。
public class FireImp extends BaseCasterMonster {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");

    public FireImp(EntityType<? extends BaseCasterMonster> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean fireImmune() { return true; }

    @Override
    protected EntityType<HostileParticleProjectile> projectileType() {
        return ModEntities.FIRE_IMP_PROJECTILE.get();
    }

    @Override
    protected RawAnimation getRestAnimation(boolean moving) {
        return IDLE;
    }

    @Override
    protected SoundEvent getCastSound() {
        return SoundEvents.BLAZE_SHOOT;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            return;
        }
        if (random.nextInt(24) == 0 && !isSilent()) {
            level().playLocalSound(getX() + 0.5, getY() + 0.5, getZ() + 0.5, SoundEvents.BLAZE_BURN, getSoundSource(), 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
        level().addParticle(ParticleTypes.FLAME, getRandomX(0.5), getRandomY(), getRandomZ(0.5), 0.0, 0.02, 0.0);
    }
}
