package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.monster.BaseCasterMonster;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;

import java.util.Objects;
import java.util.function.Function;

/// 执行“预施法、延迟释放、循环传送”的完整法师战斗周期。
///
/// 阶段计数集中管理倒计时语义，避免把挥手时刻、弹幕释放时刻和
/// 传送时刻拆成多个互相产生一 tick 偏差的顺序节点。节点只负责公共时序；
/// 弹幕种类、伤害和命中特效仍由具体实体提供的工厂决定。
public final class CasterCycleAction extends BTNode {
    private static final AttributeModifier BATTLE_RANGE = new PortAttributeModifier(Confluence.asResource("caster_battle_range"), 1.0, PortAttributeModifier.Operation.ADD_MULTIPLIED_BASE).unwrap();
    public static final Timing DEFAULT_TIMING = new Timing(1, 200, 20, 50, 8, 83);
    private final BaseCasterMonster caster;
    private final Function<LivingEntity, @Nullable Projectile> projectileFactory;
    private final TeleportNearTargetAction teleportAction;
    private final HurtResponse hurtResponse;
    private final Timing timing;
    private final int castsPerCycle;
    private final int projectilesPerVolley;
    private final int projectileIntervalTicks;
    private int phase;
    private int lastCastPhase;
    private int releaseDelay = -1;
    private int projectilesRemaining;
    private boolean castStarted;
    private int hurtPauseTicks;
    private int initialTeleportTicks;
    private boolean needsInitialTeleport = true;

    public CasterCycleAction(BaseCasterMonster caster, Function<LivingEntity, @Nullable Projectile> projectileFactory) {
        this(caster, projectileFactory, HurtResponse.CONTINUE_CYCLE, DEFAULT_TIMING, 3, 1, 1);
    }

    public CasterCycleAction(BaseCasterMonster caster, Function<LivingEntity, @Nullable Projectile> projectileFactory, HurtResponse hurtResponse) {
        this(caster, projectileFactory, hurtResponse, DEFAULT_TIMING, 3, 1, 1);
    }

    public CasterCycleAction(BaseCasterMonster caster, Function<LivingEntity, @Nullable Projectile> projectileFactory,
                             HurtResponse hurtResponse, Timing timing, int castsPerCycle, int projectilesPerVolley, int projectileIntervalTicks) {
        timing = Objects.requireNonNull(timing, "timing");
        if (castsPerCycle <= 0 || timing.firstCastPhase() - (castsPerCycle - 1) * timing.castIntervalTicks() <= 0
                || projectilesPerVolley <= 0 || projectileIntervalTicks <= 0) {
            throw new IllegalArgumentException("Cast count, volley size and projectile interval must be positive and fit within the cycle");
        }
        this.caster = Objects.requireNonNull(caster, "caster");
        this.projectileFactory = Objects.requireNonNull(projectileFactory, "projectileFactory");
        this.hurtResponse = Objects.requireNonNull(hurtResponse, "hurtResponse");
        this.timing = timing;
        this.castsPerCycle = castsPerCycle;
        this.projectilesPerVolley = projectilesPerVolley;
        this.projectileIntervalTicks = projectileIntervalTicks;
        this.teleportAction = new TeleportNearTargetAction(caster, 20, 5, 4);
        resetCycleState();
    }

    @Override
    public BTStatus execute() {
        LivingEntity target = caster.getTarget();
        if (target == null || !target.isAlive() || !caster.canAttack(target)) {
            removeBattleRange();
            phase = timing.cycleTicks();
            lastCastPhase = phase;
            cancelCast();
            hurtPauseTicks = 0;
            initialTeleportTicks = timing.initialTeleportTicks();
            needsInitialTeleport = true;
            return BTStatus.FAILURE;
        }

        addBattleRange();
        faceTarget(target);
        caster.getNavigation().stop();
        if (hurtPauseTicks > 0) {
            if (--hurtPauseTicks == 0) {
                teleportAction.start();
                teleportAction.execute();
                phase = timing.cycleTicks();
            }
            return BTStatus.RUNNING;
        }
        if (needsInitialTeleport) {
            if (--initialTeleportTicks > 0) return BTStatus.RUNNING;
            teleportAction.start();
            teleportAction.execute();
            needsInitialTeleport = false;
        }

        if (releaseDelay > 0 && --releaseDelay == 0) {
            Projectile projectile = projectileFactory.apply(target);
            if (projectile == null || !caster.level().addFreshEntity(projectile)) {
                if (projectile != null) projectile.discard();
                cancelCast();
                return BTStatus.FAILURE;
            }
            caster.playCastReleaseSound();
            if (--projectilesRemaining > 0) releaseDelay = projectileIntervalTicks;
        }
        if (isCastPhase(phase)) {
            lastCastPhase = phase;
            releaseDelay = timing.releaseDelayTicks();
            projectilesRemaining = projectilesPerVolley;
            castStarted = true;
            caster.beginCastAnimation();
        }

        if (--phase <= 0) {
            phase = timing.cycleTicks();
            teleportAction.start();
            teleportAction.execute();
        }
        return BTStatus.RUNNING;
    }

    private void faceTarget(LivingEntity target) {
        caster.faceCombatPosition(target.getEyePosition(), 180.0F, 180.0F);
    }

    @Override
    public void stop() {
        removeBattleRange();
        cancelCast();
    }

    /// 按具体法师的受击响应中断当前施法；共用的施法节奏数值保持不变。
    public void interruptAfterHurt() {
        if (hurtResponse == HurtResponse.IGNORE) return;
        if (hurtResponse == HurtResponse.CONTINUE_CYCLE) {
            phase = lastCastPhase - 1;
            return;
        }
        cancelCast();
        needsInitialTeleport = false;
        if (hurtResponse == HurtResponse.TELEPORT_IMMEDIATELY) {
            teleportAction.start();
            teleportAction.execute();
            phase = timing.cycleTicks();
            hurtPauseTicks = 0;
            return;
        }
        hurtPauseTicks = timing.hurtPauseTicks();
    }

    private boolean isCastPhase(int value) {
        int elapsed = timing.firstCastPhase() - value;
        return elapsed >= 0 && elapsed % timing.castIntervalTicks() == 0
                && elapsed / timing.castIntervalTicks() < castsPerCycle;
    }

    private void cancelCast() {
        releaseDelay = -1;
        projectilesRemaining = 0;
        if (castStarted) {
            caster.cancelCastAnimation();
            castStarted = false;
        }
    }

    private void resetCycleState() {
        phase = timing.cycleTicks();
        lastCastPhase = phase;
        initialTeleportTicks = timing.initialTeleportTicks();
    }

    private void addBattleRange() {
        AttributeInstance followRange = caster.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getModifier(BATTLE_RANGE.getId()) == null) {
            followRange.addTransientModifier(BATTLE_RANGE);
        }
    }

    private void removeBattleRange() {
        AttributeInstance followRange = caster.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRange != null && followRange.getModifier(BATTLE_RANGE.getId()) != null) {
            followRange.removeModifier(BATTLE_RANGE.getId());
        }
    }

    public enum HurtResponse {
        CONTINUE_CYCLE,
        PAUSE_THEN_TELEPORT,
        TELEPORT_IMMEDIATELY,
        IGNORE
    }

    /// 保存不同法师共享状态机中的时序差异，单位均为游戏刻。
    public record Timing(int initialTeleportTicks, int cycleTicks, int firstCastDelayTicks,
                         int castIntervalTicks, int releaseDelayTicks, int hurtPauseTicks) {
        public Timing {
            if (initialTeleportTicks <= 0 || cycleTicks <= 0 || firstCastDelayTicks <= 0
                    || firstCastDelayTicks >= cycleTicks || castIntervalTicks <= 0
                    || releaseDelayTicks <= 0 || hurtPauseTicks <= 0) {
                throw new IllegalArgumentException("Caster timing values must be positive and fit within the cycle");
            }
        }

        int firstCastPhase() {
            return cycleTicks - firstCastDelayTicks;
        }
    }
}
