package org.confluence.mod.common.item.mana;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.CustomRarityItem;
import org.confluence.terra_curio.common.component.ModRarity;

public class ManaStarItem extends CustomRarityItem {
    public ManaStarItem() {
        super(new Properties().stacksTo(16), ModRarity.YELLOW);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.getOnPos().above(), ModSoundEvents.MANA_STAR_USE.get(), SoundSource.PLAYERS, 1, 1);
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage data1 = serverPlayer.getData(ModAttachmentTypes.MANA_STORAGE);
            if (data1.addStar()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                itemStack.shrink(1);
            }
            EverBeneficial data = serverPlayer.getData(ModAttachmentTypes.EVER_BENEFICIAL);
            if (data1.isStarMaximum() && data.isLifeCrystalsMaximum() && data.isLifeFruitsMaximum()) {
                AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(Confluence.asResource("achievements/topped_off"));
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement, "never");
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
