package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import org.confluence.mod.common.entity.ai.bt.leaf.DashAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;

public class Plantera extends BaseBoss {
    private static final int SHOOT_COOLDOWN = 25;
    private static final int TENTACLE_COOLDOWN = 150;
    private int shootTimer = 0;
    private int tentacleTimer = TENTACLE_COOLDOWN;

    public Plantera(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.xpReward = 3000;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 900.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PINK;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        // Phase 2: <50% HP — aggressive
                        SequenceNode.of(
                                new HealthLowerThanCondition(Plantera.this, 0.5f),
                                SelectorNode.of(
                                        SequenceNode.of(new HasTargetCondition(Plantera.this),
                                                new MoveToTargetAction(Plantera.this, 0.6, 3.0)),
                                        SequenceNode.of(new HasTargetCondition(Plantera.this),
                                                new DashAction(Plantera.this, 0.8, 20)),
                                        SequenceNode.of(new WaitAction(5),
                                                new MoveToTargetAction(Plantera.this, 0.4, 5.0))
                                )
                        ),
                        // Phase 1: chase
                        SelectorNode.of(
                                SequenceNode.of(new HasTargetCondition(Plantera.this),
                                        new MoveToTargetAction(Plantera.this, 0.3, 4.0)),
                                new WaitAction(30)
                        )
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Shoot projectiles
            if (getTarget() != null) {
                shootTimer--;
                if (shootTimer <= 0) {
                    int cd = getHealth() / getMaxHealth() < 0.5f ? SHOOT_COOLDOWN / 2 : SHOOT_COOLDOWN;
                    shootTimer = cd + random.nextInt(10);
                    shootAtTarget();
                }
            }

            // Phase 2: spawn tentacles
            if (getHealth() / getMaxHealth() < 0.5f && getTarget() != null) {
                tentacleTimer--;
                if (tentacleTimer <= 0) {
                    tentacleTimer = TENTACLE_COOLDOWN + random.nextInt(60);
                    spawnTentacles();
                }
            }
        }
    }

    private void shootAtTarget() {
        if (getTarget() == null || !(level() instanceof ServerLevel)) return;
        Vec3 origin = getEyePosition();
        Vec3 dir = getTarget().getEyePosition().subtract(origin).normalize();
        Vec3 end = origin.add(dir.scale(32));
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().expandTowards(dir.scale(32)).inflate(1.0))) {
            if (e == this || !canAttack(e)) continue;
            if (e.getBoundingBox().clip(origin, end).isPresent()) {
                e.hurt(damageSources().mobAttack(this), 10.0f);
                break;
            }
        }
    }

    private void spawnTentacles() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        LivingEntity target = getTarget();
        if (target == null) return;
        int count = isExpert() ? 5 : 3;
        for (int i = 0; i < count; i++) {
            PlanteraTentacle t = BossEntities.PLANTERA_TENTACLE.get().create(level());
            if (t != null) {
                double ax = (random.nextFloat() - 0.5) * 6;
                double ay = (random.nextFloat() - 0.5) * 4;
                double az = (random.nextFloat() - 0.5) * 6;
                t.setPos(target.getX() + ax, target.getY() + ay, target.getZ() + az);
                t.setMaster(this, target.position());
                serverLevel.addFreshEntity(t);
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return true; }
}
