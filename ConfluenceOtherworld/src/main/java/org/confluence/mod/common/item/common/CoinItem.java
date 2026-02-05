package org.confluence.mod.common.item.common;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class CoinItem extends BlockItem {
    public static final int UPGRADES_COUNT = 100;

    public final Supplier<CoinItem> upgrade;

    public CoinItem(Block block, ModRarity rarity, @Nullable Supplier<CoinItem> upgrade, int maxStackSize) {
        super(block, new Properties().fireResistant().stacksTo(maxStackSize).component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.upgrade = upgrade;
    }

    public static void onPickup(ItemStack itemStack, ItemEntity itemEntity) {
        if (itemStack.is(ModTags.Items.COINS)) {
            if (itemStack.is(ModItems.COPPER_COIN)) {
                itemEntity.playSound(ModSoundEvents.COINS_SMALL.get());
            } else if (itemStack.is(ModItems.SILVER_COIN)) {
                itemEntity.playSound(ModSoundEvents.COINS_MEDIUM.get());
            } else {
                itemEntity.playSound(ModSoundEvents.COINS_LARGE.get());
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isCrouching() && upgrade != null && stack.getCount() >= UPGRADES_COUNT) {
            if (level.isClientSide) {
                player.playSound(ModSoundEvents.TERRA_OPERATION.get());
            } else {
                if (!player.getInventory().add(upgrade.get().getDefaultInstance())) {
                    player.drop(upgrade.get().getDefaultInstance(), true);
                }
                stack.shrink(UPGRADES_COUNT);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isCrouching()) {
            if (context.getLevel().isClientSide) {
                player.playSound(ModSoundEvents.TERRA_OPERATION.get());
            } else {
                if (!player.getInventory().add(upgrade.get().getDefaultInstance())) {
                    player.drop(upgrade.get().getDefaultInstance(), true);
                }
                context.getItemInHand().shrink(UPGRADES_COUNT);
                if (context.getItemInHand().isEmpty()) {
                    player.setItemInHand(context.getHand(), ItemStack.EMPTY);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (upgrade != null && stack.getCount() >= UPGRADES_COUNT) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.coin.0").withColor(0xAAAAAA));
        }
    }
}
