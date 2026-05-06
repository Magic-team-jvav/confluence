package org.confluence.mod.common.item.mana;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.mana.RainbowProjectile;

import java.util.List;

public class RainbowRodItem extends BaseDraggingStaffItem<RainbowProjectile> {
    public static final int COOLDOWN = 2;

    public RainbowRodItem() {
        super(ModRarity.PINK, RainbowProjectile::new, 30, 21, 18, COOLDOWN, 0.04);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.rainbow_rod.0"));
    }
}
