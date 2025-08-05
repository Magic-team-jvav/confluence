package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.color.GlobalColors;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModSoundEvents;

public class PeddlersSatchelItem extends TooltipItem {
    public PeddlersSatchelItem() {
        super(new Properties(), ModRarity.LIGHT_PURPLE, Component.translatable("tooltip.item.confluence.peddlers_satchel.0").withStyle(ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (level instanceof ServerLevel serverLevel) {
            if (!NPCSpawner.INSTANCE.isPeddlersSatchelUsed()) {
                NPCSpawner.INSTANCE.setPeddlersSatchelUsed(true);
                MutableComponent component = Component.translatable("message.confluence.peddlers_satchel").withColor(GlobalColors.MESSAGE.get());
                for (ServerPlayer serverPlayer : serverLevel.getServer().getPlayerList().getPlayers()) {
                    serverPlayer.sendSystemMessage(component);
                }
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        } else {
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
