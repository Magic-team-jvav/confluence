package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.DuskDawnProjectile;

/// 昏晓：法师武器。
///
/// 20 魔耗、30 基础伤害；弹幕会追踪并吸附敌人，持续扩散逐圈衰减的光波，
/// 光波放完后爆炸并释放一道强力横向斩击。
public class DuskDawnItem extends ManaStaffItem<DuskDawnProjectile> {
    public static final float BASE_DAMAGE = 30.0F;
    public static final int MANA_COST = 20;

    public DuskDawnItem(ModRarity rarity) {
        super(rarity, DuskDawnProjectile::new, BASE_DAMAGE, MANA_COST, 6.0F, 16, 0.04);
        withTooltip(
                Component.translatable("tooltip.item.confluence.dusk_dawn.0").withStyle(ChatFormatting.GRAY),
                Component.translatable("tooltip.item.confluence.dusk_dawn.1").withStyle(ChatFormatting.GRAY)
        );
    }
}
