package org.confluence.mod.common.soulcerer.skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.soulcerer.SoulStingProjectile;
import org.confluence.mod.util.PlayerUtils;

/// 自然之惩戒：以扇形齐射多枚毒刺弹幕（外观复用黄蜂毒刺的模型与贴图）。
public class NaturesWrathSkill extends SoulSkill {
    public NaturesWrathSkill(ResourceLocation icon, ResourceLocation id) {
        super(icon, id, SoulSkillParameters.NATURES_WRATH_DAMAGE, Kind.ACTIVE,
                SoulSkillParameters.NATURES_WRATH_SOUL_COST, SoulSkillParameters.NATURES_WRATH_COOLDOWN_TICKS);
    }

    /// 与迷魂孢雾同一套口径：武器伤害只按 60% 计入，技能说明的实时伤害也走同一比例。
    @Override
    public float getWeaponShare() {
        return SoulSkillParameters.NATURES_WRATH_WEAPON_SHARE;
    }

    @Override
    public void use(Player player, SoulSkillStack skillStack) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        float weaponDamage = PlayerUtils.getWeaponSoulDamage(player) * getWeaponShare();
        float damage = PlayerUtils.getSoulSkillDamage(player, skillStack, weaponDamage);
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        int count = SoulSkillParameters.NATURES_WRATH_COUNT;
        for (int i = 0; i < count; i++) {
            // 以视线为中心左右均分张开约 24 度
            double angle = (i - (count - 1) / 2.0) * 0.12;
            Vec3 direction = look.yRot((float) angle).normalize();
            serverPlayer.level().addFreshEntity(new SoulStingProjectile(serverPlayer, eye, direction, damage, SoulSkillParameters.NATURES_WRATH_SPEED));
        }
    }
}
