package org.confluence.mod.common.item.mana;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.CustomRarityItem;

public class ArcaneCrystalItem extends CustomRarityItem {
    public ArcaneCrystalItem() {
        super(ModRarity.LIGHT_PURPLE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide) {
            player.playSound(ModSoundEvents.TRANSMUTATION_USE.get());
        }
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage manaStorage = player.getData(ModAttachmentTypes.MANA_STORAGE);
            if (manaStorage.setArcaneCrystalUsed()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                itemStack.shrink(1);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
