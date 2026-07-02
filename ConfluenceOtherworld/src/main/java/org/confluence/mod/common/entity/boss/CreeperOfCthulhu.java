package org.confluence.mod.common.entity.boss;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.BaseFlyingMonster;

public class CreeperOfCthulhu extends BaseFlyingMonster {
    private LivingEntity master;
    private int index;
    private double orbitAngle;

    public CreeperOfCthulhu(EntityType<? extends BaseFlyingMonster> type, Level level) {
        super(type, level);
    }

    public void setMaster(LivingEntity master, int index) {
        this.master = master;
        this.index = index;
        this.orbitAngle = index * Math.PI * 2 / 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(CreeperOfCthulhu.this),
                                new ChargeAttackAction(CreeperOfCthulhu.this, 0.5)),
                        SequenceNode.of(new WaitAction(10),
                                new FlyWanderAction(CreeperOfCthulhu.this, 0.3, 5))
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();

        // Orbit around master
        if (master != null && master.isAlive()) {
            if (getTarget() == null) {
                double dist = 4.0;
                orbitAngle += 0.03;
                double tx = master.getX() + Math.cos(orbitAngle) * dist;
                double ty = master.getY() + Math.sin(orbitAngle * 1.3) * 2;
                double tz = master.getZ() + Math.sin(orbitAngle) * dist;
                Vec3 target = new Vec3(tx, ty, tz);
                Vec3 toTarget = target.subtract(position());
                if (toTarget.lengthSqr() > 1) {
                    setDeltaMovement(getDeltaMovement().add(toTarget.normalize().scale(0.05)));
                }
            }
        } else if (!level().isClientSide) {
            // Master is dead → become aggressive and find new target
            if (getTarget() == null && tickCount % 20 == 0) {
                Player nearest = level().getNearestPlayer(this, 32);
                if (nearest != null) setTarget(nearest);
            }
        }
    }
}
