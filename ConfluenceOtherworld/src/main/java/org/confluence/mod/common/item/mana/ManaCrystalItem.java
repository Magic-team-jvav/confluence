package org.confluence.mod.common.item.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.AchievementUtils;

public class ManaCrystalItem extends TooltipItem {
    public ManaCrystalItem() {
        super(new Properties().stacksTo(16), ModRarity.YELLOW, getTooltipsFromString("mana_crystal", 1, ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.getOnPos().above(), ModSoundEvents.MANA_STAR_USE.get(), SoundSource.PLAYERS, 1, 1);
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage data1 = ManaStorage.of(player);
            if (data1.addStar()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                itemStack.shrink(1);
            }
            EverBeneficial data;
            if (data1.isStarMaximum() && (data = serverPlayer.getData(ModAttachmentTypes.EVER_BENEFICIAL)).isLifeCrystalsMaximum() && data.isLifeFruitsMaximum()) {
                AchievementUtils.awardAchievement(serverPlayer, "topped_off");
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
