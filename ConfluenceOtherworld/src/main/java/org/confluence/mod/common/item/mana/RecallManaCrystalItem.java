package org.confluence.mod.common.item.mana;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.PlayerUtils;

/// 撤回一颗魔力水晶的永久增益，并同步新的魔力上限。
public class RecallManaCrystalItem extends CustomRarityItem {
    public RecallManaCrystalItem() {
        super(new Properties().stacksTo(16), ModRarity.GREEN);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.blockPosition().above(), ModSoundEvents.MANA_STAR_USE.get(), SoundSource.PLAYERS, 1, 1);
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage data = ManaStorage.of(player);
            if (data.decreaseStar()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                PlayerUtils.syncMana2Client(serverPlayer, data);
                if (!player.hasInfiniteMaterials()) {
                    itemStack.shrink(1);
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
