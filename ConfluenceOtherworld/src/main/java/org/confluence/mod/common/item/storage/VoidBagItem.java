package org.confluence.mod.common.item.storage;

import com.google.common.collect.Iterables;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.confluence.mod.common.attachment.PlayerVoidVaultContainer;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.menu.VoidVaultMenu;

import java.util.List;

public class VoidBagItem extends CustomRarityItem implements IFunctionCouldEnable {
    public VoidBagItem() {
        super(new Item.Properties().stacksTo(1), ModRarity.GREEN);
    }

    @Override
    public Component getName(ItemStack stack) {
        return isEnabled(stack) ? super.getName(stack) : Component.translatable(getDescriptionId(stack) + ".disable");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            VoidVaultMenu.open(player, viewer -> !viewer.isRemoved() && viewer.isAlive() && hasBag(viewer));
            level.playSound(null, player.blockPosition(), ModSoundEvents.ITEM_VOID_BAG.get(), SoundSource.PLAYERS, 1, 1);
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static Iterable<ItemStack> accessibleItems(Player player) {
        return Iterables.concat(player.getInventory().items,
                hasOpenBag(player) ? PlayerVoidVaultContainer.of(player).getItems() : List.of());
    }

    public static boolean hasBag(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof VoidBagItem) return true;
        }
        return player.getOffhandItem().getItem() instanceof VoidBagItem;
    }

    public static boolean hasOpenBag(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof VoidBagItem bag && bag.isEnabled(stack)) return true;
        }
        return player.getOffhandItem().getItem() instanceof VoidBagItem bag && bag.isEnabled(player.getOffhandItem());
    }
}
