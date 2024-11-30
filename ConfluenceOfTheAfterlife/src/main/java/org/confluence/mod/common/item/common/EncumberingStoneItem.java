package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.mod.mixed.IServerPlayer;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.item.IFunctionCouldEnable;
import org.jetbrains.annotations.NotNull;

public class EncumberingStoneItem extends CustomRarityItem implements IFunctionCouldEnable {
    public EncumberingStoneItem() {
        super(new Properties().stacksTo(1), ModRarity.BLUE);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer serverPlayer) {
            ((IServerPlayer) serverPlayer).confluence$setCouldPickupItem(!isEnabled(stack, null));
        }
    }

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack pStack) {
        return isEnabled(pStack, null) ? super.getName(pStack) : withRarity(pStack, Component.translatable(getDescriptionId(pStack) + ".disable"));
    }
}
