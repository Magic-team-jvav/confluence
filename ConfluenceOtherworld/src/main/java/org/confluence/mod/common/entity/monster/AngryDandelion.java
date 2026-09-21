package org.confluence.mod.common.entity.monster;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.projectile.DandelionSeed;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.Comparator;

public final class AngryDandelion extends BaseMonster {
    private static final double ATTACK_RANGE = 31.25;
    private static final double SEED_SPEED = 0.35;

    public AngryDandelion(EntityType<? extends AngryDandelion> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean hasEntityContactAttack() {
        return true;
    }

    private boolean canShootAt(LivingEntity target) {
        if (!target.isAlive() || !canAttack(target)) return false;
        // Minecraft 使用三维攻击距离，不再限制顺风半区或狭窄的水平攻击带。
        return distanceToSqr(target) <= ATTACK_RANGE * ATTACK_RANGE;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new BTNode() {
                    private int cooldown = 14;

                    @Override
                    public BTStatus execute() {
                        getNavigation().stop();
                        setDeltaMovement(0.0, getDeltaMovement().y, 0.0);
                        if (cooldown > 0) cooldown--;
                        LivingEntity target = getTarget();
                        if (target == null || !canShootAt(target)) {
                            target = ((ServerLevel) level()).players().stream()
                                    .filter(player -> !player.isCreative() && !player.isSpectator() && canShootAt(player))
                                    .min(Comparator.comparingDouble(player -> distanceToSqr(player))).orElse(null);
                        }
                        if (target == null) return BTStatus.RUNNING;
                        Vec3 origin = getEyePosition();
                        Vec3 direction = target.getBoundingBox().getCenter().subtract(origin);
                        faceCombatDirection(direction, 10.0F, 10.0F);
                        if (cooldown > 0 || !getSensing().hasLineOfSight(target))
                            return BTStatus.RUNNING;
                        cooldown = 14;
                        double flightTicks = Math.max(1.0, direction.length() / SEED_SPEED);
                        // 种子每刻会下坠，瞄准时补偿重力，避免远距离目标仍然打不到。
                        Vec3 aimedVelocity = direction.scale(1.0 / flightTicks)
                                .add(0, DandelionSeed.GRAVITY * (flightTicks + 1.0) * 0.5, 0);
                        Vec3 sideways = new Vec3(-direction.z, 0.0, direction.x).normalize();
                        float damage = LibUtils.isMaster(level(), blockPosition()) ? 42.0F : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 28.0F : 14.0F;
                        damage *= (float) getAttributeValue(Attributes.ATTACK_DAMAGE) / 15.0F;
                        boolean fired = false;
                        int count = 1 + random.nextInt(3);
                        for (int i = 0; i < count; i++) {
                            DandelionSeed seed = ModEntities.DANDELION_SEED.get().create(level());
                            if (seed == null) continue;
                            Vec3 velocity = aimedVelocity.add(sideways.scale((random.nextDouble() - 0.5) * 0.12));
                            seed.configure(AngryDandelion.this, origin, velocity, damage, 100);
                            if (level().addFreshEntity(seed)) fired = true;
                            else seed.discard();
                        }
                        if (fired) playSound(SoundEvents.SNOW_GOLEM_SHOOT, 0.5F, 1.4F);
                        return BTStatus.RUNNING;
                    }
                };
            }
        };
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
