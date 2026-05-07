package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.FlamelashProjectile;

public class FlamelashItem extends BaseDraggingStaffItem<FlamelashProjectile> {
    public static final int COOLDOWN = 2;

    public FlamelashItem() {
        super(ModRarity.ORANGE, FlamelashProjectile::new, 21, 21, 12, COOLDOWN, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.flamelash.0").withStyle(ChatFormatting.GRAY));
    }
}
