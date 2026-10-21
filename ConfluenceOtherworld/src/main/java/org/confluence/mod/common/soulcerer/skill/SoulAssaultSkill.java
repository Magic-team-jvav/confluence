package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.soulcerer.SoulLureProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.util.PlayerUtils;

import java.util.List;

/// 灵魂突击：施放后获得限时增益，增益期间每隔固定刻数把灵魂按 1:1 换成灵魂子弹，
/// 子弹伤害更高且命中后概率回魂。
///
/// 自动开火由 `PlayerUtils.tickSoulSkillStates` 推进，技能本身只负责挂增益。
public class SoulAssaultSkill extends SoulSkill {
    public SoulAssaultSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, 0.0F, Kind.BUFF,
                SoulSkillParameters.SOUL_ASSAULT_SOUL_COST, SoulSkillParameters.SOUL_ASSAULT_COOLDOWN_TICKS);
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.addEffect(new MobEffectInstance(ModEffects.SOUL_ASSAULT, SoulSkillParameters.SOUL_ASSAULT_DURATION_TICKS, 0, false, true, true));
        }
    }

    @Override
    public List<BuffDuration> getBuffDurations(SoulSkillStack skillStack) {
        return List.of(new BuffDuration(Component.translatable("effect.confluence.soul_assault"), SoulSkillParameters.SOUL_ASSAULT_DURATION_TICKS));
    }

    @Override
    public boolean isBuffActive(Player player) {
        return player.hasEffect(ModEffects.SOUL_ASSAULT);
    }

    /// 增益期间的一次自动开火：至少要 1 点灵魂才能把灵魂转成子弹。
    public static void fireBurst(ServerPlayer player) {
        int bullets = (int) Math.min(SoulSkillParameters.SOUL_ASSAULT_MAX_BULLETS, Math.floor(PlayerUtils.getSoul(player)));
        if (bullets <= 0 || !PlayerUtils.consumeSoul(player, bullets)) {
            return;
        }
        /// 武器部分用施法时的快照：增益期间换手/空手不会削弱已经点亮的子弹。
        float damage = PlayerUtils.getCastSoulSkillDamage(player, ModSoulSkills.SOUL_ASSAULT.get())
                + SoulSkillParameters.SOUL_ASSAULT_BULLET_DAMAGE;
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        RandomSource random = player.getRandom();
        for (int i = 0; i < bullets; i++) {
            Vec3 direction = look.add((random.nextDouble() - 0.5) * 0.12, (random.nextDouble() - 0.5) * 0.12, (random.nextDouble() - 0.5) * 0.12);
            player.level().addFreshEntity(new SoulLureProjectile(player, eye, direction, damage, SoulLureProjectile.NO_PARTICLE,
                    SoulSkillParameters.SOUL_LURE_SPEED, 0.0, SoulSkillParameters.SOUL_LURE_MAX_DISTANCE,
                    SoulSkillParameters.SOUL_LURE_MAX_AGE_TICKS, 0.0, 0.0F));
            if (random.nextFloat() < SoulSkillParameters.SOUL_ASSAULT_REFUND_CHANCE) {
                PlayerUtils.adjustSoul(player, 1.0F);
            }
        }
    }
}
