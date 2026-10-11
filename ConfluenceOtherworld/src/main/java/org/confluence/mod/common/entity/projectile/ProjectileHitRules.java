package org.confluence.mod.common.entity.projectile;

import org.confluence.lib.util.LibEntityUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.PartHitTarget;
import org.jetbrains.annotations.Nullable;

/// 玩家武器弹幕共用的所有者、队伍、PvP 与快照击退规则。
public final class ProjectileHitRules {
    private ProjectileHitRules() {}

    /// 校验原始碰撞部位及其遭遇主体。可攻击性、同载具、队伍友伤和服务器 PvP 均在此处处理。
    public static boolean canHit(@Nullable Entity owner, Entity rawTarget) {
        return LibEntityUtils.canHit(owner, rawTarget);
    }

    /// 返回原始实体是否允许成为直接命中部位。
    public static boolean acceptsDirectHit(Entity rawTarget) {
        return LibEntityUtils.acceptsDirectHit(rawTarget);
    }

    /// 返回必须实际执行 {@link Entity#hurt} 的实体。
    /// 原始部位保留自己的护甲、伤害倍率、生命和受击反馈。
    public static Entity damageRecipient(Entity rawTarget) {
        return rawTarget instanceof PartHitTarget part ? part.damageRecipient() : rawTarget;
    }

    /// 返回部位所属的遭遇主体，用于阵营、索敌、命中特效和奖励归属。
    public static Entity encounterOwner(Entity rawTarget) {
        return LibEntityUtils.encounterOwner(rawTarget);
    }

    /// 返回一次穿透或连续碰撞中的去重身份。
    public static Entity dedupeIdentity(Entity rawTarget) {
        if (rawTarget instanceof PartHitTarget part) {
            return part.dedupeIdentity();
        }
        return encounterOwner(rawTarget);
    }

    /// 将原始碰撞实体解析为可用于召唤物锁定的生命实体。
    public static @Nullable LivingEntity logicalLivingTarget(Entity rawTarget) {
        Entity owner = encounterOwner(rawTarget);
        return owner instanceof LivingEntity living ? living : null;
    }

    /// 应用已经在发射快照中解析完成的击退。
    ///
    /// 这里故意不再读取攻击者当前 {@code ATTACK_KNOCKBACK}，只保留受击者的击退抗性，
    /// 从而避免换武器后改变已发射弹幕或把攻击击退属性计算两次。
    public static void applyResolvedKnockback(Entity projectile, Entity target, float strength, double motionY) {
        if (strength <= 0.0F && motionY <= 0.0) {
            return;
        }
        double resolvedStrength = strength;
        if (target instanceof LivingEntity living) {
            AttributeInstance resistance = living.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (resistance != null) {
                resolvedStrength *= 1.0 - resistance.getValue();
            }
        }
        Vec3 horizontal = target.position().subtract(projectile.position()).multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-8) {
            horizontal = projectile.getDeltaMovement().multiply(1.0, 0.0, 1.0);
        }
        if (horizontal.lengthSqr() >= 1.0E-8 && resolvedStrength > 0.0) {
            horizontal = horizontal.normalize().scale(resolvedStrength);
        } else {
            horizontal = Vec3.ZERO;
        }
        target.addDeltaMovement(horizontal.add(0.0, motionY, 0.0));
    }
}
