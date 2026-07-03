package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.DashAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;

public class BrainOfCthulhu extends BaseBoss {
    private static final int CREEPER_COUNT = 20;
    private static final int PHASE1_TELEPORT_TICKS = 80;
    private static final int PHASE2_TELEPORT_TICKS = 40;
    private int teleportTimer = PHASE1_TELEPORT_TICKS;
    private int aliveCreepers = 0;
    private boolean phase2 = false;
    private boolean creepersSpawned = false;

    public BrainOfCthulhu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 1100;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.5)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PINK;
    }

    // === BT ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Phase 2: aggressive attacks
                        SequenceNode.of(
                                new PhaseCondition(BrainOfCthulhu.this),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(BrainOfCthulhu.this),
                                                new DashAction(BrainOfCthulhu.this, 1.2, 15)),
                                        SequenceNode.of(new HasTargetCondition(BrainOfCthulhu.this),
                                                new ChargeAttackAction(BrainOfCthulhu.this, 0.8)),
                                        SequenceNode.of(new WaitAction(10),
                                                new FlyWanderAction(BrainOfCthulhu.this, 0.4, 8))
                                )
                        ),
                        // Phase 1: passive, teleports
                        new WaitAction(10)
                );
            }
        };
    }

    private static class PhaseCondition extends BTNode {
        private final BrainOfCthulhu boss;
        PhaseCondition(BrainOfCthulhu boss) { this.boss = boss; }
        @Override
        public BTStatus execute() { return boss.phase2 ? BTStatus.SUCCESS : BTStatus.FAILURE; }
    }

    // === Goals ===

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    // === Tick ===

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // Spawn creepers on first tick
            if (!creepersSpawned) {
                spawnCreepers();
                creepersSpawned = true;
            }

            // Count alive creepers
            aliveCreepers = level().getEntities(this, getBoundingBox().inflate(64),
                    e -> e instanceof CreeperOfCthulhu && e.isAlive()).size();

            // Phase transition
            if (!phase2 && aliveCreepers == 0) {
                phase2 = true;
                teleportTimer = PHASE2_TELEPORT_TICKS;
                setHealth(getMaxHealth()); // full heal on phase transition
            }

            // Target finding
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Teleport logic
            teleportTimer--;
            if (teleportTimer <= 0) {
                teleportTimer = phase2 ? PHASE2_TELEPORT_TICKS : PHASE1_TELEPORT_TICKS;
                doTeleport();
            }
        }
    }

    private void doTeleport() {
        if (getTarget() == null || !(level() instanceof ServerLevel serverLevel)) return;
        float angle = random.nextFloat() * Mth.TWO_PI;
        double dist = 5 + random.nextFloat() * 5;
        double tx = getTarget().getX() + Math.cos(angle) * dist;
        double tz = getTarget().getZ() + Math.sin(angle) * dist;
        int ty = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new net.minecraft.core.BlockPos((int) tx, 0, (int) tz)).getY();
        teleportTo(tx, ty + 4 + random.nextFloat() * 4, tz);

        // Teleport creepers with the brain
        for (Entity e : level().getEntities(this, getBoundingBox().inflate(64),
                e -> e instanceof CreeperOfCthulhu && e.isAlive())) {
            double ca = random.nextFloat() * Mth.TWO_PI;
            double cd = 2 + random.nextFloat() * 3;
            e.teleportTo(tx + Math.cos(ca) * cd, ty + 4 + random.nextFloat() * 3, tz + Math.sin(ca) * cd);
        }
    }

    private void spawnCreepers() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        for (int i = 0; i < CREEPER_COUNT; i++) {
            CreeperOfCthulhu creeper = BossEntities.CREEPER_OF_CTHULHU.get().create(level());
            if (creeper != null) {
                double angle = i * Math.PI * 2 / CREEPER_COUNT;
                double dist = 3 + random.nextFloat() * 2;
                creeper.setPos(getX() + Math.cos(angle) * dist, getY() + (i % 4 - 2), getZ() + Math.sin(angle) * dist);
                creeper.setMaster(this, i);
                serverLevel.addFreshEntity(creeper);
            }
        }
    }

    // === Phase 1 invulnerability ===

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (!phase2) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!phase2) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected boolean shouldDiscardWhenNoTarget() { return true; }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        // Clean up creepers
        for (Entity e : level().getEntities(this, getBoundingBox().inflate(64),
                e -> e instanceof CreeperOfCthulhu)) {
            e.discard();
        }
    }
}
