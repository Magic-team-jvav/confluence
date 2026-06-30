package org.confluence.mod.common.entity.monster;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 蠕虫怪物基类——分段实体（头+体+尾），穿透方块移动。
 * 每 tick 头部移动，体节跟随前一个保持固定间距。
 */
public abstract class BaseWormMonster extends BaseMonster {
    private static final float COLLISION_DAMAGE = 10.0F;
    private static final int COLLISION_COOLDOWN = 8;

    protected final List<BaseWormPart> segments = new ArrayList<>();
    private int collisionCooldown;

    public BaseWormMonster(EntityType<? extends BaseWormMonster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    protected abstract int getSegmentCount();

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) initSegments();
    }

    public void initSegments() {
        if (!segments.isEmpty()) return;
        for (int i = 0; i < getSegmentCount(); i++) {
            BaseWormPart part = new BaseWormPart(i == 0 ? this : segments.get(i - 1), i, level());
            if (i == getSegmentCount() - 1) part.tail = true;
            level().addFreshEntity(part);
            segments.add(part);
        }
    }

    @Nullable
    public WormSegment getSegment(int index) {
        if (index < 0) return null;
        if (index == 0) return null;
        int segIdx = index - 1;
        return segIdx < segments.size() ? segments.get(segIdx) : null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (segments.isEmpty()) initSegments();
            tickWormMove();
            tickCollision();
        }
    }

    private void tickWormMove() {
        Entity leader = this;
        for (BaseWormPart part : segments) {
            part.leader = leader;
            leader = part;
        }
    }

    private void tickCollision() {
        if (collisionCooldown > 0) { collisionCooldown--; return; }
        AABB box = getBoundingBox().inflate(1.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (target == this) continue;
            if (target.getType() == getType()) continue;
            if (getTarget() == null) setTarget(target);
            target.hurt(damageSources().mobAttack(this), COLLISION_DAMAGE);
            collisionCooldown = COLLISION_COOLDOWN;
            break;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        for (BaseWormPart part : segments) part.discard();
    }

    public static AttributeSupplier.Builder createWormAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BaseWormMonster.this),
                                new MoveToTargetAction(BaseWormMonster.this, 0.5, 2.0)),
                        new WaitAction(20));
            }
        };
    }
}
