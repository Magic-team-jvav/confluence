package org.confluence.mod.common.item.flail;

import org.mesdag.particlestorm.ParticleStorm;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.confluence.mod.common.entity.projectile.Flail.FlaironBubbleProjectile;
import org.confluence.mod.common.entity.projectile.Flail.FlowerProjectile;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.network.s2c.GuardianFlailBeamPacketS2C;
import org.confluence.lib.util.LibUtils;
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
                            && LibUtils.canHitEntity(flail, e));
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
                            && LibUtils.canHitEntity(flail, e));

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

    /**
     * <h1>猪鲨链球攻击策略</h1>
     * SPIN/THROWN/RETRACT 阶段每秒发射 15 枚追踪气泡，
     * STAY 阶段每秒发射 10 枚。气泡从连枷面朝方向的 30° 锥角内随机射出。
     * <p>
     * 射弹造成武器面板 50% 伤害，边长 [0.5, 0.8] 格，命中敌怪后消失。
     */
    final class FlaironAttackStrategy implements FlailStrategy {
        /** 每 tick +1 的循环计数器，用于 1-1-2 发射节奏 */
        private int tickCounter;

        /** 60° 锥角半角（弧度） */
        private static final double CONE_HALF_ANGLE = Math.toRadians(30);

        @Override
        public void onSpinTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, true, false);
        }

        @Override
        public void onThrownTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                 @NotNull FlailComponent component) {
            tryShoot(flail, player, component, true, false);
        }

        @Override
        public void onRetractTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                  @NotNull FlailComponent component) {
            tryShoot(flail, player, component, true, true);
        }

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            tryShoot(flail, player, component, false, false);
        }

        @Override
        public void onDiscard(@NotNull BaseFlailEntity flail, @NotNull Player player,
                              @NotNull FlailComponent component) {
            tickCounter = 0;
        }

        /**
         * @param active true=SPIN/THROWN/RETRACT (1-1-2 节奏, 15/秒),
         *               false=STAY (每 2 tick, 10/秒)
         * @param invert true=RETRACT 时反转发射方向
         */
        private void tryShoot(BaseFlailEntity flail, Player player, FlailComponent component,
                              boolean active, boolean invert) {
            boolean shouldShoot;
            if (active) {
                // 1-1-2 节奏: tick 0,1,2 发射, tick 2 跳过, 循环
                shouldShoot = tickCounter % 4 != 2;
            } else {
                // 每 2 tick: tick 0 发射, tick 1 跳过
                shouldShoot = tickCounter % 2 == 0;
            }
            tickCounter++;

            if (!shouldShoot) return;

            Level level = flail.level();
            if (level.isClientSide()) return;

            float baseDamage = (float) (player.getAttributeValue(LibAttributes.getAttackDamage()));
            float bubbleDamage = baseDamage * 0.5f;

            // 连枷面朝方向（由 faceDirection 设置的 yaw/pitch 反算）
            float yaw = (float) Math.toRadians(flail.getYRot());
            float pitch = (float) Math.toRadians(flail.getXRot());
            double cosPitch = Math.cos(pitch);
            Vec3 facing = new Vec3(
                    -Math.sin(yaw) * cosPitch,
                    -Math.sin(pitch),
                    Math.cos(yaw) * cosPitch
            ).normalize();

            // 30° 锥角内随机方向
            Vec3 dir = randomInCone(level, facing);

            // RETRACT 时反转方向（面朝玩家 → 背离玩家）
            if (invert) {
                dir = dir.scale(-1);
            }

            // 随机初速 [2格/秒, 5格/秒] = [0.1, 0.25] 格/tick
            double speed = 0.1 + level.random.nextDouble() * 0.15;
            Vec3 velocity = dir.scale(speed);

            FlaironBubbleProjectile bubble = new FlaironBubbleProjectile(
                    ModEntities.FLAIRON_BUBBLE.get(), level, flail, player, velocity);
            bubble.setPos(flail.position().add(0, flail.getBbHeight() * 0.5, 0));
            bubble.setBaseDamage(bubbleDamage);
            level.addFreshEntity(bubble);
        }

        /** 在给定方向的 30° 锥角内生成随机单位向量 */
        @NotNull
        private static Vec3 randomInCone(Level level, Vec3 axis) {
            double theta = level.random.nextDouble() * CONE_HALF_ANGLE;
            double phi = level.random.nextDouble() * 2.0 * Math.PI;
            double sinTheta = Math.sin(theta);
            double cosTheta = Math.cos(theta);

            // 构建正交基
            Vec3 arb = Math.abs(axis.x) < 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
            Vec3 right = axis.cross(arb).normalize();
            Vec3 up = axis.cross(right);

            return axis.scale(cosTheta)
                    .add(right.scale(sinTheta * Math.cos(phi)))
                    .add(up.scale(sinTheta * Math.sin(phi)));
        }
    }

    /**
     * <h1>锚攻击策略</h1>
     * 触碰方块进入收回状态时，若飞行时间超过 4 tick，
     * 对锚周围 4×4×1 区域内的敌怪造成 60% 武器伤害。
     */
    final class AnchorAttackStrategy implements FlailStrategy {

        @Override
        public void onThrownToRetract(@NotNull BaseFlailEntity flail, @NotNull Player player,
                                      @NotNull FlailComponent component) {
            Level level = flail.level();
            if (level.isClientSide()) return;

            // 飞行时间需超过 4 tick（0.2s）
            if (flail.tickCount <= 4) return;

            float baseDamage = (float) Math.max(1.0F, player.getAttributeValue(Attributes.ATTACK_DAMAGE));
            float aoeDamage = baseDamage * 0.6f;
            DamageSource source = ModDamageTypes.of(level, ModDamageTypes.SWORD_PROJECTILE, flail, player);

            // 横向 4 格、纵向 4 格、高 1 格的方形区域
            AABB aoe = flail.getBoundingBox().inflate(2.0, 0.5, 2.0);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, aoe,
                    e -> e != player && e.isAlive() && LibUtils.canHitEntity(flail, e));

            for (LivingEntity target : targets) {
                target.hurt(source, aoeDamage);
            }

            // 撞击音效
            level.playSound(null, flail.getX(), flail.getY(), flail.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8F, 0.9F + level.random.nextFloat() * 0.2F);

            // 方块破碎粒子
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, net.minecraft.world.level.block.Blocks.COBBLESTONE.defaultBlockState()),
                        flail.getX(), flail.getY() + 0.5, flail.getZ(),
                        60, 4, 0.5, 4, 0.2);
            }
        }
    }
}
