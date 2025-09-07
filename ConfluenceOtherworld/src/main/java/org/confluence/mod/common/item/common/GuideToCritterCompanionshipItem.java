package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.FunctionItem;
import org.confluence.mod.common.attachment.PlayerSpecialData;

public class GuideToCritterCompanionshipItem extends FunctionItem {
    public GuideToCritterCompanionshipItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE, getTooltipsFromString("guide_to_critter_companionship", 2, ChatFormatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            PlayerSpecialData.of(player).setCouldHurtCritters(!isEnabled(stack));
        }
    }
}
