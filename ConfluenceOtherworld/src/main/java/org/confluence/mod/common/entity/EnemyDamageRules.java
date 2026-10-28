package org.confluence.mod.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.CommonConfigs;

// 通过配置控制敌怪阵营不互伤、敌怪不伤害被动生物；
// 玩家、玩家召唤物和无攻击者的环境伤害不受这些规则影响。
public final class EnemyDamageRules {
    private EnemyDamageRules() {}

    public static boolean blocks(Entity victim, DamageSource source) {
        Entity attacker = source.getEntity();
        if (attacker == null) attacker = source.getDirectEntity();
        if (!isEnemy(attacker)) return false;
        /// 禁止怪物互伤
        if (isProtectedEnemy(victim)) return true;
        /// 禁止敌怪伤害被动生物
        return isProtectedPassiveCreature(victim);
    }

    public static boolean isProtectedEnemy(Entity entity) {
        return CommonConfigs.PREVENT_ENEMY_FRIENDLY_FIRE.get() && isEnemy(entity);
    }

    /// 受配置保护的被动生物：动物、水生生物与环境生物（蝙蝠等）。
    public static boolean isProtectedPassiveCreature(Entity entity) {
        return CommonConfigs.PREVENT_ENEMY_HURTING_PASSIVE_MOBS.get() && isPassiveCreature(entity);
    }

    /// 被动生物：非敌怪阵营的动物 / 水生生物 / 环境生物。
    ///
    /// 玩家、村民与城镇 NPC、召唤物本体都不在此列；本模组的生物（甲虫、蝴蝶等）都继承 `Animal`，同样受保护。
    public static boolean isPassiveCreature(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living instanceof Player || isEnemy(living)) {
            return false;
        }
        return living instanceof Animal || living instanceof WaterAnimal || living instanceof AmbientCreature;
    }

    public static boolean isEnemy(Entity entity) {
        return LibEntityUtils.isEnemyFaction(entity);
    }
}
