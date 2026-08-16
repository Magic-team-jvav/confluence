package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.data.entity.CreatureDefinition;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SpawnProjectileAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.HostileParticleProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 以环绕目标和周期性弹幕为主要战斗方式的飞行怪物基类。
///
/// <p>构造参数提供没有数据包覆盖时的射击间隔与伤害倍率，行为树创建时再读取当前
/// {@link CreatureDefinition}。因此数据包可以调整同一实体的盘旋速度、盘旋半径、射击节奏
/// 和游荡范围，而实体注册代码仍保留一套始终可用的安全默认值。</p>
///
/// <p>射击行为复用实体攻击伤害属性生成弹幕快照；本类只编排移动与发射时机，不重复实现
/// 弹幕命中、暴击或伤害结算。</p>
public class RangedFlyingMonster extends BaseFlyingMonster {
    private final int shotCooldown;
    private final double shotMultiplier;

    public RangedFlyingMonster(EntityType<? extends RangedFlyingMonster> type, Level level,
                               int shotCooldown, double shotMultiplier) {
        super(type, level);
        this.shotCooldown = shotCooldown;
        this.shotMultiplier = shotMultiplier;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected BTRoot createBT() {
        CreatureDefinition.BehaviorOverrides behavior = creatureDefinition().behavior();
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(RangedFlyingMonster.this),
                                new CircleAroundTargetAction(RangedFlyingMonster.this,
                                        behavior.orbitSpeedOr(0.28), behavior.orbitRadiusOr(7)),
                                new SpawnProjectileAction(
                                        RangedFlyingMonster.this,
                                        RangedFlyingMonster.this::createVileSpit),
                                new WaitAction(behavior.shotCooldownOr(shotCooldown))),
                        new FlyWanderAction(RangedFlyingMonster.this,
                                behavior.wanderSpeedOr(0.18), behavior.wanderRadiusOr(10)));
            }
        };
    }

    HostileParticleProjectile createVileSpit(LivingEntity target) {
        HostileParticleProjectile projectile =
                ModEntities.VILE_SPIT_PROJECTILE.get().create(level());
        if (projectile == null) {
            return null;
        }
        projectile.configure(
                this,
                target,
                (float) (getAttributeValue(Attributes.ATTACK_DAMAGE)
                        * creatureDefinition().behavior()
                        .shotMultiplierOr(shotMultiplier)));
        return projectile;
    }
}
