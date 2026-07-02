package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import org.confluence.mod.common.init.entity.BossEntities;

public class EyeOfCthulhu extends BaseBoss {
    private static final int SERVANT_COOLDOWN = 150;
    private int servantTimer = SERVANT_COOLDOWN;

    public EyeOfCthulhu(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 1000;
    }

    // === Attributes ===

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    // === Boss bar ===

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    // === BT ===

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Phase 2: HP < 50% — aggressive dashing + charging, no servants
                        SequenceNode.of(
                                new HealthLowerThanCondition(EyeOfCthulhu.this, 0.5f),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(EyeOfCthulhu.this),
                                                new DashAction(EyeOfCthulhu.this, 1.0, 20)),
                                        SequenceNode.of(new HasTargetCondition(EyeOfCthulhu.this),
                                                new CircleAroundTargetAction(EyeOfCthulhu.this, 0.5, 3),
                                                new WaitAction(10)),
                                        SequenceNode.of(new HasTargetCondition(EyeOfCthulhu.this),
                                                new ChargeAttackAction(EyeOfCthulhu.this, 0.7))
                                )
                        ),
                        // Phase 1: HP >= 50% — circling + charge + spawn servants
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(EyeOfCthulhu.this),
                                        new CircleAroundTargetAction(EyeOfCthulhu.this, 0.3, 5),
                                        new WaitAction(20)),
                                SequenceNode.of(new HasTargetCondition(EyeOfCthulhu.this),
                                        new ChargeAttackAction(EyeOfCthulhu.this, 0.4)),
                                SequenceNode.of(new WaitAction(15),
                                        new FlyWanderAction(EyeOfCthulhu.this, 0.2, 8))
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
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Servant spawning (phase 1 only: HP >= 50%)
            if (getHealth() / getMaxHealth() >= 0.5f && getTarget() != null) {
                servantTimer--;
                if (servantTimer <= 0) {
                    servantTimer = SERVANT_COOLDOWN + random.nextInt(60);
                    spawnServant();
                }
            }
        }
    }

    private void spawnServant() {
        if (level() instanceof ServerLevel serverLevel) {
            ServantOfCthulhu servant = BossEntities.SERVANT_OF_CTHULHU.get().create(level());
            if (servant != null) {
                double angle = random.nextFloat() * Math.PI * 2;
                double dist = 3 + random.nextFloat() * 3;
                servant.setPos(
                        getX() + Math.cos(angle) * dist,
                        getY() + 1 + random.nextFloat() * 2,
                        getZ() + Math.sin(angle) * dist
                );
                if (getTarget() != null) servant.setTarget(getTarget());
                serverLevel.addFreshEntity(servant);
            }
        }
    }

    // === Misc ===

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
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
        // Clean up remaining servants
        for (Entity e : level().getEntities(this, getBoundingBox().inflate(64),
                e -> e instanceof ServantOfCthulhu)) {
            e.discard();
        }
    }
}
