package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.mod.common.entity.ai.BossMinionCoordinator;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.ModSoundEvents;

/// 腐化史莱姆使用普通史莱姆行为，死亡时分裂出二至三个史莱姆灵。
public class CorruptSlime extends BaseSlime {

    public CorruptSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && !isRemoved() && isDeadOrDying()) {
            int count = 2 + random.nextInt(2);
            float spread = getBbWidth() * 0.3F;
            for (int i = 0; i < count; i++) {
                BaseSlime child = MonsterEntities.SLIMELING.get().create(level());
                if (child == null) continue;
                if (isPersistenceRequired()) child.setPersistenceRequired();
                if (hasCustomName()) child.setCustomName(getCustomName());
                child.setNoAi(isNoAi());
                child.setInvulnerable(isInvulnerable());
                double angle = Math.PI * 2.0 * i / count;
                child.moveTo(getX() + Math.cos(angle) * spread, getY() + getBbHeight() * 0.25F,
                        getZ() + Math.sin(angle) * spread, random.nextFloat() * 360.0F, 0.0F);
                LivingEntity target = getTarget();
                if (target != null && target.isAlive()) {
                    child.setTarget(target);
                    BossMinionCoordinator.faceTargetImmediately(child, target);
                }
                if (!level().addFreshEntity(child)) child.discard();
            }
        }
        super.remove(reason);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ROUTINE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ROUTINE_DEATH.get();
    }

}
