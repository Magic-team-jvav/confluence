package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 保留噬魂怪冲撞方式，并在追击期间周期性发射魔唾液的腐化者。
public final class Corruptor extends EaterOfSouls {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final int DEFAULT_SHOT_COOLDOWN = 45;
    private static final double DEFAULT_SHOT_DAMAGE_MULTIPLIER = 0.8;
    private int shotCooldown = DEFAULT_SHOT_COOLDOWN;

    public Corruptor(EntityType<? extends Corruptor> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive()) return;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !hasLineOfSight(target)) return;
        if (--shotCooldown > 0) return;
        shotCooldown = creatureDefinition().behavior().shotCooldownOr(DEFAULT_SHOT_COOLDOWN);
        HostileParticleProjectile projectile = ModEntities.VILE_SPIT_PROJECTILE.get().create(level());
        if (projectile == null) return;
        double multiplier = creatureDefinition().behavior().shotMultiplierOr(DEFAULT_SHOT_DAMAGE_MULTIPLIER);
        projectile.configure(this, target, (float) (getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier));
        if (!level().addFreshEntity(projectile)) projectile.discard();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Movement", 0, state -> state.setAndContinue(IDLE)));
    }

    /// 腐化者受击后重新计算下一次吐息，连续攻击可以打断它的远程攻击节奏。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted)
            shotCooldown = creatureDefinition().behavior().shotCooldownOr(DEFAULT_SHOT_COOLDOWN);
        return accepted;
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
