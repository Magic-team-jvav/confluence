package org.confluence.mod.common.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.boss.BaseBoss;
import org.confluence.mod.common.entity.boss.BossOwnedEntity;
import org.confluence.mod.common.entity.monster.BaseMonster;
import org.jetbrains.annotations.Nullable;

/// 普通敌怪共用目标仲裁：有效玩家优先，其次保留或寻找受击反击目标。
/// Boss 使用自己的遭遇范围和玩家死亡规则，不由这里覆盖。
public final class EnemyTargeting {
    private EnemyTargeting() {}

    /// 射弹和多部件的仇恨归属于实际攻击者，没有攻击者的环境伤害不产生目标。
    public static @Nullable LivingEntity attacker(DamageSource source) {
        Entity entity = source.getEntity();
        if (entity == null) entity = source.getDirectEntity();
        for (int depth = 0; entity != null && depth < 16; depth++) {
            Entity owner = entity instanceof PartHitTarget part ? part.encounterOwner() : LibEntityUtils.getOwner(entity);
            if (owner == null || owner == entity)
                return entity instanceof LivingEntity living ? living : null;
            entity = owner;
        }
        return null;
    }

    public static boolean applies(Mob mob) {
        return !mob.level().isClientSide && !mob.isNoAi() && mob.isAlive()
                && !(mob instanceof BaseBoss) && isConfluenceEnemy(mob);
    }

    /// 只接管本模组注册的敌怪；原版和其他模组保留各自的索敌及反击规则。
    public static boolean isConfluenceEnemy(Mob mob) {
        return Confluence.MODID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace())
                && EnemyDamageRules.isEnemy(mob);
    }

    public static @Nullable LivingEntity select(Mob mob, LivingEntity proposed) {
        LivingEntity current = mob.getTarget();
        if (current instanceof Player player && validPlayer(mob, player)) return player;
        if (proposed instanceof Player player && validPlayer(mob, player)) return player;
        Player nearest = null;
        double distance = Double.POSITIVE_INFINITY;
        for (Player player : mob.level().players()) {
            if (validPlayer(mob, player) && mob.distanceToSqr(player) < distance) {
                nearest = player;
                distance = mob.distanceToSqr(player);
            }
        }
        if (nearest != null) return nearest;
        if (mob instanceof BossOwnedEntity dependent && dependent.getBossOwner() != null
                && proposed == dependent.getBossOwner().getTarget() && valid(mob, proposed))
            return proposed;
        LivingEntity attacker = mob.getLastHurtByMob();
        if (valid(mob, attacker)) return attacker;
        /// 已经开始的反击不依赖原版短时受击记录一直存在。
        if (current != null && !(current instanceof Player) && valid(mob, current)) return current;
        return null;
    }

    private static boolean validPlayer(Mob mob, Player player) {
        if (!valid(mob, player)) return false;
        return !(mob instanceof BaseMonster monster) || monster.canSelectPlayerTarget(player);
    }

    public static boolean valid(Mob mob, LivingEntity target) {
        if (target == null || target == mob || target.level() != mob.level() || !target.isAlive() || target.isRemoved() || EnemyDamageRules.isEnemy(target) || !mob.canAttack(target))
            return false;
        if (target instanceof Player player && (player.isCreative() || player.isSpectator()))
            return false;
        double range = mob.getAttributeValue(Attributes.FOLLOW_RANGE);
        return mob.distanceToSqr(target) <= range * range;
    }
}
