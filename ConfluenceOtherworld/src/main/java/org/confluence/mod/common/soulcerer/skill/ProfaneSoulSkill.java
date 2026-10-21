package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.util.PlayerUtils;

/// 亵渎之魂：从地面钻出火球弹幕，命中小范围爆炸。
///
/// 索敌与**魂引天星**同一套口径（`STAR_CALL_TARGET_RADIUS` = 48 格、球形距离、只认敌怪、取最近一只）：
/// 火球从目标**脚下** `PROFANE_SOUL_SPAWN_OFFSET` 格处钻出，朝它的身体中心向上飞；
/// 没有敌人时退化为在自身前方地面向上喷出。
public class ProfaneSoulSkill extends SoulSkill {
    public ProfaneSoulSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.PROFANE_SOUL_DAMAGE, Kind.ACTIVE,
                SoulSkillParameters.PROFANE_SOUL_SOUL_COST, SoulSkillParameters.PROFANE_SOUL_COOLDOWN_TICKS);
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float damage = PlayerUtils.getSoulSkillDamage(player, skillStack);
        LivingEntity target = PlayerUtils.findNearestEnemy(serverPlayer, SoulSkillParameters.STAR_CALL_TARGET_RADIUS);
        Vec3 origin;
        Vec3 direction;
        if (target != null) {
            /// 从敌怪脚下钻出，朝它的碰撞箱中心向上打，命中点自然落在它身上。
            origin = new Vec3(target.getX(), target.getY() - SoulSkillParameters.PROFANE_SOUL_SPAWN_OFFSET, target.getZ());
            direction = target.getBoundingBox().getCenter().subtract(origin);
            if (direction.lengthSqr() < 1.0E-4) {
                direction = new Vec3(0.0, 1.0, 0.0);
            }
        } else {
            origin = serverPlayer.position().add(serverPlayer.getLookAngle().scale(SoulSkillParameters.PROFANE_SOUL_SPAWN_DISTANCE));
            direction = new Vec3(0.0, 1.0, 0.0);
        }
        player.level().addFreshEntity(new SoulLureProjectile(serverPlayer, origin, direction, damage,
                SoulLureProjectile.FIREBALL_PARTICLE, 0.6F, 0.04,
                SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS,
                SoulSkillParameters.PROFANE_SOUL_AOE_RADIUS, SoulSkillParameters.PROFANE_SOUL_AOE_DAMAGE));
    }
}
