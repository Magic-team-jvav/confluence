package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.util.PlayerUtils;

/// 灵魂枪手：把当前灵魂按 1:1 换成灵魂子弹，子弹伤害更高且命中后概率回魂。
public class SoulGunnerSkill extends SoulSkill {
    public SoulGunnerSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.SOUL_GUNNER_BULLET_DAMAGE, Kind.ACTIVE, 0.0F, SoulSkillParameters.SOUL_GUNNER_COOLDOWN_TICKS);
    }

    /// 至少要 1 点灵魂才能开火。
    @Override
    public boolean canUse(ServerPlayer player, SoulSkillStack skillStack) {
        return player.isCreative() || PlayerUtils.getSoul(player) >= 1.0F;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        int bullets = (int) Math.min(SoulSkillParameters.SOUL_GUNNER_MAX_BULLETS, Math.floor(PlayerUtils.getSoul(serverPlayer)));
        if (bullets <= 0 || !PlayerUtils.consumeSoul(serverPlayer, bullets)) {
            return;
        }
        float damage = PlayerUtils.getSoulSkillDamage(player, skillStack) + SoulSkillParameters.SOUL_GUNNER_BULLET_DAMAGE;
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        RandomSource random = RandomSource.create();
        for (int i = 0; i < bullets; i++) {
            Vec3 direction = look.add((random.nextDouble() - 0.5) * 0.12, (random.nextDouble() - 0.5) * 0.12, (random.nextDouble() - 0.5) * 0.12);
            serverPlayer.level().addFreshEntity(new SoulLureProjectile(serverPlayer, eye, direction, damage, SoulLureProjectile.PARTICLE,
                    SoulSkillParameters.SOUL_LURE_SPEED, 0.0, SoulSkillParameters.SOUL_LURE_MAX_DISTANCE,
                    SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F));
            if (random.nextFloat() < SoulSkillParameters.SOUL_GUNNER_REFUND_CHANCE) {
                PlayerUtils.adjustSoul(player, 1.0F);
            }
        }
    }
}
