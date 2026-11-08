package org.confluence.mod.common.item.mana;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModSoundEvents;

public class ArcaneCrystalItem extends CustomRarityItem {
    public ArcaneCrystalItem() {
        super(new Properties(), ModRarity.LIGHT_PURPLE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            // 已经用过：不消耗、不播声音
            if (!ManaStorage.of(player).setArcaneCrystalUsed()) return InteractionResultHolder.fail(itemStack);
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
