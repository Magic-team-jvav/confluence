package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.FunctionItem;
import org.confluence.mod.common.attachment.PlayerSpecialData;

public class GuideToPeacefulCoexistenceItem extends FunctionItem {
    public GuideToPeacefulCoexistenceItem() {
        super(new Properties().stacksTo(1), ModRarity.GREEN, getTooltipsFromString("guide_to_peaceful_coexistence", 3, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            PlayerSpecialData data = PlayerSpecialData.of(player);
            boolean b = !isEnabled(stack);
            data.setCouldHurtCritters(b);
            data.setCouldDamageEnvironment(b);
        }
    }
}
