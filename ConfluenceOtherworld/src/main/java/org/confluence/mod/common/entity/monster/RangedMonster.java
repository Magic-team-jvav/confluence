package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.*;

/// 地面远程战斗模板。
///
/// 在专用弹幕美术资源移植期间，实体仍使用统一的距离控制与射击参数，
/// 之后替换渲染资源不应改变服务端战斗逻辑。
public abstract class RangedMonster extends BaseWarriorMonster {
    private final int shotCooldown;
    private final double shotMultiplier;

    public RangedMonster(EntityType<? extends RangedMonster> type, Level level, int shotCooldown, double shotMultiplier) {
        super(type, level);
        this.shotCooldown = shotCooldown;
        this.shotMultiplier = shotMultiplier;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseWarriorMonster.createAttributes().add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected BTRoot createBT() {
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(RangedMonster.this),
                                new MoveToTargetAction(RangedMonster.this,
                                        behavior.moveSpeedOr(0.9), behavior.preferredRangeOr(14.0)),
                                new SpawnProjectileAction(
                                        RangedMonster.this,
                                        RangedMonster.this::createProjectile),
                                new WaitAction(behavior.shotCooldownOr(shotCooldown))),
                        SequenceNode.of(new HasTargetCondition(RangedMonster.this),
                                new MoveToTargetAction(RangedMonster.this,
                                        behavior.moveSpeedOr(1.1), behavior.meleeRangeOr(2.0)),
                                new MeleeAttackAction(RangedMonster.this,
                                        behavior.meleeRangeOr(1.0))),
                        SequenceNode.of(new WaitAction(behavior.idleTicksOr(20) + random.nextInt(40)),
                                new RandomStrollAction(RangedMonster.this,
                                        behavior.wanderSpeedOr(0.5), behavior.wanderRadiusOr(8))));
            }
        };
    }

    /// 由具体怪物创建自己的弹幕，基类不根据实体 ID 猜测攻击类型。
    protected abstract Projectile createProjectile(LivingEntity target);

    protected final double shotMultiplier() {
        return creatureDefinition().behavior().shotMultiplierOr(shotMultiplier);
    }
}
