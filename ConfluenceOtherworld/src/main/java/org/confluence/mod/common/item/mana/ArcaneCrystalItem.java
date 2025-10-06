package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModSoundEvents;

public class ArcaneCrystalItem extends TooltipItem {
    public ArcaneCrystalItem() {
        super(new Properties(), ModRarity.LIGHT_PURPLE, getTooltipsFromString("arcane_crystal", 1, ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
        }
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (ManaStorage.of(player).setArcaneCrystalUsed()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
