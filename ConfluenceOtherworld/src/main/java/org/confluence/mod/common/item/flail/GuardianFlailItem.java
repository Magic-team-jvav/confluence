package org.confluence.mod.common.item.flail;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.entity.flail.FlailAttackStrategy;
import org.confluence.mod.network.s2c.GuardianFlailBeamPacketS2C;
import org.confluence.terraentity.utils.TEUtils;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <h1>守卫链球物品</h1>
 * 连枷 STAY 阶段向周围敌人发射守卫者激光，锁定 1 个目标
 * <p>
 * 内嵌 {@link GuardianAttackStrategy} 实现完整激光状态机
 */
public class GuardianFlailItem extends BaseFlailItem {

    public GuardianFlailItem(@NotNull FlailComponent flailComponent, @NotNull ModRarity rarity) {
        super(flailComponent, rarity);
    }

    @Override
    @NotNull
    public FlailAttackStrategy getAttackStrategy() {
        return new GuardianAttackStrategy(false);
    }
    /**
     * <h1>状态机实现</h1>
     * <p>
     * 内含{@link onStayTick}、{@link onDiscard}、{@link canUse}、{@link start}、{@link canContinueToUse}
     * 激光伤害是链球的六分之一
     */
    public static final class GuardianAttackStrategy implements FlailAttackStrategy {
private int attackTime;
        private final List<LivingEntity> targets = new ArrayList<>(3);
        private boolean active;
        private final boolean elder;
        private int syncTimer;

        public GuardianAttackStrategy(boolean elder) {
            this.elder = elder;}

        // ── 参数 ──

        private int maxTargets() { return elder ? 3 : 1; }
        private float range() { return elder ? 20.0F : 15.0F; }
        private int attackInterval() { return 40; }
        private float damage(Player player, FlailComponent component) {
            return (float) (player.getAttributeValue(org.confluence.lib.common.LibAttributes.getAttackDamage()))
                    / 6.0F;
        }

        // ── 回调 ──

        @Override
        public void onStayTick(@NotNull BaseFlailEntity flail, @NotNull Player player,
                               @NotNull FlailComponent component) {
            Level level = flail.level();
            if (level.isClientSide()) return;

            int phase = flail.getPhase();
            if (!active) {
                if (canUse(flail, player, level)) {start(flail, level);
                }
            } else {
                if (canContinueToUse(flail, level)) {
                    tick(flail, player, level, component);
                } else {stop(flail, level);
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

        // ── 状态机 ──
        private boolean canUse(BaseFlailEntity flail, Player player, Level level) {
            double range = range();
            AABB searchBox = flail.getBoundingBox().inflate(range);
            List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                    e -> e != player
                            && e.isAlive()
                            && canFlailSee(flail, level, e)
                            && TEUtils.projectileCanHurtEntityTest.test(flail, e));if (candidates.isEmpty()) return false;

            candidates.sort(Comparator.comparingDouble(e -> e.distanceToSqr(flail)));
            int count = Math.min(maxTargets(), candidates.size());
            targets.clear();
            for (int i = 0; i < count; i++) {
                LivingEntity t = candidates.get(i);
                targets.add(t);}
            return true;
        }

        /** 激活：重置计时器，同步目标到客户端 */
        private void start(BaseFlailEntity flail, Level level) {
            this.active = true;
            this.attackTime = 0;
            this.syncTimer = 0;syncTargets(flail, level);
        }

        /** tick：计时器递增，到间隔时造成伤害 + 同步 */
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

        /** 持续判定*/
        private boolean canContinueToUse(BaseFlailEntity flail, Level level) {
            double rangeSqr = range() * range();
            targets.removeIf(t ->
                    !t.isAlive()
                            || t.distanceToSqr(flail) > rangeSqr
                            || !canFlailSee(flail, level, t));
            return !targets.isEmpty();
        }

        /** 停止：重置状态，发送清空包 */
        private void stop(BaseFlailEntity flail, Level level) {
            active = false;
            attackTime = 0;
            targets.clear();
            if (level instanceof ServerLevel serverLevel) {
                GuardianFlailBeamPacketS2C.sendClear(serverLevel, flail.getId(), elder);
            }
        }

        /** 将当前ID 列表同步到客户端 */
        private void syncTargets(BaseFlailEntity flail, Level level) {
            if (level instanceof ServerLevel serverLevel) {
                int[] ids = targets.stream().mapToInt(Entity::getId).toArray();GuardianFlailBeamPacketS2C.send(serverLevel, flail.getId(), ids, elder);
            }
        }

        /**
         * 从连枷位置到目标实体进行视线检测
         */
        private static boolean canFlailSee(BaseFlailEntity flail, Level level, Entity target) {
            Vec3 from = flail.position().add(0, 0.25, 0);
            Vec3 to = target.getBoundingBox().getCenter();
            ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, flail);
            return level.clip(ctx).getType() == HitResult.Type.MISS;
        }
    }
}
