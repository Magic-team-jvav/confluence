package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.RainbowProjectile;

public class RainbowRodItem extends BaseDraggingStaffItem<RainbowProjectile> {
    public static final int COOLDOWN = 2;

    public RainbowRodItem() {
        super(ModRarity.PINK, RainbowProjectile::new, 30, 21, 18, COOLDOWN, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.rainbow_rod.0").withStyle(ChatFormatting.GRAY));
    }
}
