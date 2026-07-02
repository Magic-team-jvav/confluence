package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.condition.HealthLowerThanCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;
import org.confluence.mod.common.init.entity.MonsterEntities;

public class QueenBee extends BaseBoss {
    private static final int BEE_COOLDOWN = 200;
    private int beeTimer = BEE_COOLDOWN;

    public QueenBee(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 1200;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 18.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.YELLOW;
    }

    // === BT ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Phase 2: HP < 50% — aggressive
                        SequenceNode.of(
                                new HealthLowerThanCondition(QueenBee.this, 0.5f),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                                new DashAction(QueenBee.this, 1.0, 15)),
                                        SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                                new ShootAction(QueenBee.this, 10.0f, 0.3f),
                                                new WaitAction(15)),
                                        SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                                new CircleAroundTargetAction(QueenBee.this, 0.5, 3),
                                                new WaitAction(10)),
                                        SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                                new ChargeAttackAction(QueenBee.this, 0.7))
                                )
                        ),
                        // Phase 1: normal
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                        new CircleAroundTargetAction(QueenBee.this, 0.3, 5),
                                        new WaitAction(15)),
                                SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                        new ShootAction(QueenBee.this, 8.0f, 0.5f),
                                        new WaitAction(20)),
                                SequenceNode.of(new HasTargetCondition(QueenBee.this),
                                        new ChargeAttackAction(QueenBee.this, 0.4)),
                                SequenceNode.of(new WaitAction(10),
                                        new FlyWanderAction(QueenBee.this, 0.2, 10))
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
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Spawn bees periodically during combat
            if (getTarget() != null) {
                beeTimer--;
                if (beeTimer <= 0) {
                    beeTimer = BEE_COOLDOWN + random.nextInt(60);
                    spawnBees();
                }
            }
        }
    }

    private void spawnBees() {
        if (level() instanceof ServerLevel serverLevel) {
            int count = isExpert() ? 4 : 2;
            for (int i = 0; i < count; i++) {
                Entity bee = MonsterEntities.CAVE_BAT.get().create(level());
                if (bee instanceof Mob mob) {
                    mob.setPos(
                            getX() + (random.nextFloat() - 0.5) * 3,
                            getY() + random.nextFloat() * 2,
                            getZ() + (random.nextFloat() - 0.5) * 3
                    );
                    if (getTarget() != null) mob.setTarget(getTarget());
                    serverLevel.addFreshEntity(mob);
                }
            }
        }
    }

    @Override
    public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected boolean shouldDiscardWhenNoTarget() { return true; }
}
