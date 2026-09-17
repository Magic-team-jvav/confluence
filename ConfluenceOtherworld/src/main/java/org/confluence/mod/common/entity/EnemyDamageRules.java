package org.confluence.mod.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Enemy;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.summon.OwnedSummon;

/// 敌怪阵营不互伤；玩家、玩家召唤物和无攻击者的环境伤害不受此规则影响。
public final class EnemyDamageRules {
    private EnemyDamageRules() {}

    public static boolean blocks(Entity victim, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) attacker = source.getDirectEntity();
        return isEnemy(victim) && isEnemy(attacker);
    }

    private static boolean isEnemy(Entity entity) {
        for (int depth = 0; entity != null && depth < 16; depth++) {
            if (entity instanceof OwnedSummon) return false;
            Entity owner = entity instanceof PartHitTarget part ? part.encounterOwner() : LibEntityUtils.getOwner(entity);
            if (owner == null || owner == entity)
                return entity instanceof Enemy || entity.getType().getCategory() == MobCategory.MONSTER;
            entity = owner;
        }
        return false;
    }
}
