package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.MagicMissileProjectile;

public class MagicMissileItem extends BaseDraggingStaffItem<MagicMissileProjectile> {
    public static final int COOLDOWN = 2;

    public MagicMissileItem() {
        super(ModRarity.GREEN, MagicMissileProjectile::new, 22, 14, 12, COOLDOWN, 0.04);
        withTooltip(Component.translatable("tooltip.item.confluence.magic_missile.0").withStyle(ChatFormatting.GRAY));
    }
}
