package org.confluence.mod.common.summon.flying;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

/// 幽匿飞灵召唤物的运行实例。
public final class SculkWispSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 7.0F;
    private int attackCooldown;
    private int castTicks;
    private LivingEntity delayedTarget;

    public SculkWispSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("sculk_wisp"), owner, slotCost, stats, initialPose, 1.0, 1.0);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new MomentumSummonIdleGoal<>(this, 1.8, 0.035, 0.70));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 64.0);
    }

    @Override
    protected void beforeGoalTick() {
        if (castTicks > 0 && --castTicks == 0) {
            LivingEntity target = delayedTarget;
            delayedTarget = null;
            if (target != null) {
                sonicBoom(target);
            }
        }
    }

    private void combat(LivingEntity target) {
        hoverNear(targetBasePosition(), targetPosition(), 5.0, 3.0, 5.0, 0.0525, 0.03, 1.05);
        if (--attackCooldown <= 0) {
            attackCooldown = 30;
            castTicks = 19;
            delayedTarget = target;
        }
    }

    private void sonicBoom(LivingEntity target) {
        Vec3 origin = eyePosition();
        Vec3 offset = target.getEyePosition().subtract(origin);
        if (offset.lengthSqr() < 1.0E-6) {
            return;
        }
        Vec3 direction = offset.normalize();
        ServerLevel level = owner().serverLevel();
        for (int index = 1; index < (int) Math.floor(offset.length()) + 7; index++) {
            Vec3 particle = origin.add(direction.scale(index));
            level.sendParticles(ParticleTypes.SONIC_BOOM, particle.x, particle.y, particle.z, 1, 0.0, 0.0, 0.0, 0.0);
        }
        level.playSound(null, origin.x, origin.y, origin.z, SoundEvents.WARDEN_SONIC_BOOM, net.minecraft.sounds.SoundSource.NEUTRAL, 3.0F, 1.0F);
        if (hurtTarget(target, 1.0F)) {
            double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            target.push(direction.x * 2.5 * (1.0 - resistance), direction.y * 0.5 * (1.0 - resistance), direction.z * 2.5 * (1.0 - resistance));
        }
    }

    private Vec3 eyePosition() {
        return position().add(0.0, 0.85, 0.0);
    }

    @Override
    public SummonVisualState visualState() {
        return castTicks > 0
                ? new SummonVisualState(false, SummonAnimation.MELEE_ATTACK, 20 - castTicks, 20, 0.0F, 1.0F, 1.0F)
                : SummonVisualState.DEFAULT;
    }

    private static final class AttackGoal extends SummonGoal<SculkWispSummon> {
        private AttackGoal(SculkWispSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return summon.target() != null;
        }

        @Override
        public void tick() {
            summon.combat(summon.target());
        }
    }

}
