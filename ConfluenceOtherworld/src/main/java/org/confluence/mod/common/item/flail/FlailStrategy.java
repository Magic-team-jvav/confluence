package org.confluence.mod.common.item.flail;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.projectile.Flail.DripplerCripplerProjectile;
import org.confluence.mod.common.entity.projectile.Flail.FlowerProjectile;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.network.s2c.GuardianFlailBeamPacketS2C;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <h1>连枷攻击策略</h1>
 * 定义连枷在五阶段状态机 {@code SPIN→THROWN→STAY→RETRACT} 中各节点的攻击行为回调，
 * 以及三种内置实现：守卫激光、花之力花瓣、滴滴怪弹射。
 *
 * @see BaseFlailEntity
 */
public interface FlailStrategy {

    /** 空策略全局单例，所有回调均为空操作，适用于无需额外攻击行为的普通连枷 */
    FlailStrategy NULL = new FlailStrategy() {};

    // SPIN 阶段每 tick 调用。
    default void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // 从 SPIN 切换到 THROWN 时调用一次。
    default void onLaunch(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // THROWN 阶段每 tick 调用。
    default void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // STAY 阶段每 tick 调用。
    default void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // THROWN 切换到 RETRACT 时调用一次。
    default void onThrownToRetract(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // RETRACT 阶段每 tick 调用。
    default void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // 连枷碰撞命中实体时调用（在伤害计算之后）。
    default void onHitEntity(@NotNull BaseFlailEntity flail, @NotNull Player player,
                             @NotNull FlailComponent component, @NotNull LivingEntity target) {}

    // 连枷被丢弃/移除时调用。
    default void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player, @NotNull FlailComponent component) {}

    // ─────────────────────────────────────────────
    // 内置策略实现
    // ─────────────────────────────────────────────

    /**
     * <h1>守卫连枷攻击策略</h1>
     * 连枷 STAY 阶段向周围敌人发射守卫者激光。
     */
    final class GuardianAttackStrategy implements FlailStrategy {
        private int attackTime;
        private final List<LivingEntity> targets = new ArrayList<>(3);
        private boolean active;
        private final boolean elder;
        private int syncTimer;

        public GuardianAttackStrategy(boolean elder) {
            this.elder = elder;
        }

        private int maxTargets() { return elder ? 3 : 1; }
        private float range() { return elder ? 20.0F : 15.0F; }
        private int attackInterval() { return 40; }

        private float damage(Player player, FlailComponent component) {
            return (float) (player.getAttributeValue(LibAttributes.getAttackDamage())) / 6.0F;
        }

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            Level level = flail.level();
            if (level.isClientSide()) return;

            if (!active) {
                if (canUse(flail, player, level)) {
                    start(flail, level);
                }
            } else {
                if (canContinueToUse(flail, level)) {
                    tick(flail, player, level, component);
                } else {
                    stop(flail, level);
                }
            }
        }

        @Override
        public void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player,
                              @NotNull FlailComponent component) {
            Level level = flail.level();
            if (!level.isClientSide() && active) {
                stop(flail, level);
            }
        }

        private boolean canUse(BaseFlailEntity flail, Player player, Level level) {
            double range = range();
            AABB searchBox = flail.getBoundingBox().inflate(range);
            List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player
                            && e.isAlive()
                            && canFlailSee(flail, level, e)
                            && TEUtils.projectileCanHurtEntityTest.test(flail, e));
            if (candidates.isEmpty()) return false;

            candidates.sort(Comparator.comparingDouble(e -> e.distanceToSqr(flail)));
            int count = Math.min(maxTargets(), candidates.size());
            targets.clear();
            for (int i = 0; i < count; i++) {
                targets.add(candidates.get(i));
            }
            return true;
        }

        private void start(BaseFlailEntity flail, Level level) {
            this.active = true;
            this.attackTime = 0;
            this.syncTimer = 0;
            syncTargets(flail, level);
        }

        private void tick(BaseFlailEntity flail, Player player, Level level, FlailComponent component) {
            attackTime++;
            if (attackTime % attackInterval() == 0) {
                float dmg = damage(player, component);
                for (LivingEntity target : targets) {
                    if (target.isAlive() && canFlailSee(flail, level, target)) {
                        target.hurt(level.damageSources().mobAttack(player), dmg);
                        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                                SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 1.0F, 1.0F);
                    }
                }
            }
            if (++syncTimer >= 5) {
                syncTimer = 0;
                syncTargets(flail, level);
            }
        }

        private boolean canContinueToUse(BaseFlailEntity flail, Level level) {
            double rangeSqr = range() * range();
            targets.removeIf(t ->
                    !t.isAlive()
                            || t.distanceToSqr(flail) > rangeSqr
                            || !canFlailSee(flail, level, t));
            return !targets.isEmpty();
        }

        private void stop(BaseFlailEntity flail, Level level) {
            active = false;
            attackTime = 0;
            targets.clear();
            if (level instanceof ServerLevel serverLevel) {
                GuardianFlailBeamPacketS2C.sendClear(serverLevel, flail.getId(), elder);
            }
        }

        private void syncTargets(BaseFlailEntity flail, Level level) {
            if (level instanceof ServerLevel serverLevel) {
                int[] ids = targets.stream().mapToInt(Entity::getId).toArray();
                GuardianFlailBeamPacketS2C.send(serverLevel, flail.getId(), ids, elder);
            }
        }

        private static boolean canFlailSee(BaseFlailEntity flail, Level level, Entity target) {
            Vec3 from = flail.position().add(0, 0.25, 0);
            Vec3 to = target.getBoundingBox().getCenter();
            ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, flail);
            return level.clip(ctx).getType() == HitResult.Type.MISS;
        }
    }

    /**
     * <h1>花之力攻击策略</h1>
     * SPIN/THROWN/RETRACT 每 10 tick、STAY 每 5 tick 向索敌范围内最近的生物发射花瓣。
     * <p>
     * 索敌半径 = 连枷最大距离。花瓣造成玩家攻击力的 1/3。
     */
    final class FlowerAttackStrategy implements FlailStrategy {
        private int shootTimer;

        @Override
        public void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                 @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                  @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 10);
        }

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, 5);
        }

        @Override
        public void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player,
                              @NotNull FlailComponent component) {
            shootTimer = 0;
        }

        private void tryShoot(BaseFlailEntity flail, Player player, FlailComponent component, int interval) {
            if (--shootTimer > 0) return;
            shootTimer = interval;

            Level level = flail.level();
            if (level.isClientSide()) return;

            float maxDist = component.maxDistance;
            AABB searchBox = flail.getBoundingBox().inflate(maxDist);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player && e.isAlive()
                            && TEUtils.projectileCanHurtEntityTest.test(flail, e));

            LivingEntity nearest = targets.stream()
                    .min(Comparator.comparingDouble(e -> e.distanceToSqr(flail)))
                    .orElse(null);
            if (nearest == null) return;

            Vec3 direction = nearest.getBoundingBox().getCenter()
                    .subtract(flail.position()).normalize();
            Vec3 velocity = direction.scale(component.throwSpeed);

            float baseDamage = (float) (player.getAttributeValue(LibAttributes.getAttackDamage()));
            float petalDamage = baseDamage / 3.0f;

            FlowerProjectile petal = new FlowerProjectile(
                    ModEntities.FLOWER_PROJECTILE.get(), level, flail, player, velocity);
            petal.setPos(flail.position());
            petal.setBaseDamage(petalDamage);
            petal.setMaxLifetime(100);
            level.addFreshEntity(petal);
        }
    }

    /**
     * <h1>滴滴怪致残者攻击策略</h1>
     * 在 THROWN→RETRACT 转换时，沿连枷当前运动方向发射一枚受重力影响的可弹跳射弹。
     * <p>
     * 射弹造成武器面板 50% 伤害，最多在方块上弹射 2 次或击中 2 个敌怪后消失。
     */
    final class DripplerCripplerAttackStrategy implements FlailStrategy {

        @Override
        public void onThrownToRetract(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                      @NotNull FlailComponent component) {
            Level level = flail.level();
            if (level.isClientSide()) return;

            Vec3 velocity = flail.getDeltaMovement().normalize().scale(component.throwSpeed);

            float baseDamage = (float) (player.getAttributeValue(LibAttributes.getAttackDamage()));
            float projectileDamage = baseDamage * 0.5f;

            DripplerCripplerProjectile projectile = new DripplerCripplerProjectile(
                    ModEntities.DRIPPLER_CRIPPLER_PROJECTILE.get(), level, flail, player, velocity);
            projectile.setPos(flail.position());
            projectile.setBaseDamage(projectileDamage);
            projectile.setMaxLifetime(200);
            level.addFreshEntity(projectile);
        }
    }
}
