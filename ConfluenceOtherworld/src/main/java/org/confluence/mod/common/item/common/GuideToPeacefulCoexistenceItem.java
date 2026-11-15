package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.confluence.mod.common.attachment.PlayerSpecialData;

public class GuideToPeacefulCoexistenceItem extends CustomRarityItem implements IFunctionCouldEnable {
    public GuideToPeacefulCoexistenceItem() {
        super(new Properties().stacksTo(1), ModRarity.GREEN);
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

    @Override
    public Component getName(ItemStack stack) {
        return isEnabled(stack) ? super.getName(stack) : Component.translatable(getDescriptionId(stack) + ".disable");
    }

}
