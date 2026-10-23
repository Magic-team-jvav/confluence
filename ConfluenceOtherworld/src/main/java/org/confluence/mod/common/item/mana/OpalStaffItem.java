package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.OpalStaffProjectile;

/// 欧泊法杖。
///
/// 魔耗固定，弹幕伤害在基础值之外额外加算施法者 50% 的近战攻击力，
/// 因此近战装备的加成也能带动法杖的输出。
public class OpalStaffItem extends ManaStaffItem<OpalStaffProjectile> {
    /// 近战攻击力折算成法杖伤害的比例。
    public static final float MELEE_DAMAGE_RATIO = 0.5F;

    public OpalStaffItem(ModRarity rarity, ProjectileFactory<OpalStaffProjectile> factory, float damage, int manaCost, float rawVelocity, int cooldown, double critChance) {
        super(rarity, factory, damage, manaCost, rawVelocity, cooldown, critChance);
        withTooltip(
                Component.translatable("tooltip.item.confluence.opal_staff.0").withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.item.confluence.opal_staff.1").withStyle(ChatFormatting.GRAY)
        );
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, OpalStaffProjectile projectile) {
        super.beforeShoot(player, stack, projectile);
        float melee = (float) player.getAttributeValue(LibAttributes.getAttackDamage());
        projectile.setDamage(damage + melee * MELEE_DAMAGE_RATIO);
    }
}
