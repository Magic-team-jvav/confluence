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
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.util.AchievementUtils;

public class ManaCrystalItem extends CustomRarityItem {
    public ManaCrystalItem() {
        super(new Properties().stacksTo(16), ModRarity.YELLOW);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            ManaStorage manaStorage = ManaStorage.of(player);
            // 已经到上限：不消耗、不播声音
            if (!manaStorage.addStar()) return InteractionResultHolder.fail(itemStack);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.MANA_STAR_USE.get(), SoundSource.PLAYERS, 1, 1);
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
            EverBeneficial everBeneficial;
            if (manaStorage.isStarMaximum() && (everBeneficial = EverBeneficial.of(serverPlayer)).isLifeCrystalsMaximum() && everBeneficial.isLifeFruitsMaximum()) {
                AchievementUtils.awardAchievement(serverPlayer, "topped_off");
            }
        }
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
