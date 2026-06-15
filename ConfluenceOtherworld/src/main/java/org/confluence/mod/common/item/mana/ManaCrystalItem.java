package org.confluence.mod.common.item.mana;

import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
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
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.AchievementUtils;

public class ManaCrystalItem extends TooltipItem {
    public ManaCrystalItem() {
        super(new Properties().stacksTo(16), ModRarity.YELLOW, getTooltipsFromString("mana_crystal", 1, ChatFormatting.GREEN));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        level.playSound(player, player.blockPosition().above(), ModSoundEvents.MANA_STAR_USE.get(), SoundSource.PLAYERS, 1, 1);
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage manaStorage = ManaStorage.of(player);
            if (manaStorage.addStar()) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
                    itemStack.shrink(1);
                }
            }
            EverBeneficial everBeneficial;
            if (manaStorage.isStarMaximum() && (everBeneficial = EverBeneficial.of(serverPlayer)).isLifeCrystalsMaximum() && everBeneficial.isLifeFruitsMaximum()) {
                AchievementUtils.awardAchievement(serverPlayer, "topped_off");
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
