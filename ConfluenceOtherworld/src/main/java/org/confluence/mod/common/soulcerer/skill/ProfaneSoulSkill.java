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
/// 优先在最近敌人的脚下钻出，没有目标时在自身前方地面钻出；火球外观复用火焰花的粒子特效。
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
        LivingEntity nearest = null;
        double best = SoulSkillParameters.PROFANE_SOUL_SPAWN_DISTANCE * SoulSkillParameters.PROFANE_SOUL_SPAWN_DISTANCE * 4.0;
        for (LivingEntity target : serverPlayer.level().getEntitiesOfClass(LivingEntity.class,
                serverPlayer.getBoundingBox().inflate(SoulSkillParameters.PROFANE_SOUL_SPAWN_DISTANCE * 2.0),
                target -> target.isAlive() && !target.isSpectator() && !target.isAlliedTo(serverPlayer))) {
            double distance = target.distanceToSqr(serverPlayer);
            if (distance < best) {
                best = distance;
                nearest = target;
            }
        }
        Vec3 origin = nearest != null
                ? new Vec3(nearest.getX(), nearest.getY() + 0.2, nearest.getZ())
                : serverPlayer.position().add(serverPlayer.getLookAngle().scale(SoulSkillParameters.PROFANE_SOUL_SPAWN_DISTANCE));
        player.level().addFreshEntity(new SoulLureProjectile(serverPlayer, origin, new Vec3(0, 1, 0), damage,
                SoulLureProjectile.FIREBALL_PARTICLE, 0.6F, 0.04,
                SoulSkillParameters.SOUL_LURE_MAX_DISTANCE, SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS,
                SoulSkillParameters.PROFANE_SOUL_AOE_RADIUS, SoulSkillParameters.PROFANE_SOUL_AOE_DAMAGE));
    }
}
