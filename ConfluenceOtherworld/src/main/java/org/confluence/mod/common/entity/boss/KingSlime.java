package org.confluence.mod.common.entity.boss;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.condition.HealthLowerThanCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.MonsterEntities;

public class KingSlime extends BaseBoss {
    private static final int PRE_JUMP_TICKS = 5;
    private static final int TELEPORT_SHRINK_TICKS = 20;
    private static final int TELEPORT_ENLARGE_TICKS = 20;
    private static final int STUCK_THRESHOLD = 200;
    private static final int MAX_SIZE = 12;
    private static final int MIN_SIZE = 4;

    private int lastNearTargetTick = 0;
    private int teleportTimer = 0;
    private TeleportState teleportState = TeleportState.NONE;
    private int prevSlimesLeft = 0;

    private enum TeleportState { NONE, SHRINKING, ENLARGING }

    public KingSlime(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 800;
        setMaxUpStep(1.0f);
    }

    // === Attributes ===

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 728.0)
                .add(Attributes.ATTACK_DAMAGE, 16.5)
                .add(Attributes.ATTACK_KNOCKBACK, 2.2)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    // === Boss bar ===

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.BLUE;
    }

    // === BT ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Expert phase: HP < 50% → more aggressive jumping
                        SequenceNode.of(
                                new HealthLowerThanCondition(KingSlime.this, 0.5f),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(KingSlime.this),
                                                new BossHopAction(KingSlime.this, true, 0.9)),
                                        SequenceNode.of(new WaitAction(10),
                                                new BossHopAction(KingSlime.this, false, 0.7))
                                )
                        ),
                        // Normal phase
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(KingSlime.this),
                                        new BossHopAction(KingSlime.this, true, 0.5)),
                                SequenceNode.of(new WaitAction(20 + random.nextInt(40)),
                                        new BossHopAction(KingSlime.this, false, 0.3))
                        )
                );
            }
        };
    }

    // === Goals ===

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    // === Tick ===

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            // Target finding
            if (getTarget() == null && tickCount % 40 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Stuck detection
            if (getTarget() != null && distanceToSqr(getTarget()) < 25) {
                lastNearTargetTick = tickCount;
            }

            if (teleportState == TeleportState.NONE && getTarget() != null
                    && tickCount - lastNearTargetTick > STUCK_THRESHOLD) {
                startTeleport();
            }

            // Teleport animation
            if (teleportState != TeleportState.NONE) {
                teleportTick();
            }
        } else {
            // Client: gel particles on ground contact (cheap approximation)
            if (onGround() && random.nextInt(3) == 0) {
                for (int i = 0; i < 3; i++) {
                    level().addParticle(ParticleTypes.ITEM_SLIME,
                            getX() + (random.nextFloat() - 0.5) * getBbWidth(),
                            getY(),
                            getZ() + (random.nextFloat() - 0.5) * getBbWidth(),
                            0.2, 0.6, 1.0);
                }
            }
        }
    }

    private void startTeleport() {
        teleportState = TeleportState.SHRINKING;
        teleportTimer = 0;
    }

    private void teleportTick() {
        teleportTimer++;

        if (teleportState == TeleportState.SHRINKING && teleportTimer >= TELEPORT_SHRINK_TICKS) {
            // Teleport near target
            if (getTarget() != null && level() instanceof ServerLevel serverLevel) {
                Vec3 targetPos = getTarget().position();
                float angle = random.nextFloat() * Mth.TWO_PI;
                double dist = 3 + random.nextFloat() * 5;
                double tx = targetPos.x + Math.cos(angle) * dist;
                double tz = targetPos.z + Math.sin(angle) * dist;
                BlockPos surface = serverLevel.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos((int) tx, 0, (int) tz));
                teleportTo(tx, surface.getY() + 2, tz);
            }
            lastNearTargetTick = tickCount;
            teleportState = TeleportState.ENLARGING;
            teleportTimer = 0;
        }

        if (teleportState == TeleportState.ENLARGING && teleportTimer >= TELEPORT_ENLARGE_TICKS) {
            teleportState = TeleportState.NONE;
            teleportTimer = 0;
        }
    }

    private int getSlimeSize() {
        if (teleportState == TeleportState.SHRINKING) {
            float progress = (float) teleportTimer / TELEPORT_SHRINK_TICKS;
            return Math.max(1, Math.round(getMaxSlimeSize() * (1.0f - progress)));
        }
        if (teleportState == TeleportState.ENLARGING) {
            float progress = (float) teleportTimer / TELEPORT_ENLARGE_TICKS;
            return Math.max(1, Math.round(progress * getMaxSlimeSize()));
        }
        return getMaxSlimeSize();
    }

    private int getMaxSlimeSize() {
        return Math.max(MIN_SIZE, Math.round(getHealth() / getMaxHealth() * MAX_SIZE));
    }

    // === Hurt → spawn slimes ===

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (teleportState != TeleportState.NONE) return false;

        int slimesBefore = getSlimesLeft();
        boolean result = super.hurt(source, amount);
        int slimesAfter = getSlimesLeft();

        if (result && !level().isClientSide) {
            for (int i = 0; i < slimesBefore - slimesAfter; i++) {
                spawnBabySlime();
            }
        }
        return result;
    }

    private int getSlimesLeft() {
        return (int) (getHealth() / getMaxHealth() * getTotalSplits());
    }

    private int getTotalSplits() {
        return isExpert() ? 50 : 30;
    }

    private void spawnBabySlime() {
        if (level() instanceof ServerLevel serverLevel) {
            EntityType<?> slimeType = isExpert() && random.nextFloat() < 0.15f
                    ? MonsterEntities.SPIKED_SLIME.get()
                    : MonsterEntities.BLUE_SLIME.get();
            Entity slime = slimeType.create(level());
            if (slime instanceof Mob mob) {
                mob.setPos(position().x + (random.nextFloat() - 0.5) * 2,
                        position().y + 0.5,
                        position().z + (random.nextFloat() - 0.5) * 2);
                if (getTarget() != null) mob.setTarget(getTarget());
                serverLevel.addFreshEntity(mob);
            }
        }
    }

    // === Dimensions ===

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        int size = getSlimeSize();
        float dim = 0.51000005F * size;
        return EntityDimensions.scalable(dim, dim);
    }

    @Override
    public void onSyncedDataUpdated(net.minecraft.network.syncher.EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        refreshDimensions();
    }

    // === Misc ===

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && !(target instanceof Slime);
    }

    @Override
    public void playerTouch(Player player) {
        if (teleportState != TeleportState.NONE) return;
        super.playerTouch(player);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level().isClientSide) {
            // Spawn a few slimes on death
            for (int i = 0; i < 4; i++) {
                spawnBabySlime();
            }
        }
    }

    // === Inner: BossHopAction ===

    private static class BossHopAction extends BTNode {
        private final KingSlime boss;
        private final boolean towardTarget;
        private final double speed;
        private int tick;

        BossHopAction(KingSlime boss, boolean towardTarget, double speed) {
            this.boss = boss;
            this.towardTarget = towardTarget;
            this.speed = speed;
        }

        @Override
        public void start() { tick = 0; }

        @Override
        public BTStatus execute() {
            tick++;
            if (tick <= PRE_JUMP_TICKS) return BTStatus.RUNNING;

            if (tick == PRE_JUMP_TICKS + 1) {
                Vec3 dir;
                if (towardTarget && boss.getTarget() != null) {
                    Vec3 toTarget = boss.getTarget().position().subtract(boss.position());
                    double hDist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
                    dir = hDist > 0.01
                            ? new Vec3(toTarget.x / hDist, 0, toTarget.z / hDist)
                            : Vec3.ZERO;
                } else {
                    float yaw = boss.getRandom().nextFloat() * Mth.TWO_PI;
                    dir = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
                }
                double jumpPower = 0.5 + speed;
                double h = jumpPower * 0.7;
                boss.setDeltaMovement(dir.x * h * speed * 2, jumpPower, dir.z * h * speed * 2);
                boss.hasImpulse = true;
                return BTStatus.RUNNING;
            }

            if (boss.onGround() && tick > PRE_JUMP_TICKS + 2) return BTStatus.SUCCESS;
            if (tick > 60) return BTStatus.SUCCESS;
            return BTStatus.RUNNING;
        }
    }
}
