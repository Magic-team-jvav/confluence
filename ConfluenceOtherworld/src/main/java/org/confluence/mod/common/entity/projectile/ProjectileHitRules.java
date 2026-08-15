package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.entity.boss.BaseBossPart;
import org.confluence.mod.common.entity.boss.BossWormPart;
import org.confluence.mod.common.entity.monster.BaseWormPart;
import org.jetbrains.annotations.Nullable;

/**
 * 玩家武器弹幕共用的所有者、队伍、PvP 与快照击退规则。
 */
public final class ProjectileHitRules {
    private ProjectileHitRules() {}

    /**
     * 校验原始碰撞实体及其真正受击本体。原版可攻击性、同载具、队伍友伤和服务器 PvP 均在此处理。
     */
    public static boolean canHit(@Nullable Entity owner, Entity rawTarget) {
        if (!LibEntityUtils.canHitEntity(rawTarget, owner)) {
            return false;
        }
        Entity target = impactedEntity(rawTarget);
        if (target == owner || owner != null && owner.isAlliedTo(target)) {
            return false;
        }
        if (owner instanceof Player attackingPlayer
                && target instanceof Player targetPlayer) {
            return attackingPlayer.canHarmPlayer(targetPlayer)
                    && PlayerSpecialData.of(attackingPlayer).isPvP()
                    && PlayerSpecialData.of(targetPlayer).isPvP();
        }
        return true;
    }

    /**
     * 多部件实体统一返回真正承受伤害和 UUID 去重的本体。
     */
    public static Entity impactedEntity(Entity rawTarget) {
        Entity impacted = LibEntityUtils.tryFindBeImpacted(rawTarget);
        if (impacted != null && impacted != rawTarget) {
            return impacted;
        }
        // 这些 1.20 重写部件是独立 Entity，不是 Forge PartEntity，
        // 因此需要显式解析到唯一的生命与去重权威。
        if (rawTarget instanceof BaseBossPart<?> part
                && part.getOwner() != null) {
            return part.getOwner();
        }
        if (rawTarget instanceof BossWormPart part
                && part.getOwner() != null) {
            return part.getOwner();
        }
        if (rawTarget instanceof BaseWormPart part
                && part.getOwner() != null) {
            return part.getOwner();
        }
        return rawTarget;
    }

    /**
     * 应用已经在发射快照中解析完成的击退。
     *
     * <p>这里故意不再读取攻击者当前 {@code ATTACK_KNOCKBACK}，只保留受击者的击退抗性，
     * 从而避免换武器后改变已发射弹幕或把攻击击退属性计算两次。</p>
     */
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
