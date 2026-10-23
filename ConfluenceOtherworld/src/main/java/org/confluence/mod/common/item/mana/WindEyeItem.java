package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.WindEyeTornadoEntity;

/// 风眼：消耗 100 魔力制造一个持续 30 秒的大范围旋风。
///
/// 旋风会持续吸附敌人并造成持续伤害；往旋风中心打进其它弹幕，
/// 会让旋风吸收该弹幕的伤害。
public class WindEyeItem extends ManaStaffItem<WindEyeTornadoEntity> {
    public static final int MANA_COST = 100;
    /// 旋风的放置距离。
    private static final double PLACE_DISTANCE = 6.0;

    public WindEyeItem(ModRarity rarity) {
        super(rarity, WindEyeTornadoEntity::new, 1.0F, MANA_COST, 0.0F, 40, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.wind_eye.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, WindEyeTornadoEntity projectile) {
        Vec3 center = player.getEyePosition().add(player.getLookAngle().scale(PLACE_DISTANCE));
        projectile.setPos(center.x, center.y, center.z);
        projectile.setDamage(1.0F);
        projectile.setDefaultVelocity(0.0F);
        projectile.setOwner(player);
    }
}
