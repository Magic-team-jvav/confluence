package org.confluence.mod.common.entity.boss;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.monster.WormSegment;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 蠕虫型 Boss 基类。穿透方块移动，体节跟随。
 */
public abstract class BaseWormBoss extends BaseBoss {
    private static final float COLLISION_DAMAGE = 10.0F;
    private static final int COLLISION_COOLDOWN = 8;

    protected final List<BossWormPart> segments = new ArrayList<>();
    private int collisionCooldown;

    public BaseWormBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    protected abstract int getSegmentCount();

    protected EntityType<?> getPartType() {
        return getType();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) initSegments();
    }

    public void initSegments() {
        if (!segments.isEmpty()) return;
        for (int i = 0; i < getSegmentCount(); i++) {
            Entity leader = i == 0 ? this : segments.get(i - 1);
            BossWormPart part = new BossWormPart(leader, i, level());
            if (i == getSegmentCount() - 1) part.tail = true;
            level().addFreshEntity(part);
            segments.add(part);
        }
    }

    @Nullable
    public WormSegment getSegment(int index) {
        if (index < 0) return null;
        if (index == 0) return null; // head is the boss itself
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
        for (BossWormPart part : segments) {
            part.leader = leader;
            leader = part;
        }
    }

    private void tickCollision() {
        if (collisionCooldown > 0) { collisionCooldown--; return; }
        AABB box = getBoundingBox().inflate(1.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (target == this) continue;
            if (isAlliedTo(target)) continue;
            target.hurt(damageSources().mobAttack(this), COLLISION_DAMAGE);
            collisionCooldown = COLLISION_COOLDOWN;
            if (getTarget() == null) setTarget(target);
            break;
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == damageSources().inWall() || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        for (BossWormPart part : segments) part.discard();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(BaseWormBoss.this),
                                new MoveToTargetAction(BaseWormBoss.this, 0.5, 2.0)),
                        new WaitAction(20));
            }
        };
    }

    public static AttributeSupplier.Builder createWormBossAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }
}
