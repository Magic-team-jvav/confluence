package org.confluence.mod.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.CommonConfigs;

// 通过配置控制敌怪阵营不互伤；玩家、玩家召唤物和无攻击者的环境伤害不受此规则影响。
public final class EnemyDamageRules {
    private EnemyDamageRules() {}

    public static boolean blocks(Entity victim, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) attacker = source.getDirectEntity();
        return isProtectedEnemy(victim) && isEnemy(attacker);
    }

    public static boolean isProtectedEnemy(Entity entity) {
        return CommonConfigs.PREVENT_ENEMY_FRIENDLY_FIRE.get() && isEnemy(entity);
    }

    public static boolean isEnemy(Entity entity) {
        return LibEntityUtils.isEnemyFaction(entity);
    }
}
